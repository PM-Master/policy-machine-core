package gov.nist.csd.pm.common.serialization.json;

import gov.nist.csd.pm.pap.AdminPolicy;
import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.common.serialization.PolicySerializer;
import gov.nist.csd.pm.pap.query.PolicyQuery;

import java.util.*;

import static gov.nist.csd.pm.common.graph.node.NodeType.*;
import static gov.nist.csd.pm.common.graph.node.Properties.NO_PROPERTIES;

public class JSONSerializer implements PolicySerializer {

    private Map<Association, List<String>> delayedAssociations;
    private Set<String> createdPCs;
    private Set<String> createdAttrs;

    public JSONSerializer() {}

    @Override
    public String serialize(PolicyQuery policyQuery) throws PMException {
        return buildJSONPolicy(policyQuery)
                .toString();
    }

    public JSONPolicy buildJSONPolicy(PolicyQuery policyQuery) throws PMException {
        resetBuild(policyQuery);

        return new JSONPolicy(
                buildGraphJSON(policyQuery),
                buildProhibitionsJSON(policyQuery),
                buildObligationsJSON(policyQuery),
                buildUserDefinedPML(policyQuery)
        );
    }

    private void resetBuild(PolicyQuery policyQuery) throws PMException {
        this.delayedAssociations = new HashMap<>();
        this.createdPCs = new HashSet<>();
        this.createdAttrs = new HashSet<>();
    }


    private JSONUserDefinedPML buildUserDefinedPML(PolicyQuery policyQuery) throws PMException {
        Map<String, FunctionDefinitionStatement> functions = policyQuery.pml().getFunctions();
        Map<String, String> jsonFunctions = new HashMap<>();
        for (Map.Entry<String, FunctionDefinitionStatement> e : functions.entrySet()) {
            jsonFunctions.put(e.getKey(), e.getValue().toString());
        }

        Map<String, Value> constants = policyQuery.pml().getConstants();
        Map<String, String> jsonConstants = new HashMap<>();
        for (Map.Entry<String, Value> e : constants.entrySet()) {
            if (AdminPolicy.isAdminPolicyNodeConstantName(e.getKey())) {
                continue;
            }

            jsonConstants.put(e.getKey(), e.getValue().toString());
        }

        return new JSONUserDefinedPML(jsonFunctions, jsonConstants);
    }

    private List<String> buildObligationsJSON(PolicyQuery policyQuery) throws PMException {
        List<String> jsonObligations = new ArrayList<>();
        Collection<Obligation> all = policyQuery.obligations().getAll();
        for (Obligation obligation : all) {
            jsonObligations.add(obligation.toString());
        }

        return jsonObligations;
    }

    private List<Prohibition> buildProhibitionsJSON(PolicyQuery policyQuery) throws PMException {
        List<Prohibition> prohibitions = new ArrayList<>();
        Map<String, Collection<Prohibition>> all = policyQuery.prohibitions().getAll();
        for (Collection<Prohibition> value : all.values()) {
            prohibitions.addAll(value);
        }

        return prohibitions;
    }

    private JSONGraph buildGraphJSON(PolicyQuery policyQuery) throws PMException {
        return new JSONGraph(
                policyQuery.graph().getResourceAccessRights(),
                buildPolicyClasses(policyQuery),
                buildUsersOrObjects(policyQuery, U),
                buildUsersOrObjects(policyQuery, O)
        );
    }

    private List<JSONUserOrObject> buildUsersOrObjects(PolicyQuery policyQuery, NodeType type)
            throws PMException {
        List<JSONUserOrObject> userOrObjects = new ArrayList<>();

        Collection<String> search = policyQuery.graph().search(type, NO_PROPERTIES);
        for (String userOrObject : search) {
            JSONUserOrObject jsonUserOrObject = new JSONUserOrObject();
            jsonUserOrObject.setName(userOrObject);

            Node node = policyQuery.graph().getNode(userOrObject);
            if (!node.getProperties().isEmpty()) {
                jsonUserOrObject.setProperties(node.getProperties());
            }

            jsonUserOrObject.setParents(policyQuery.graph().getParents(userOrObject));

            userOrObjects.add(jsonUserOrObject);
        }

        return userOrObjects;
    }

