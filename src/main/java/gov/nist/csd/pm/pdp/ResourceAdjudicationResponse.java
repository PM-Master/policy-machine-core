package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.common.graph.node.Node;

import java.util.Objects;

public class ResourceAdjudicationResponse {

    private Status status;
    private Node node;

    public ResourceAdjudicationResponse(Status status, Node node) {
        this.status = status;
        this.node = node;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (ResourceAdjudicationResponse) obj;
        return Objects.equals(this.status, that.status) &&
                Objects.equals(this.node, that.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, node);
    }

    @Override
    public String toString() {
        return "ResourceAdjudicationResponse[" +
                "status=" + status + ", " +
                "node=" + node + ']';
    }

}
