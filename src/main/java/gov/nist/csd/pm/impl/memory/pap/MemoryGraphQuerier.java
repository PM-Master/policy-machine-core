package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.dag.DepthFirstGraphWalker;
import gov.nist.csd.pm.common.graph.dag.Direction;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.pap.query.GraphQuerier;
import gov.nist.csd.pm.pap.query.GraphQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static gov.nist.csd.pm.common.graph.node.NodeType.*;
import static gov.nist.csd.pm.common.graph.node.Properties.WILDCARD;

public class MemoryGraphQuerier extends GraphQuerier implements GraphQuery {

    private MemoryPolicy memoryPolicy;

    public MemoryGraphQuerier(MemoryPolicy memoryPolicy) {
        this.memoryPolicy = memoryPolicy;
    }

    @Override
    public Node getNodeInternal(String name) throws PMException {
        return memoryPolicy.graph.get(name).getNode();
    }

    @Override
    public List<String> getParentsInternal(String name) throws PMException {
        return memoryPolicy.graph.get(name).getParents();
    }

    @Override
    public List<String> getChildrenInternal(String name) throws PMException {
        return memoryPolicy.graph.get(name).getChildren();
    }

    @Override
    public List<Association> getAssociationsWithSourceInternal(String ua) throws PMException {
        return memoryPolicy.graph.get(ua).getOutgoingAssociations();
    }

    @Override
    public List<Association> getAssociationsWithTargetInternal(String target) throws PMException {
        return memoryPolicy.graph.get(target).getIncomingAssociations();
    }

    @Override
    public List<String> getAttributeContainersInternal(String node) throws PMException {
        List<String> attrs = new ArrayList<>();

        new DepthFirstGraphWalker(this)
                .withDirection(Direction.PARENTS)
                .withVisitor((n) -> {
                    Node visitedNode;
                    visitedNode = getNode(n);
                    if (visitedNode.getType().equals(UA) ||
                            visitedNode.getType().equals(OA)) {
                        attrs.add(n);
                    }
                })
                .walk(node);

        return attrs;
    }

    @Override
    public List<String> getPolicyClassContainersInternal(String node) throws PMException {
        List<String> attrs = new ArrayList<>();

        new DepthFirstGraphWalker(this)
                .withDirection(Direction.PARENTS)
                .withVisitor((n) -> {
                    Node visitedNode;
                    visitedNode = getNode(n);
                    if (visitedNode.getType().equals(PC)) {
                        attrs.add(n);
                    }
                })
                .walk(node);

        return attrs;
    }

    @Override
    public boolean isContainedInternal(String subject, String container) throws PMException {
        AtomicBoolean found = new AtomicBoolean(false);

        new DepthFirstGraphWalker(this)
                .withDirection(Direction.PARENTS)
                .withVisitor((n) -> {
                    if (n.equals(container)) {
                        found.set(true);
                    }
                })
                .walk(subject);

        return found.get();
    }

    @Override
    public AccessRightSet getResourceAccessRights() throws PMException {
        return memoryPolicy.resourceAccessRights;
    }

    @Override
    public boolean nodeExists(String name) throws PMException {
        return memoryPolicy.graph.containsKey(name);
    }

    @Override
    public List<String> search(NodeType type, Map<String, String> properties) {
        List<String> nodes = filterByType(type);
        return filterByProperties(nodes, properties);
    }

    @Override
    public List<String> getPolicyClasses() {
        return new ArrayList<>(memoryPolicy.pcs);
    }

    private List<String> filterByProperties(List<String> nodes, Map<String, String> properties) {
        List<String> results = new ArrayList<>();
        if (properties.isEmpty()) {
            results.addAll(nodes);
        } else {
            for (String n : nodes) {
                Map<String, String> nodeProperties = memoryPolicy.graph.get(n).getNode().getProperties();

                if (!hasAllKeys(nodeProperties, properties)
                        || !valuesMatch(nodeProperties, properties)) {
                    continue;
                }

                results.add(n);
            }
        }

        return results;
    }

    private List<String> filterByType(NodeType type) {
        List<String> nodes = new ArrayList<>();
        if (type != ANY) {
            if (type == PC) {
                nodes.addAll(memoryPolicy.pcs);
            } else if (type == OA) {
                nodes.addAll(memoryPolicy.oas);
            } else if (type == UA) {
                nodes.addAll(memoryPolicy.uas);
            } else if (type == O) {
                nodes.addAll(memoryPolicy.os);
            } else {
                nodes.addAll(memoryPolicy.us);
            }
        } else {
            nodes.addAll(memoryPolicy.pcs);
            nodes.addAll(memoryPolicy.uas);
            nodes.addAll(memoryPolicy.oas);
            nodes.addAll(memoryPolicy.us);
            nodes.addAll(memoryPolicy.os);
        }

        return nodes;
    }

    private boolean valuesMatch(Map<String, String> nodeProperties, Map<String, String> checkProperties) {
        for (Map.Entry<String, String> entry : checkProperties.entrySet()) {
            String checkKey = entry.getKey();
            String checkValue = entry.getValue();
            if (!checkValue.equals(nodeProperties.get(checkKey))
                    && !checkValue.equals(WILDCARD)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasAllKeys(Map<String, String> nodeProperties, Map<String, String> checkProperties) {
        for (String key : checkProperties.keySet()) {
            if (!nodeProperties.containsKey(key)) {
                return false;
            }
        }

        return true;
    }

    private boolean associationExists(String source, String target) {
        List<Association> outgoingAssociations = memoryPolicy.graph.get(source).getOutgoingAssociations();
        for (Association a : outgoingAssociations) {
            if (a.getTarget().equals(target)) {
                return true;
            }
        }

        return false;
    }
}