    private List<JSONPolicyClass> buildPolicyClasses(PolicyQuery policyQuery) throws PMException {
        List<JSONPolicyClass> policyClassesList = new ArrayList<>();

        Collection<String> policyClasses = policyQuery.graph().getPolicyClasses();
        for (String pc : policyClasses) {
            JSONPolicyClass jsonPolicyClass = buildJSONPolicyCLass(pc, policyQuery);

            createdPCs.add(pc);

            // ignore the creation of the admin policy class - it is done automatically
            if (jsonPolicyClass.getName().equals(AdminPolicyNode.ADMIN_POLICY.nodeName())) {
                continue;
            }

            policyClassesList.add(jsonPolicyClass);
        }

        return policyClassesList;
    }

    private JSONPolicyClass buildJSONPolicyCLass(String pc, PolicyQuery policyQuery) throws PMException {
        List<Association> associations = new ArrayList<>();

        // uas
        List<JSONNode> userAttributes = getAttributes(pc, UA, associations, policyQuery);

        // oas
        List<JSONNode> objectAttributes = getAttributes(pc, OA, associations, policyQuery);

        // associations
        Map<String, List<JSONAssociation>> jsonAssociations = new HashMap<>();
        for (Association association : associations) {
            List<String> waitingFor = delayedAssociations.getOrDefault(association, new ArrayList<>());
            waitingFor.removeAll(createdAttrs);

            if (waitingFor.isEmpty()) {
                List<JSONAssociation> nodeAssociations = jsonAssociations.getOrDefault(association.getSource(), new ArrayList<>());
                nodeAssociations.add(new JSONAssociation(association.getTarget(), association.getAccessRightSet()));
                jsonAssociations.put(association.getSource(), nodeAssociations);

                delayedAssociations.remove(association);
            } else {
                // update the list of nodes the association is waiting for
                delayedAssociations.put(association, waitingFor);
            }
        }

        // check delayed associations
        Iterator<Map.Entry<Association, List<String>>> iterator = delayedAssociations.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Association, List<String>> next = iterator.next();
            Association association = next.getKey();
            List<String> waitingFor = next.getValue();
            waitingFor.removeAll(createdAttrs);

            if (waitingFor.isEmpty()) {
                List<JSONAssociation> nodeAssociations = jsonAssociations.getOrDefault(association.getSource(), new ArrayList<>());
                nodeAssociations.add(new JSONAssociation(association.getTarget(), association.getAccessRightSet()));
                jsonAssociations.put(association.getSource(), nodeAssociations);

                iterator.remove();
            }
        }

        Node node = policyQuery.graph().getNode(pc);
        boolean isAdminNode = AdminPolicy.isAdminPolicyNodeName(pc);

        JSONPolicyClass jsonPolicyClass = new JSONPolicyClass();
        jsonPolicyClass.setName(pc);
        if (!isAdminNode && !node.getProperties().isEmpty()) {
            jsonPolicyClass.setProperties(node.getProperties());
        }
        if (!isAdminNode && !userAttributes.isEmpty()) {
            jsonPolicyClass.setUserAttributes(userAttributes);
        }
        if (!isAdminNode && !objectAttributes.isEmpty()) {
            jsonPolicyClass.setObjectAttributes(objectAttributes);
        }
        if (!jsonAssociations.isEmpty()) {
            jsonPolicyClass.setAssociations(jsonAssociations);
        }

        return jsonPolicyClass;
    }

    private List<JSONNode> getAttributes(String start, NodeType type, List<Association> associations, PolicyQuery policyQuery) throws PMException {
        List<JSONNode> jsonNodes = new ArrayList<>();
        Collection<String> children = policyQuery.graph().getChildren(start);
        for(String child : children) {
            Node node = policyQuery.graph().getNode(child);
            if (node.getType() != type) {
                continue;
            }

            Collection<Association> nodeAssociations = policyQuery.graph().getAssociationsWithTarget(node.getName());
            for (Association association : nodeAssociations) {
                List<String> waitingFor = new ArrayList<>(List.of(association.getSource()));
                if (!AdminPolicy.isAdminPolicyNodeName(association.getTarget())) {
                    waitingFor.add(association.getTarget());
                }

                delayedAssociations.put(association, waitingFor);
            }

            associations.addAll(nodeAssociations);

            JSONNode jsonNode = new JSONNode();
            jsonNode.setName(child);

            if (!node.getProperties().isEmpty() && !createdAttrs.contains(child)) {
                jsonNode.setProperties(node.getProperties());
            }

            createdAttrs.add(child);

            List<JSONNode> childAttrs = getAttributes(child, type, associations, policyQuery);
            if (!childAttrs.isEmpty()) {
                jsonNode.setChildren(childAttrs);
            }

            jsonNodes.add(jsonNode);
        }

        return jsonNodes;
    }
}
