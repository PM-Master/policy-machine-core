package gov.nist.csd.pm.pap.op.operand;

public class Operand {

    private String name;
    private Object value;

    public Operand(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Operand(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
