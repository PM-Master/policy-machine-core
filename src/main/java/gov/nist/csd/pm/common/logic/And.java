package gov.nist.csd.pm.common.logic;

public class And extends Predicate {

    public static void main(String[] args) {
        And and = and();
        System.out.println(and.eval());
    }

    public static And and(Predicate... args) {
        return new And(args);
    }

    private Predicate[] args;

    public And(Predicate[] args) {
        this.args = args;
    }

    @Override
    public boolean eval() {
        // evaluate all args, if one is false return false
        for (Predicate arg : args) {
            if (!arg.eval()) {
                return false;
            }
        }

        return true;
    }
}
