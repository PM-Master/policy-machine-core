package gov.nist.csd.pm.pap.op;

public class DeserializeFromJsonEvent implements PolicyEvent{

    private String json;

    public DeserializeFromJsonEvent(String json) {
        this.json = json;
    }

    @Override
    public String getEventName() {
        return "deserialize_from_json";
    }

}
