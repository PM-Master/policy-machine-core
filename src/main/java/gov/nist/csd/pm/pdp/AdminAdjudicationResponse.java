package gov.nist.csd.pm.pdp;

import java.util.Objects;

public class AdminAdjudicationResponse {

    private Status status;

    public AdminAdjudicationResponse(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (AdminAdjudicationResponse) obj;
        return Objects.equals(this.status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return "AdminAdjudicationResponse[" +
                "status=" + status + ']';
    }

}
