package gov.nist.csd.pm.pap.serialization.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.pap.query.AccessQuery;
import gov.nist.csd.pm.pap.serialization.PolicyDeserializer;
import gov.nist.csd.pm.pap.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.statement.VariableDeclarationStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;

import java.util.*;

import static gov.nist.csd.pm.common.graph.node.NodeType.*;

public class JSONDeserializer implements PolicyDeserializer {

    public static void main(String[] args) throws PMException {
        MemoryPAP pap = new MemoryPAP();

        JSONDeserializer jsonDeserializer = new JSONDeserializer();
        jsonDeserializer.deserialize(pap, new UserContext(""), """
                {
                	"resourceAccessRights": ["read", "write"],
                	"graph": {
                		"pcs": [
                			{
                				"name": "pc1",
                				"properties": {}
                			}
                		],
                		"uas": [
                			{
                				"name": "ua1",
                				"properties": {},
                				"assignments": ["pc1"],
                				"associations": {
                					"oa1": ["read", "write"]
                				}
                			}
                		],
                		"oas": [
                			{
                				"name": "oa1",
                				"properties": {},
                				"assignments": ["pc1"]
                			}
                		],
                		"users": [
                			{
                				"name": "u1",
                				"properties": {},
                				"assignments": ["ua1"]
                			}
                		],
                		"objects": [
                			{
                				"name": "o1",
                				"properties": {},
                				"assignments": ["oa1"]
                			}
                		]
                	},
                	"pml": {
                		"constants": [
                			{
                				"name": "const1",
                				"value": "123"
                			}
                		],
                		"functions": [
                			"create function func1() {}"
                		]
                	}
                }
                """);
        System.out.println(pap.query().graph().search(ANY, new HashMap<>()));
    }


    private FunctionDefinitionStatement[] customPMLFunctions;

    public JSONDeserializer(FunctionDefinitionStatement... customPMLFunctions) {
        this.customPMLFunctions = customPMLFunctions;
    }

    public void setCustomPMLFunctions(FunctionDefinitionStatement[] customPMLFunctions) {
        this.customPMLFunctions = customPMLFunctions;
    }

    @Override
    public void deserialize(PAP pap, UserContext author, String input) throws PMException {
        Gson gson = new Gson();
        JSONPolicy jsonPolicy = gson.fromJson(input, new TypeToken<JSONPolicy>() {}.getType());

        pap.modify().graph().setResourceAccessRights(jsonPolicy.getResourceAccessRights());

        createUserDefinedPML(pap, author, customPMLFunctions, jsonPolicy.getUserDefinedPML());
        createGraph(pap, jsonPolicy.getGraph());
        createProhibitionsAndObligations(pap, author, customPMLFunctions, jsonPolicy.getProhibitions(), jsonPolicy.getObligations());
    }

    private void createUserDefinedPML(PAP pap, UserContext author,
                                      FunctionDefinitionStatement[] customPMLFunctions,
                                      JSONUserDefinedPML userDefinedPML)
            throws PMException {
        if (userDefinedPML == null) {
            return;
        }

        // to apply the constants and functions to the policy, create a PML string and execute it on the policy
        // this will allow all function signatures to be compiled before the function bodies in the case of functions
        // calling other functions
        StringBuilder pml = new StringBuilder();
        VisitorContext visitorCtx = new VisitorContext(new Scope<>(GlobalScope.forCompile(pap, customPMLFunctions)));

        Map<String, String> constants = userDefinedPML.getConstants();
        if (constants != null){
            List<VariableDeclarationStatement.Declaration> constDecs = new ArrayList<>();
            for (Map.Entry<String, String> e : constants.entrySet()) {
                Expression expression = Expression.fromString(visitorCtx, e.getValue(), Type.any());
                constDecs.add(new VariableDeclarationStatement.Declaration(e.getKey(), expression));
            }
            pml.append(new VariableDeclarationStatement(true, constDecs)).append("\n");
        }

        Map<String, String> functions = userDefinedPML.getFunctions();
        if (functions != null) {
            for (Map.Entry<String, String> e : functions.entrySet()) {
                pml.append(e.getValue()).append("\n");
            }
        }

        PMLDeserializer pmlDeserializer = new PMLDeserializer(customPMLFunctions);
        pmlDeserializer.deserialize(pap, author, pml.toString());
    }

    private void createProhibitionsAndObligations(PAP pap,
                                                  UserContext author,
                                                  FunctionDefinitionStatement[] customPMLFunctions,
                                                  List<String> obligations,
                                                  List<String> prohibitions) throws PMException {
        PMLDeserializer pmlDeserializer = new PMLDeserializer(customPMLFunctions);

        if (prohibitions != null) {
            for (String prohibitionStr : prohibitions) {
                pmlDeserializer.deserialize(pap, author, prohibitionStr);
            }
        }

        if (obligations != null) {
            for (String obligationStr : obligations) {
                pmlDeserializer.deserialize(pap, author, obligationStr);
            }
        }
    }

