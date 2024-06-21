package gov.nist.csd.pm.pap.serialization.pml;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.pap.admin.AdminPolicy;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.expression.literal.ArrayLiteral;
import gov.nist.csd.pm.pap.pml.expression.literal.MapLiteral;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.expression.reference.ReferenceByID;
import gov.nist.csd.pm.pap.pml.statement.*;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.serialization.PolicySerializer;
import gov.nist.csd.pm.pap.query.PolicyQuery;
import gov.nist.csd.pm.pap.serialization.json.*;

import java.util.*;

import static gov.nist.csd.pm.common.graph.node.NodeType.*;

public class PMLSerializer implements PolicySerializer {

    public static void main(String[] args) throws PMException {
        MemoryPAP pap = new MemoryPAP();
        String serialize = new PMLSerializer().serialize(pap.query());
        System.out.println(serialize);
    }

    @Override
    public String serialize(PolicyQuery policyQuery) throws PMException {
        JSONSerializer json = new JSONSerializer();
        JSONPolicy jsonPolicy = json.buildJSONPolicy(policyQuery);

        return serialize(jsonPolicy);
    }

    private String serialize(JSONPolicy jsonPolicy) {
        StringBuilder sb = new StringBuilder();

        sb.append("// pml functions and constants\n");
        sb.append(jsonUserDefinedPMLToPML(jsonPolicy.getPml()));

        sb.append("// resource operations\n");
        sb.append(jsonResourceOperations(jsonPolicy.getResourceAccessRights()));

        sb.append("\n// GRAPH\n");
        sb.append(jsonGraphToPML(jsonPolicy.getGraph()));

        sb.append("\n// PROHIBITIONS\n");
        sb.append(jsonProhibitionsToPML(jsonPolicy.getProhibitions()));

        sb.append("\n// OBLIGATIONS\n");
        sb.append(jsonObligations(jsonPolicy.getObligations()));

        return sb.toString();
    }

    private String jsonResourceOperations(AccessRightSet accessRightSet) {
        ArrayLiteral arrayLiteral = new ArrayLiteral(Type.string());
        for (String ar : accessRightSet) {
            arrayLiteral.add(new StringLiteral(ar));
        }

        return new SetResourceAccessRightsStatement(arrayLiteral).toFormattedString(0) + "\n";
    }

    private String jsonUserDefinedPMLToPML(JSONPML JSONPML) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> constants = JSONPML.getConstants();
        for (Map.Entry<String, String> e : constants.entrySet()) {
            // wrap e.getValue in string literal constructor to include the quote marks
            sb.append(String.format("const %s = %s", e.getKey(), e.getValue())).append("\n");
        }

        sb.append("\n");

        Map<String, String> functions = JSONPML.getFunctions();
        for (Map.Entry<String, String> e : functions.entrySet()) {
            sb.append(e.getValue()).append("\n\n");
        }

