package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.impl.memory.pap.unmodifiable.UnmodifiableNode;
import gov.nist.csd.pm.pdp.AccessRightSet;
import gov.nist.csd.pm.common.graph.nodes.Node;
import gov.nist.csd.pm.common.graph.nodes.NodeType;
import gov.nist.csd.pm.common.graph.relationships.Association;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class VertexPolicyClass extends Vertex {

    private UnmodifiableNode node;
    private List<String> children;

    public VertexPolicyClass(String name, Map<String, String> properties) {
        this.node = new UnmodifiableNode(name, NodeType.PC, properties);
        this.children = Collections.unmodifiableList(new ArrayList<>());
    }

    private VertexPolicyClass(Node node, List<String> children) {
        this.node = new UnmodifiableNode(node);
        this.children = Collections.unmodifiableList(children);
    }

    @Override
    protected void setProperties(Map<String, String> properties) {
        node = new UnmodifiableNode(node.getName(), node.getType(), properties);
    }

    @Override
    public UnmodifiableNode getNode() {
        return node;
    }

    @Override
    public List<String> getParents() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getChildren() {
        return children;
    }

    @Override
    public List<Association> getOutgoingAssociations() {
        return Collections.emptyList();
    }

    @Override
    public List<Association> getIncomingAssociations() {
        return Collections.emptyList();
    }

    @Override
    public void addAssignment(String child, String parent) {
        List<String> l = new ArrayList<>(children);
        l.add(child);
        children = Collections.unmodifiableList(l);
    }

    @Override
    public void deleteAssignment(String child, String parent) {
        List<String> l = new ArrayList<>(children);
        l.remove(child);
        children = Collections.unmodifiableList(l);
    }

    @Override
    public void addAssociation(String ua, String target, AccessRightSet accessRightSet) {

    }

    @Override
    public void deleteAssociation(String ua, String target) {

    }
}