    private void createGraph(PAP pap, JSONGraph graph)
            throws PMException {
        if (graph.pcs == null) {
            return;
        }

        // create all policy class nodes first
        for (JSONPolicyClass policyClass : graph.pcs) {
            pap.modify().graph().createPolicyClass(policyClass.getName(), policyClass.getProperties());
        }

        Map<String, Map<String, AccessRightSet>> assocs = createUserAttributes(pap, graph.uas);
        createObjectAttributes(pap, graph.oas);

        for (Map.Entry<String, Map<String, AccessRightSet>> entry : assocs.entrySet()) {
            for (Map.Entry<String, AccessRightSet> target : entry.getValue().entrySet()) {
                pap.modify().graph().associate(entry.getKey(), target.getKey(), target.getValue());
            }
        }

        createUserOrObjects(pap, graph.users, U);
        createUserOrObjects(pap, graph.objects, O);
    }

    private Map<String, Map<String, AccessRightSet>> createUserAttributes(PAP pap, List<JSONUserAttribute> uas) throws PMException {
        Map<String, List<String>> waitingAssignments = new HashMap<>();
        Map<String, Map<String, AccessRightSet>> associations = new HashMap<>();

        if (uas == null) {
            return new HashMap<>();
        }

        int i = 0;
        while (!uas.isEmpty()) {
            JSONUserAttribute ua = uas.get(i);

            // determine the existing nodes to initally assign the node to
            List<String> existingAssignmentNodes = new ArrayList<>();
            for (String end : ua.getAssignments()) {
                if (pap.query().graph().nodeExists(end)) {
                    existingAssignmentNodes.add(end);
                } else {
                    List<String> waitingAssignmentsForEnd = waitingAssignments.getOrDefault(end, new ArrayList<>());
                    waitingAssignmentsForEnd.add(ua.getName());
                    waitingAssignments.put(end, waitingAssignmentsForEnd);
                }
            }

            // create node
            pap.modify().graph().createUserAttribute(ua.getName(), ua.getProperties(), existingAssignmentNodes);

            // once created, check if any other nodes were waiting on it and assign
            List<String> waitingAssignmentsForUA = waitingAssignments.getOrDefault(ua.getName(), new ArrayList<>());
            for (String waiting : waitingAssignmentsForUA) {
                pap.modify().graph().assign(waiting, ua.getName());
            }
            waitingAssignments.remove(ua.getName());

            // store associations
            associations.put(ua.getName(), ua.getAssociations());

            uas.remove(i);

            if (i == uas.size()-1) {
                i = 0;
            } else {
                i++;
            }
        }

        return associations;
    }


    private void createObjectAttributes(PAP pap, List<JSONObjectAttribute> oas) throws PMException {
        Map<String, List<String>> waitingAssignments = new HashMap<>();

        if (oas == null) {
            return;
        }

        int i = 0;
        while (!oas.isEmpty()) {
            JSONObjectAttribute oa = oas.get(i);

            // determine the existing nodes to initally assign the node to
            List<String> existingAssignmentNodes = new ArrayList<>();
            for (String end : oa.getAssignments()) {
                if (pap.query().graph().nodeExists(end)) {
                    existingAssignmentNodes.add(end);
                } else {
                    List<String> waitingAssignmentsForEnd = waitingAssignments.getOrDefault(end, new ArrayList<>());
                    waitingAssignmentsForEnd.add(oa.getName());
                    waitingAssignments.put(end, waitingAssignmentsForEnd);
                }
            }

            // create node
            pap.modify().graph().createObjectAttribute(oa.getName(), oa.getProperties(), existingAssignmentNodes);

            // once created, check if any other nodes were waiting on it and assign
            List<String> waitingAssignmentsForUA = waitingAssignments.getOrDefault(oa.getName(), new ArrayList<>());
            for (String waiting : waitingAssignmentsForUA) {
                pap.modify().graph().assign(waiting, oa.getName());
            }

            waitingAssignments.remove(oa.getName());
            oas.remove(i);

            if (i == oas.size() - 1) {
                i = 0;
            } else {
                i++;
            }
        }
    }

    private void createUserOrObjects(PAP pap, List<JSONUserOrObject> usersOrObjects, NodeType type) throws PMException {
        for (JSONUserOrObject userOrObject : usersOrObjects) {
            Collection<String> parents = userOrObject.getAssignments();
            if (parents.isEmpty()) {
                throw new PMException("node " + userOrObject.getName() + " does not have any parents");
            }

            if (type == U) {
                pap.modify().graph().createUser(userOrObject.getName(), userOrObject.getProperties(), parents);
            } else {
                pap.modify().graph().createObject(userOrObject.getName(), userOrObject.getProperties(), parents);
            }
        }
    }
}
