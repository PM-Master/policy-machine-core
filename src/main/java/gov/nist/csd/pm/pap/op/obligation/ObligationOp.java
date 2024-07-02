package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBLIGATION;

public abstract class ObligationOp extends Operation<Void> {

    protected UserContext author;
    protected String name;
    protected Collection<Rule> rules;
    protected String reqCap;

    public ObligationOp(String opName, String reqCap) {
        super(opName, List.of(
                new RequiredCapability("author"),
                new RequiredCapability("name"),
                new RequiredCapability("rules")
        ));

        this.reqCap = reqCap;
    }

    public ObligationOp(String opName, UserContext author, String name, Collection<Rule> rules, String reqCap) {
        super(opName, List.of(
                new RequiredCapability("author"),
                new RequiredCapability("name"),
                new RequiredCapability("rules")
        ));

        this.reqCap = reqCap;

        setOperands(author, name, rules);
    }

    @Override
    public void setOperands(List<Object> operands) {
        super.setOperands(operands);

        this.author = (UserContext) operands.get(0);
        this.name = (String) operands.get(1);
        this.rules = (Collection<Rule>) operands.get(2);
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
    public Operation canExecute(PAP pap, UserContext userCtx) throws PMException {
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

        return this;
    }
}
