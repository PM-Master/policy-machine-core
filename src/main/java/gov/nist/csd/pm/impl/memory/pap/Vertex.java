package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.relationship.Association;

import java.util.Collection;
import java.util.Map;

abstract class Vertex {

    protected String name;
    protected NodeType type;
    protected Map<String, String> properties;

    public Vertex(String name, NodeType type, Map<String, String> properties) {
        this.name = name;
        this.type = type;
        this.properties = properties;
    }

    protected String getName() {
        return name;
    }

    protected NodeType getType() {
        return type;
    }

    protected void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    protected Map<String, String> getProperties() {
        return properties;
    }

    protected abstract Collection<String> getDescendants();
    protected abstract Collection<String> getAscendants();
    protected abstract Collection<Association> getOutgoingAssociations();
    protected abstract Collection<Association> getIncomingAssociations();

    protected abstract void addAssignment(String ascendant, String descendant);
    protected abstract void deleteAssignment(String ascendant, String descendant);
    protected abstract void addAssociation(String ua, String target, AccessRightSet accessRightSet);
    protected abstract void deleteAssociation(String ua, String target);

}
