package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.obligation.Rule;

import java.util.List;
import java.util.Objects;

public class CreateObligationOp extends ObligationsOp {
    private final UserContext author;
    private final String name;
    private final List<Rule> rules;

    public CreateObligationOp(UserContext author, String name, List<Rule> rules) {
        this.author = author;
        this.name = name;
        this.rules = rules;
    }

    @Override
    public String getOpName() {
        return "create_obligation";
    }

    @Override
    public Object[] getOperands() {
        return operands(author, name, rules);
    }

    public UserContext author() {
        return author;
    }

    public String name() {
        return name;
    }

    public List<Rule> rules() {
        return rules;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (CreateObligationOp) obj;
        return Objects.equals(this.author, that.author) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.rules, that.rules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, name, rules);
    }

    @Override
    public String toString() {
        return "CreateObligationOp[" +
                "author=" + author + ", " +
                "name=" + name + ", " +
                "rules=" + rules + ']';
    }

}
