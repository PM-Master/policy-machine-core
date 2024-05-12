package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.obligation.Rule;

import java.util.Collection;
import java.util.List;

public class CreateObligationOp extends ObligationOp {

    public CreateObligationOp(UserContext author, String name, Collection<Rule> rules) {
        super(author, name, rules);
    }

    @Override
    public String getOpName() {
        return "create_obligation";
    }

    @Override
    public String toString() {
        return "CreateObligationOp{" +
                "author=" + author +
                ", name='" + name + '\'' +
                ", eventPatternNodes=" + eventPatternNodes +
                '}';
    }
}
