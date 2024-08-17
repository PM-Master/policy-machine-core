package gov.nist.csd.pm.pap.exception;

public class NodeReferencedInProhibitionException extends PMException {
    public NodeReferencedInProhibitionException(String nodeToDelete, String prohibitionName) {
        super("cannot delete \"" + nodeToDelete + "\" because it is referenced in prohibition \"" + prohibitionName + "\"");
    }
}