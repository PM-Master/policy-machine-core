package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.operand.Operand;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBLIGATION;

public abstract class ObligationOp extends Operation {

    protected final UserContext author;
    protected final String name;
    protected final Collection<Rule> rules;
    protected final transient String reqCap;

    public ObligationOp(String opName, UserContext author, String name, Collection<Rule> rules, String reqCap) {
        super(opName,
              new Operand("author", author),
              new Operand("name", name),
              new Operand("rules", rules));
        this.author = author;
        this.name = name;
        this.rules = rules;
        this.reqCap = reqCap;
    }

    public UserContext getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }

    public Collection<Rule> getRules() {
        return rules;
    }

    @Override
    public void canExecute(PAP pap, UserContext userCtx) throws PMException {
        for (Rule rule : rules) {
            EventPattern eventPattern = rule.getEventPattern();

            // check subject pattern
            Pattern pattern = eventPattern.getSubjectPattern();
            checkPatternPrivileges(pap, userCtx, pattern, AdminPolicyNode.OBLIGATIONS_TARGET, reqCap);

            // check operand patterns
            for (Pattern operandPattern : eventPattern.getOperandPatterns()) {
                checkPatternPrivileges(pap, userCtx, operandPattern, AdminPolicyNode.OBLIGATIONS_TARGET, reqCap);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObligationOp that = (ObligationOp) o;
        return Objects.equals(author, that.author) && Objects.equals(
                name,
                that.name
        ) && Objects.equals(rules, that.rules) && Objects.equals(reqCap, that.reqCap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, name, rules, reqCap);
    }

    @Override
    public String toString() {
        return "ObligationOp{" +
                "author=" + author +
                ", name='" + name + '\'' +
                ", rules=" + rules +
                ", reqCap='" + reqCap + '\'' +
                '}';
    }
}
