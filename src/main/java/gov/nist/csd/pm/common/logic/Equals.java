package gov.nist.csd.pm.common.logic;

public class Equals extends Predicate {

    public static Equals equals(Predicate... args) {
        return new Equals(args);
    }

    private Object arg;

    public Equals(Object arg) {
        this.arg = arg;
    }

    @Override
    public boolean eval(Object input) {
        return input.equals(arg);
    }
}
