package gov.nist.csd.pm.impl.memory.pdp;

import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.GraphQuery;
import gov.nist.csd.pm.pap.PolicyModifier;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.query.ObligationsQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryObligationsReviewer implements ObligationsQuery {

    private final PolicyModifier policy;
    private final GraphQuery graphQuery;

    public MemoryObligationsReviewer(PolicyModifier policy, MemoryGraphReviewer graphReviewer) {
        this.policy = policy;
        this.graphQuery = graphReviewer;
    }

    @Override
    public List<Obligation> getObligationsWithAuthor(UserContext userCtx) throws PMException {
        List<Obligation> obls = new ArrayList<>();
        for (Obligation obligation : policy.obligations().getAll()) {
            if (obligation.getAuthor().equals(userCtx)) {
                obls.add(obligation);
            }
        }

        return obls;
    }

    @Override
    public Map<String, List<Rule>> getRulesWithEventSubject(String subject) throws PMException {
        Map<String, List<Rule>> rulesMap = new HashMap<>();
        for (Obligation obligation : policy.obligations().getAll()) {
            List<Rule> rules = obligation.getRules();
            for (Rule rule : rules) {
                if (rule.getEventPattern().getSubjectPattern().matches(subject, graphQuery)) {
                    List<Rule> matchingRules = rulesMap.getOrDefault(obligation.getName(), new ArrayList<>());
                    matchingRules.add(rule);
                    rulesMap.put(obligation.getName(), matchingRules);
                }
            }
        }

        return rulesMap;
    }

    @Override
    public Map<String, List<Rule>> getRulesWithEventOperation(String operation) throws PMException {
        Map<String, List<Rule>> rulesMap = new HashMap<>();
        for (Obligation obligation : policy.obligations().getAll()) {
            List<Rule> rules = obligation.getRules();
            for (Rule rule : rules) {
                if (rule.getEventPattern().getOperationPattern().matches(operation, graphQuery)) {
                    List<Rule> matchingRules = rulesMap.getOrDefault(obligation.getName(), new ArrayList<>());
                    matchingRules.add(rule);
                    rulesMap.put(obligation.getName(), matchingRules);
                }
            }
        }

        return rulesMap;
    }

    @Override
    public Map<String, List<Rule>> getRulesWithEventOperand(String operand) throws PMException {
        Map<String, List<Rule>> rulesMap = new HashMap<>();
        for (Obligation obligation : policy.obligations().getAll()) {
            List<Rule> rules = obligation.getRules();
            for (Rule rule : rules) {
                for (Pattern operandPattern : rule.getEventPattern().getOperandPatterns()) {
                    if (operandPattern.matches(operand, graphQuery)) {
                        List<Rule> matchingRules = rulesMap.getOrDefault(obligation.getName(), new ArrayList<>());
                        matchingRules.add(rule);
                        rulesMap.put(obligation.getName(), matchingRules);
                    }
                }
            }
        }

        return rulesMap;
    }

    @Override
    public List<Response> getMatchingEventResponses(EventContext evt) throws PMException {
        throw new PMException("TODO");
        /*List<Response> responses = new ArrayList<>();
        for (Obligation obligation : policy.obligations().getAll()) {
            for (Rule rule : obligation.getRules()) {
                if (evt.matchesPattern(rule.getEventPattern(), graphReview)) {
                    responses.add(rule.getResponse());
                }
            }
        }

        return responses;*/
    }

}
