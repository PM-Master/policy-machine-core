package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.Association;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Map;

class VertexPolicyClass extends Vertex {
    private ObjectOpenHashSet<String> children;

    public VertexPolicyClass(String name, Map<String, String> properties) {
        super(name, NodeType.PC, properties);
        this.children = new ObjectOpenHashSet<>();
    }

    @Override
    protected ObjectOpenHashSet<String> getParents() {
        return new ObjectOpenHashSet<>();
    }

    @Override
    protected ObjectOpenHashSet<String> getChildren() {
        return children;
    }

    @Override
    protected ObjectOpenHashSet<Association> getOutgoingAssociations() {
        return new ObjectOpenHashSet<>();
    }

    @Override
    protected ObjectOpenHashSet<Association> getIncomingAssociations() {
        return new ObjectOpenHashSet<>();
    }

    @Override
    public void addAssignment(String child, String parent) {
        children.add(child);
    }

    @Override
    public void deleteAssignment(String child, String parent) {
        children.remove(child);
    }

    @Override
    public void addAssociation(String ua, String target, AccessRightSet accessRightSet) {

    }

    @Override
    public void deleteAssociation(String ua, String target) {

    }
}
