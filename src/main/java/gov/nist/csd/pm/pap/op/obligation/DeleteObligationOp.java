package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.List;

public class DeleteObligationOp extends ObligationOp {

    public DeleteObligationOp(UserContext author,
                              String name,
                              List<Rule> rules) {
        super(author, name, rules);
    }

    @Override
    public String getOpName() {
        return "delete_obligation";
    }

    @Override
    public String toString() {
        return "DeleteObligationOp{" +
                "author=" + author +
                ", name='" + name + '\'' +
                ", eventPatternNodes=" + eventPatternNodes +
                '}';
    }
}
