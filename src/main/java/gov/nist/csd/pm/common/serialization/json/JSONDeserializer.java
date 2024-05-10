package gov.nist.csd.pm.common.serialization.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gov.nist.csd.pm.common.serialization.PolicyDeserializer;
import gov.nist.csd.pm.common.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.statement.VariableDeclarationStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        createUserDefinedPML(pap, author, customPMLFunctions, jsonPolicy.getUserDefinedPML());
        createGraph(pap, jsonPolicy.getGraph());
        createProhibitions(pap, jsonPolicy.getProhibitions());
        createObligations(pap, author, customPMLFunctions, jsonPolicy.getObligations());
    }

    private void createUserDefinedPML(PAP pap, UserContext author,
                                      FunctionDefinitionStatement[] customPMLFunctions,
                                      JSONUserDefinedPML userDefinedPML)
            throws PMException {
        // to apply the constants and functions to the policy, create a PML string and execute it on the policy
        // this will allow all function signatures to be compiled before the function bodies in the case of functions
        // calling other functions
        StringBuilder pml = new StringBuilder();
        VisitorContext visitorCtx = new VisitorContext(new Scope<>(GlobalScope.forCompile(pap, customPMLFunctions)));

        Map<String, String> constants = userDefinedPML.getConstants();
        List<VariableDeclarationStatement.Declaration> constDecs = new ArrayList<>();
        for (Map.Entry<String, String> e : constants.entrySet()) {
            Expression expression = Expression.fromString(visitorCtx, e.getValue(), Type.any());
            constDecs.add(new VariableDeclarationStatement.Declaration(e.getKey(), expression));
        }
        pml.append(new VariableDeclarationStatement(true, constDecs)).append("\n");

        Map<String, String> functions = userDefinedPML.getFunctions();
        for (Map.Entry<String, String> e : functions.entrySet()) {
            pml.append(e.getValue()).append("\n");
        }

        PMLDeserializer pmlDeserializer = new PMLDeserializer(customPMLFunctions);
        pmlDeserializer.deserialize(pap, author, pml.toString());
    }

    private void createObligations(PAP pap, UserContext author,
                                   FunctionDefinitionStatement[] customPMLFunctions, List<String> obligations)
            throws PMException {
        for (String obligationStr : obligations) {
            PMLDeserializer pmlDeserializer = new PMLDeserializer(customPMLFunctions);
            pmlDeserializer.deserialize(pap, author, obligationStr);
        }
    }

    private void createProhibitions(PAP pap, List<Prohibition> prohibitions)
            throws PMException {
        for (Prohibition prohibition : prohibitions) {
            pap.modify().prohibitions().create(
                    prohibition.getName(),
                    prohibition.getSubject(),
                    prohibition.getAccessRightSet(),
                    prohibition.isIntersection(),
                    prohibition.getContainers().toArray(new ContainerCondition[0])
            );
        }
    }

    private void createGraph(PAP pap, JSONGraph graph)
            throws PMException {
        if (graph.resourceAccessRights != null) {
            pap.modify().graph().setResourceAccessRights(graph.resourceAccessRights);
        }

        if (graph.policyClasses == null) {
            return;
        }

        // create all policy class nodes first
        for (JSONPolicyClass policyClass : graph.policyClasses) {
            pap.modify().graph().createPolicyClass(policyClass.getName(), policyClass.getProperties());
        }

        // create policy class attribute hierarchies
        for (JSONPolicyClass policyClass : graph.policyClasses) {
            createPolicyClass(pap, policyClass);
        }

        createUserOrObjects(pap, graph.users, U);
        createUserOrObjects(pap, graph.objects, O);
    }

    private void createUserOrObjects(PAP pap, List<JSONUserOrObject> usersOrObjects, NodeType type) throws PMException {
        for (JSONUserOrObject userOrObject : usersOrObjects) {
            List<String> parents = userOrObject.getParents();
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

    private void createPolicyClass(PAP pap, JSONPolicyClass policyClass)
            throws PMException {
        String name = policyClass.getName();
        Map<String, String> properties = policyClass.getProperties();
        List<JSONNode> userAttributes = policyClass.getUserAttributes();
        List<JSONNode> objectAttributes = policyClass.getObjectAttributes();

        // create policy class node
        if (!pap.query().graph().nodeExists(name)) {
            pap.modify().graph().createPolicyClass(name, properties == null ? new HashMap<>() : properties);
        }

        if (userAttributes != null) {
            createAttributes(pap, UA, name, userAttributes);
        }

        if (objectAttributes != null) {
            createAttributes(pap, OA, name, objectAttributes);
        }

        Map<String, List<JSONAssociation>> associations = policyClass.getAssociations();
        if (associations != null) {
            for (Map.Entry<String, List<JSONAssociation>> e : associations.entrySet()) {
                for (JSONAssociation jsonAssociation : e.getValue()) {
                    pap.modify().graph().associate(e.getKey(), jsonAssociation.getTarget(), jsonAssociation.getArset());
                }
            }
        }
    }

    private void createAttributes(PAP pap, NodeType type, String parent, List<JSONNode> attrs)
            throws PMException {
        if (attrs == null) {
            return;
        }

        for (JSONNode attr : attrs) {
            String name = attr.getName();
            if (pap.query().graph().nodeExists(name)) {
                pap.modify().graph().assign(attr.getName(), parent);
            } else {
                Map<String, String> properties = attr.getProperties() == null ? new HashMap<>() : attr.getProperties();
                if (type == UA) {
                    pap.modify().graph().createUserAttribute(name, properties, List.of(parent));
                } else {
                    pap.modify().graph().createObjectAttribute(name, properties, List.of(parent));
                }
            }

            createAttributes(pap, type, name, attr.getChildren());
        }
    }
}
