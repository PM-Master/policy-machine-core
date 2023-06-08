package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.Association;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Map;

class VertexAttribute extends Vertex {

    private ObjectOpenHashSet<String> parents;
    private ObjectOpenHashSet<String> children;
    private ObjectOpenHashSet<Association> outgoingAssociations;
    private ObjectOpenHashSet<Association> incomingAssociations;

    public VertexAttribute(String name, NodeType type, Map<String, String> properties) {
        super(name, type, properties);
        this.parents = new ObjectOpenHashSet<>();
        this.children = new ObjectOpenHashSet<>();
        this.outgoingAssociations = new ObjectOpenHashSet<>();
        this.incomingAssociations = new ObjectOpenHashSet<>();
    }

    @Override
    public ObjectOpenHashSet<String> getParents() {
        return parents;
    }

    @Override
    public ObjectOpenHashSet<String> getChildren() {
        return children;
    }

    @Override
    public ObjectOpenHashSet<Association> getOutgoingAssociations() {
        return outgoingAssociations;
    }

    @Override
    public ObjectOpenHashSet<Association> getIncomingAssociations() {
        return incomingAssociations;
    }

    @Override
    protected void addAssignment(String child, String parent) {
        if (child.equals(name)) {
            parents.add(parent);
        } else {
            children.add(child);
        }
    }

    @Override
    protected void deleteAssignment(String child, String parent) {
        if (child.equals(name)) {
            parents.remove(parent);
        } else {
            children.remove(child);
        }
    }

    @Override
    public void addAssociation(String ua, String target, AccessRightSet accessRightSet) {
        if (ua.equals(name)) {
            outgoingAssociations.add(new Association(ua, target, accessRightSet));
        } else {
            incomingAssociations.add(new Association(ua, target, accessRightSet));
        }
    }

    @Override
    public void deleteAssociation(String ua, String target) {
        if (ua.equals(name)) {
            outgoingAssociations.removeIf(a -> a.getSource().equals(ua) && a.getTarget().equals(target));
        } else {
            incomingAssociations.removeIf(a -> a.getSource().equals(ua) && a.getTarget().equals(target));
        }
    }
}

