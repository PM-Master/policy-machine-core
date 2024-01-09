package gov.nist.csd.pm.policy.exceptions;

import gov.nist.csd.pm.policy.model.graph.nodes.NodeType;

public class DisconnectedNodeException extends PMException{
    public DisconnectedNodeException(String child, String parent) {
        super("deassigning " + child + " from " + parent + " would make " + child + " a disconnected node");
    }
    public DisconnectedNodeException(String node, NodeType type) {
        super(node + " is of type " + type + " which is required to be assigned to at least one node initially");
    }
}
