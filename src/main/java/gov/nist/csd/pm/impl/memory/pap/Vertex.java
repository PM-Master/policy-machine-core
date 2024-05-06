package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.graph.relationship.Association;

import java.util.List;
import java.util.Map;

abstract class Vertex {

    protected abstract void setProperties(Map<String, String> properties);
    protected abstract Node getNode();
    protected abstract List<String> getParents();
    protected abstract List<String> getChildren();
    protected abstract List<Association> getOutgoingAssociations();
    protected abstract List<Association> getIncomingAssociations();

    protected abstract void addAssignment(String child, String parent);
    protected abstract void deleteAssignment(String child, String parent);
    protected abstract void addAssociation(String ua, String target, AccessRightSet accessRightSet);
    protected abstract void deleteAssociation(String ua, String target);

}
