package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.Association;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Map;

class VertexPolicyClass extends Vertex {
    private ObjectOpenHashSet<String> ascendants;

    public VertexPolicyClass(String name, Map<String, String> properties) {
        super(name, NodeType.PC, properties);
        this.ascendants = new ObjectOpenHashSet<>();
    }

    @Override
    protected ObjectOpenHashSet<String> getDescendants() {
        return new ObjectOpenHashSet<>();
    }

    @Override
    protected ObjectOpenHashSet<String> getAscendants() {
        return ascendants;
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
    public void addAssignment(String ascendant, String descendant) {
        ascendants.add(ascendant);
    }

    @Override
    public void deleteAssignment(String ascendant, String descendant) {
        ascendants.remove(ascendant);
    }

    @Override
    public void addAssociation(String ua, String target, AccessRightSet accessRightSet) {

    }

    @Override
    public void deleteAssociation(String ua, String target) {

    }
}
