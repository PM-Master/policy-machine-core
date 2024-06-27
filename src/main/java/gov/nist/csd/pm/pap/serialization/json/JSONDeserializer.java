package gov.nist.csd.pm.pap.serialization.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
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
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;

import java.util.*;

import static gov.nist.csd.pm.common.graph.node.NodeType.*;

public class JSONDeserializer implements PolicyDeserializer {

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

        createGraph(pap, jsonPolicy.getGraph());
        createProhibitionsAndObligations(pap, author, customPMLFunctions, jsonPolicy.getProhibitions(), jsonPolicy.getObligations());
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
        for (Map.Entry<String, JSONPolicyClass> policyClass : graph.pcs.entrySet()) {
            Map<String, String> properties = policyClass.getValue().getProperties();
            if (properties == null) {
                properties = new HashMap<>();
            }

            pap.modify().graph().createPolicyClass(policyClass.getKey(), properties);
        }

        // create uas
        Map<String, Map<String, AccessRightSet>> assocs = createNodes(pap, UA, graph.uas);

        // create oas
        createNodes(pap, OA, graph.oas);

        // associate uas and uas/oas
        for (Map.Entry<String, Map<String, AccessRightSet>> entry : assocs.entrySet()) {
            for (Map.Entry<String, AccessRightSet> target : entry.getValue().entrySet()) {
                pap.modify().graph().associate(entry.getKey(), target.getKey(), target.getValue());
            }
        }

        // create u and o
        createNodes(pap, U, graph.users);
        createNodes(pap, O, graph.objects);
    }

    private Map<String, Map<String, AccessRightSet>> createNodes(PAP pap, NodeType type, Map<String, JSONNode> nodes)
            throws PMException {
        if (nodes == null) {
            return new HashMap<>();
        }

        Map<String, List<String>> waitingAssignments = new HashMap<>();
        Map<String, Map<String, AccessRightSet>> associations = new HashMap<>();

        Iterator<Map.Entry<String, JSONNode>> iterator = nodes.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, JSONNode> entry = iterator.next();

            String key = entry.getKey();
            JSONNode value = entry.getValue();

            // determine the existing nodes to initially assign the node to
            List<String> existingAssignmentNodes = new ArrayList<>();
            for (String end : value.getAssignments()) {
                if (pap.query().graph().nodeExists(end)) {
                    existingAssignmentNodes.add(end);
                } else {
                    List<String> waitingAssignmentsForEnd = waitingAssignments.getOrDefault(end, new ArrayList<>());
                    waitingAssignmentsForEnd.add(key);
                    waitingAssignments.put(end, waitingAssignmentsForEnd);
                }
            }

            if (existingAssignmentNodes.isEmpty()) {
                continue;
            }

            // create node or assign to descs if already exists (in the case of admin nodes)
            if (!pap.query().graph().nodeExists(key)) {
                createNode(pap, type, key, value, existingAssignmentNodes);
            } else {
                assignNode(pap, type, key, value, existingAssignmentNodes);
            }

            iterator.remove();

            // once created, check if any other nodes were waiting on it and assign
            List<String> waitingAssignmentsForUA = waitingAssignments.getOrDefault(key, new ArrayList<>());
            for (String waiting : waitingAssignmentsForUA) {
                if (!pap.query().graph().nodeExists(waiting)) {
                    createNode(pap, type, waiting, nodes.get(waiting), List.of(key));
                } else {
                    pap.modify().graph().assign(waiting, key);
                }
            }

            waitingAssignments.remove(key);

            // store associations
            if (value.getAssociations() != null) {
                associations.put(key, value.getAssociations());
            }
        }

        return associations;
    }

    private void assignNode(PAP pap, NodeType type, String key, JSONNode value, List<String> existingAssignmentNodes)
            throws PMException {
        Collection<String> assigns = existingAssignmentNodes;

        // if O or U, it is assumed all attrs are already created and we dont need to worry about a node not existing yet
        if (type == O || type == U) {
            assigns = value.getAssignments();
        }

        for (String assign : assigns) {
            pap.modify().graph().assign(key, assign);
        }
    }

    private void createNode(PAP pap, NodeType type, String key, JSONNode value, List<String> existingAssignmentNodes)
            throws PMException {
        switch (type) {
            case OA -> pap.modify().graph().createObjectAttribute(key, value.getProperties(), existingAssignmentNodes);
            case UA -> pap.modify().graph().createUserAttribute(key, value.getProperties(), existingAssignmentNodes);
            case O -> pap.modify().graph().createObject(key, value.getProperties(), value.getAssignments());
            case U -> pap.modify().graph().createUser(key, value.getProperties(), value.getAssignments());
        }
    }
}
