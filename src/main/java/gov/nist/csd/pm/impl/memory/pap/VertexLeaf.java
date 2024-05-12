package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.Association;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Map;

class VertexLeaf extends Vertex {

    private ObjectOpenHashSet<String> parents;

    public VertexLeaf(String name, NodeType type, Map<String, String> properties) {
        super(name, type, properties);
        this.parents = new ObjectOpenHashSet<>();
    }

    @Override
    protected ObjectOpenHashSet<String> getParents() {
        return parents;
    }

    @Override
    protected ObjectOpenHashSet<String> getChildren() {
        return new ObjectOpenHashSet<>();
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
        parents.add(parent);
    }

    @Override
    public void deleteAssignment(String child, String parent) {
        parents.remove(parent);
    }

    @Override
    public void addAssociation(String ua, String target, AccessRightSet accessRightSet) {

    }

    @Override
    public void deleteAssociation(String ua, String target) {

    }
}
