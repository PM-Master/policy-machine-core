package gov.nist.csd.pm.pap.serialization.json;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.admin.AdminPolicy;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.serialization.PolicySerializer;
import gov.nist.csd.pm.pap.query.PolicyQuery;

import java.util.*;

import static gov.nist.csd.pm.common.graph.node.NodeType.*;
import static gov.nist.csd.pm.pap.admin.AdminPolicyNode.ADMIN_POLICY;
import static gov.nist.csd.pm.pap.admin.AdminPolicyNode.POLICY_CLASS_TARGETS;

public class JSONSerializer implements PolicySerializer {

    @Override
    public String serialize(PolicyQuery policyQuery) throws PMException {
        return buildJSONPolicy(policyQuery)
                .toString();
    }

    public JSONPolicy buildJSONPolicy(PolicyQuery policyQuery) throws PMException {
        return new JSONPolicy(
                policyQuery.graph().getResourceAccessRights(),
                buildGraphJSON(policyQuery),
                buildProhibitionsJSON(policyQuery),
                buildObligationsJSON(policyQuery)
        );
    }

    private List<String> buildObligationsJSON(PolicyQuery policyQuery) throws PMException {
        List<String> jsonObligations = new ArrayList<>();
        Collection<Obligation> all = policyQuery.obligations().getAll();
        for (Obligation obligation : all) {
            jsonObligations.add(obligation.toString());
        }

        return jsonObligations;
    }

    private List<String> buildProhibitionsJSON(PolicyQuery policyQuery) throws PMException {
        List<String> prohibitions = new ArrayList<>();
        Map<String, Collection<Prohibition>> all = policyQuery.prohibitions().getAll();
        for (Collection<Prohibition> value : all.values()) {
            for (Prohibition prohibition : value) {
                prohibitions.add(prohibition.toString());
            }
        }

        return prohibitions;
    }

    private JSONGraph buildGraphJSON(PolicyQuery policyQuery) throws PMException {
        return new JSONGraph(
                buildPolicyClasses(policyQuery),
                buildUserAttributes(policyQuery),
                buildNonUANodes(policyQuery, OA),
                buildNonUANodes(policyQuery, U),
                buildNonUANodes(policyQuery, O)
        );
    }

    private Map<String, JSONNode> buildNonUANodes(PolicyQuery policyQuery, NodeType type) throws PMException {
        Map<String, JSONNode> nodes = new HashMap<>();

        Collection<String> search = policyQuery.graph().search(type, new HashMap<>());
        for (String node : search) {
            AdminOrTarget adminOrTarget = isAdminNodeOrTargetNode(policyQuery, node);

            if (isUnmodifiedAdminNodeOrTarget(policyQuery, node, adminOrTarget)) {
                continue;
            }

            Node n = policyQuery.graph().getNode(node);

            String name = n.getName();
            Map<String, String> properties = n.getProperties();
            Collection<String> descs = new ArrayList<>(policyQuery.graph().getAdjacentDescendants(name));

            // remove default admin node assignments
            if (adminOrTarget == AdminOrTarget.TARGET) {
                descs.remove(POLICY_CLASS_TARGETS.nodeName());
            } else if (adminOrTarget == AdminOrTarget.ADMIN) {
                descs.remove(ADMIN_POLICY.nodeName());
            }

            nodes.put(name, new JSONNode(properties, descs));
        }

        return nodes;
    }

    private AdminOrTarget isAdminNodeOrTargetNode(PolicyQuery policyQuery, String node) throws PMException {
        // check target node first because of the admin policy class target node
        // which satisfies both conditions
        boolean isTargetNode = isPolicyClassTarget(policyQuery, node);
        if (isTargetNode) {
            return AdminOrTarget.TARGET;
        }

        boolean isAdminNode = AdminPolicy.isAdminPolicyNodeName(node);
        if (isAdminNode) {
            return AdminOrTarget.ADMIN;
        }

        // return null to denote neither an admin node ot a target node
        return null;
    }

    private boolean isUnmodifiedAdminNodeOrTarget(PolicyQuery policyQuery, String node, AdminOrTarget isAdminOrTarget) throws PMException {
        if (isAdminOrTarget == null) {
            return false;
        }

        Collection<String> descendants = policyQuery.graph().getAdjacentDescendants(node);
        boolean unmodified;

        // check target node first because of the admin policy class target node
        // which satisfies both conditions
        if (isAdminOrTarget == AdminOrTarget.TARGET) {
            unmodified = descendants.contains(POLICY_CLASS_TARGETS.nodeName()) && descendants.size() == 1;
        } else {
            unmodified = descendants.contains(AdminPolicyNode.ADMIN_POLICY.nodeName()) && descendants.size() == 1;
        }

        return unmodified;
    }

    enum AdminOrTarget {
        ADMIN,
        TARGET
    }

    private boolean isPolicyClassTarget(PolicyQuery query, String name) throws PMException {
        Collection<String> pcNames = query.graph().getPolicyClasses();

        for (String pcName : pcNames) {
            pcName = AdminPolicy.policyClassTargetName(pcName);
            if (pcName.equals(name)) {
                return true;
            }
        }

        return false;
    }


    private Map<String, JSONNode> buildUserAttributes(PolicyQuery policyQuery) throws PMException {
        Map<String, JSONNode> userAttributes = new HashMap<>();

        Collection<String> search = policyQuery.graph().search(UA, new HashMap<>());
        for (String node : search) {
            Node n = policyQuery.graph().getNode(node);

            String name = n.getName();
            Map<String, String> properties = n.getProperties();
            Collection<String> descendants = policyQuery.graph().getAdjacentDescendants(name);
            Collection<Association> assocList = policyQuery.graph().getAssociationsWithSource(name);
            Map<String, AccessRightSet> assocMap = new HashMap<>();

            for (Association assoc : assocList) {
                assocMap.put(assoc.getTarget(), assoc.getAccessRightSet());
            }

            JSONNode jsonNode;
            if (assocMap.isEmpty()) {
                jsonNode = new JSONNode(properties, descendants);
            } else {
                jsonNode = new JSONNode(properties, descendants, assocMap);
            }

            userAttributes.put(name, jsonNode);
        }

        return userAttributes;
    }

    private Map<String, JSONPolicyClass> buildPolicyClasses(PolicyQuery policyQuery) throws PMException {
        Map<String, JSONPolicyClass> policyClassesList = new HashMap<>();

        Collection<String> policyClasses = policyQuery.graph().getPolicyClasses();
        for (String pc : policyClasses) {
            if (AdminPolicy.isAdminPolicyNodeName(pc)) {
                continue;
            }

            JSONPolicyClass jsonPolicyClass = buildJSONPolicyCLass(pc, policyQuery);

            policyClassesList.put(pc, jsonPolicyClass);
        }

        return policyClassesList;
    }

    private JSONPolicyClass buildJSONPolicyCLass(String pc, PolicyQuery policyQuery) throws PMException {
        Node node = policyQuery.graph().getNode(pc);
        JSONPolicyClass jsonPolicyClass = new JSONPolicyClass();
        if (!node.getProperties().isEmpty()) {
            jsonPolicyClass.setProperties(node.getProperties());
        }

        return jsonPolicyClass;
    }
}