        return sb.toString();
    }

    private String jsonGraphToPML(JSONGraph jsonGraph) {
        StringBuilder pml = new StringBuilder();

        pml.append(buildPolicyClassesPML(jsonGraph));
        pml.append(buildAttributesPML(jsonGraph));
        pml.append(buildUsersAndObjectsPML(jsonGraph));

        return pml.toString();
    }

    private String buildPolicyClassesPML(JSONGraph jsonGraph) {
        StringBuilder sb = new StringBuilder();

        sb.append("// policy classes\n");

        Map<String, JSONPolicyClass> pcs = jsonGraph.getPcs();
        for (Map.Entry<String, JSONPolicyClass> e : pcs.entrySet()) {
            // do not serialize admin policy node
            if (AdminPolicy.isAdminPolicyNodeName(e.getKey())) {
                continue;
            }

            sb.append(new CreatePolicyStatement(
                    buildNameExpression(e.getKey())
            )).append("\n");

            SetNodePropertiesStatement setNodePropertiesStatement =
                    buildSetNodePropertiesStatement(e.getKey(), e.getValue().getProperties());
            if (setNodePropertiesStatement != null) {
                sb.append(setNodePropertiesStatement).append("\n");
            }
        }

        return sb.toString();
    }

    private String buildAttributesPML(JSONGraph jsonGraph) {
        StringBuilder sb = new StringBuilder();

        Set<String> seen =  initSeenNodes(jsonGraph);
        Map<String, JSONNode> delayedNodes = new HashMap<>();
        Map<String, List<String>> delayedAssignments = new HashMap<>();
        Map<String, Map<String, AccessRightSet>> delayedAssociations = new HashMap<>();

        sb.append("\n// user attributes\n");
        for (Map.Entry<String, JSONNode> e : jsonGraph.getUas().entrySet()) {
            sb.append(buildAttrPML(jsonGraph, e.getKey(), UA, e.getValue(), seen, delayedNodes, delayedAssignments, delayedAssociations));
        }

        // reset delayed for seen and OAs -- inlclude policy class target oas and admin policy oas
        seen = initSeenNodes(jsonGraph);
        delayedNodes.clear();
        delayedAssignments.clear();

        sb.append("\n// object attributes\n");
        for (Map.Entry<String, JSONNode> e : jsonGraph.getOas().entrySet()) {
            sb.append(buildAttrPML(jsonGraph, e.getKey(), OA, e.getValue(), seen, delayedNodes, delayedAssignments, delayedAssociations));
        }

        return sb.toString();
    }

    private Set<String> initSeenNodes(JSONGraph jsonGraph) {
        Set<String> seen = new HashSet<>(jsonGraph.getPcs().keySet());
        for (Map.Entry<String, JSONPolicyClass> e : jsonGraph.getPcs().entrySet()) {
            seen.add(AdminPolicy.policyClassTargetName(e.getKey()));
        }
        seen.addAll(AdminPolicy.ALL_NODE_NAMES);

        return seen;
    }

    private String buildAttrPML(JSONGraph jsonGraph, String name, NodeType type, JSONNode node, Set<String> seen,
                                Map<String, JSONNode> delayedNodes, Map<String, List<String>> delayedAssignments,
                                Map<String, Map<String, AccessRightSet>> delayedAssociations) {
        StringBuilder sb = new StringBuilder();

        List<String> assignments = processAssignments(name, node, seen, delayedAssignments);
        if (assignments.isEmpty()) {
            delayedNodes.put(name, node);
            return "";
        }

        Map<String, AccessRightSet> associations = processAssociations(name, node, seen, delayedAssociations);

        createNode(jsonGraph, sb, name, type, node, seen, assignments, associations, delayedNodes, delayedAssignments, delayedAssociations);

        return sb.toString();
    }

    private List<String> processAssignments(String name, JSONNode node, Set<String> seen,
                                            Map<String, List<String>> delayedAssignments) {
        // process assignments, storing any assignments in which the descendant node does not exist
        Collection<String> assignments = node.getAssignments();
        List<String> seenAssignments = new ArrayList<>();
        for (String assignment : assignments) {
            if (seen.contains(assignment)) {
                seenAssignments.add(assignment);
            } else {
                List<String> delayed = delayedAssignments.getOrDefault(assignment, new ArrayList<>());
                delayed.add(name);
                delayedAssignments.put(assignment, delayed);
            }
        }

        return seenAssignments;
    }

    private Map<String, AccessRightSet> processAssociations(String name, JSONNode node, Set<String> seen,
                                                            Map<String, Map<String, AccessRightSet>> delayedAssociations) {
        // process the associations if UA node, storing any in which the target is not created yet
        if (node.getAssociations() == null) {
            return new HashMap<>();
        }

        Map<String, AccessRightSet> associations = node.getAssociations();
        Map<String, AccessRightSet> seenAssociations = new HashMap<>();
        for (Map.Entry<String, AccessRightSet> assoc : associations.entrySet()) {
            String target = assoc.getKey();
            if (seen.contains(target)) {
                seenAssociations.put(target, assoc.getValue());
            } else {
                Map<String, AccessRightSet> del = delayedAssociations.getOrDefault(target, new HashMap<>());
                del.put(name, assoc.getValue());
                delayedAssociations.put(target, del);
            }
        }

        return seenAssociations;
    }

    private void createNode(JSONGraph jsonGraph, StringBuilder sb, String name, NodeType type, JSONNode node, Set<String> seen,
                            Collection<String> assignments,
                            Map<String, AccessRightSet> associations,
                            Map<String, JSONNode> delayedNodes,
                            Map<String, List<String>> delayedAssignments,
                            Map<String, Map<String, AccessRightSet>> delayedAssociations) {
        // create with seen assignments
        sb.append(jsonNodeToPML(seen, name, type, node, assignments));

        // create delayed assignment statements
        List<String> delayed = delayedAssignments.getOrDefault(name, new ArrayList<>());
        while (!delayed.isEmpty()) {
            String ascendant = delayed.getFirst();

            // create the ascendant node if not sen yet
            if (delayedNodes.containsKey(ascendant)) {
                // add create node statement
                sb.append(jsonNodeToPML(seen, ascendant, type, delayedNodes.get(ascendant), List.of(name)));

                // remove from delayed nodes
                delayedNodes.remove(ascendant);
            } else {
                // add assign to statement
                sb.append(new AssignStatement(
                        buildNameExpression(ascendant),
                        new ArrayLiteral(Type.string(), buildNameExpression(name))
                )).append("\n");
            }

            // remove from delayed assignments
            List<String> delAssigns = delayedAssignments.getOrDefault(name, new ArrayList<>());
            delAssigns.remove(ascendant);

            if (delAssigns.isEmpty()) {
                delayedAssignments.remove(name);
            } else {
                delayedAssignments.put(name, delAssigns);
            }
        }

        // create associations and delayed association statements
        associations.putAll(delayedAssociations.getOrDefault(name, new HashMap<>()));
        Iterator<Map.Entry<String, AccessRightSet>> iter = associations.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, AccessRightSet> next = iter.next();

            Expression ua;
            Expression target;
            if (type == UA) {
                ua = buildNameExpression(name);
                target = buildNameExpression(next.getKey());
            } else {
                ua = buildNameExpression(next.getKey());
                target = buildNameExpression(name);
            }

            sb.append(new AssociateStatement(
                    ua,
                    target,
                    setToExpression(next.getValue())
            )).append("\n");
        }

        delayedAssociations.remove(name);
    }

    private String buildUsersAndObjectsPML(JSONGraph jsonGraph) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n// users\n");
        for (Map.Entry<String, JSONNode> e : jsonGraph.getUsers().entrySet()) {
            sb.append(jsonNodeToPML(new HashSet<>(), e.getKey(), U, e.getValue(), e.getValue().getAssignments()));
        }

        sb.append("\n// objects\n");
        for (Map.Entry<String, JSONNode> e : jsonGraph.getObjects().entrySet()) {
            sb.append(jsonNodeToPML(new HashSet<>(), e.getKey(), O, e.getValue(), e.getValue().getAssignments()));
        }

        return sb.toString();
    }

    private String jsonNodeToPML(Set<String> seen, String name, NodeType type, JSONNode jsonNode, Collection<String> assignments) {
        StringBuilder sb = new StringBuilder();

        // if node is an admin node, assign to assignments not create
        if (seen.contains(name)) {
            sb.append(new AssignStatement(
                    buildNameExpression(name),
                    setToExpression(new HashSet<>(assignments))
            )).append("\n");
        } else {
            sb.append(new CreateNonPCStatement(
                    buildNameExpression(name),
                    type,
                    setToExpression(new HashSet<>(assignments))
            )).append("\n");
        }

        SetNodePropertiesStatement setNodePropertiesStatement =
                buildSetNodePropertiesStatement(name, jsonNode.getProperties());
        if (setNodePropertiesStatement != null) {
            sb.append(setNodePropertiesStatement).append("\n");
        }

        return sb.toString();

    }

    private String jsonProhibitionsToPML(List<String> prohibitions) {
        StringBuilder pml = new StringBuilder();

        for (String p : prohibitions) {
            pml.append(p).append("\n");
        }

        return pml.toString();
    }

    private String jsonObligations(List<String> obligations) {
        StringBuilder pml = new StringBuilder();

        for (String o : obligations) {
            pml.append(o).append("\n");
        }

        return pml.toString();
    }

    private Expression buildNameExpression(String name) {
        if (AdminPolicy.isAdminPolicyNodeName(name)) {
            return new ReferenceByID(
                    AdminPolicyNode.fromNodeName(name).constantName()
            );
        }

        return new StringLiteral(name);
    }

    private ArrayLiteral setToExpression(Set<String> set) {
        Expression[] expressions = new Expression[set.size()];
        int i = 0;
        for (String s : set) {
            expressions[i] = buildNameExpression(s);
            i++;
        }

        return new ArrayLiteral(
                expressions,
                Type.string()
        );
    }

    private SetNodePropertiesStatement buildSetNodePropertiesStatement(String name, Map<String, String> properties) {
        Expression propertiesExpression = propertiesMapToExpression(properties);
        if (propertiesExpression == null) {
            return null;
        }

        return new SetNodePropertiesStatement(
                buildNameExpression(name),
                propertiesExpression
        );
    }

    private Expression propertiesMapToExpression(Map<String, String> properties) {
        if (properties == null || properties.isEmpty()) {
            return null;
        }

        Map<Expression, Expression> propertiesExpressions = new HashMap<>();
        for (Map.Entry<String, String> property : properties.entrySet()) {
            propertiesExpressions.put(
                    new StringLiteral(property.getKey()),
                    new StringLiteral(property.getValue())
            );
        }

        return new MapLiteral(propertiesExpressions, Type.string(), Type.string());
    }
}
