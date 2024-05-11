package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.obligation.Rule;

import java.util.List;
import java.util.Objects;

public class UpdateObligationOp extends ObligationOp {

    public UpdateObligationOp(UserContext author, String name, List<Rule> rules) {
        super(author, name, rules);
    }

    @Override
    public String getOpName() {
        return "update_obligation";
    }

    @Override
    public String toString() {
        return "UpdateObligationOp{" +
                "author=" + author +
                ", name='" + name + '\'' +
                ", eventPatternNodes=" + eventPatternNodes +
                '}';
    }
}
