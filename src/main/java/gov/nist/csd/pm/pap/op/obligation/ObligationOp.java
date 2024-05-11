package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ObligationOp extends Operation {

    protected final UserContext author;
    protected final String name;
    protected final List<String> eventPatternNodes;
    private final transient List<Rule> rules;

    public ObligationOp(UserContext author, String name, List<Rule> rules) {
        this.author = author;
        this.name = name;
        this.rules = rules;

        this.eventPatternNodes = new ArrayList<>();
        for (Rule rule : rules) {
            EventPattern eventPattern = rule.getEventPattern();

            Pattern pattern = eventPattern.getSubjectPattern();
            eventPatternNodes.addAll(pattern.getReferencedNodes().nodes());

            List<Pattern> operandPatterns = eventPattern.getOperandPatterns();
            for (Pattern operandPattern : operandPatterns) {
                eventPatternNodes.addAll(operandPattern.getReferencedNodes().nodes());
            }
        }
    }

    @Override
    public abstract String getOpName();

    @Override
    public Object[] getOperands() {
        return operands(author, eventPatternNodes);
    }

    public UserContext getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }

    public List<String> getEventPatternNodes() {
        return eventPatternNodes;
    }

    public List<Rule> getRules() {
        return rules;
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
        ) && Objects.equals(eventPatternNodes, that.eventPatternNodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, name, eventPatternNodes);
    }
}
