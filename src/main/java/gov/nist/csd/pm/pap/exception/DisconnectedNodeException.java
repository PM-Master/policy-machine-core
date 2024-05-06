package gov.nist.csd.pm.pap.exception;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.NodeType;

public class DisconnectedNodeException extends PMException {
    public DisconnectedNodeException(String child, String parent) {
        super("deassigning " + child + " from " + parent + " would make " + child + " a disconnected node");
    }
    public DisconnectedNodeException(String node, NodeType type) {
        super(node + " is of type " + type + " which is required to be assigned to at least one node initially");
    }
}
