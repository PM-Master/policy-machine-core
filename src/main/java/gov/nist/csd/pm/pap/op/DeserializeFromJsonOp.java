package gov.nist.csd.pm.pap.op;

import java.util.Objects;

public class DeserializeFromJsonOp extends Operation {
    private final String json;

    public DeserializeFromJsonOp(String json) {
        super(operands(json));
        this.json = json;
    }

    @Override
    public String getOpName() {
        return "deserialize_from_json";
    }

    public String json() {
        return json;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (DeserializeFromJsonOp) obj;
        return Objects.equals(this.json, that.json);
    }

    @Override
    public int hashCode() {
        return Objects.hash(json);
    }

    @Override
    public String toString() {
        return "DeserializeFromJsonOp[" +
                "json=" + json + ']';
    }


}
