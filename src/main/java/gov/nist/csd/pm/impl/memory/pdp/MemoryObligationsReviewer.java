package gov.nist.csd.pm.impl.memory.pdp;

import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.pap.Policy;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.obligation.event.subject.AnyUserSubject;
import gov.nist.csd.pm.common.obligation.event.subject.Subject;
import gov.nist.csd.pm.common.obligation.event.target.AnyTarget;
import gov.nist.csd.pm.common.obligation.event.target.Target;
import gov.nist.csd.pm.pap.ObligationsReview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryObligationsReviewer implements ObligationsReview {

    private final Policy policy;
    private final MemoryGraphReviewer graphReviewer;

    public MemoryObligationsReviewer(Policy policy, MemoryGraphReviewer graphReviewer) {
        this.policy = policy;
        this.graphReviewer = graphReviewer;
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
    public Map<String, List<Rule>> getRulesWithEventSubject(String attribute) throws PMException {
        Map<String, List<Rule>> rulesMap = new HashMap<>();
        for (Obligation obligation : policy.obligations().getAll()) {
            List<Rule> rules = obligation.getRules();
            for (Rule rule : rules) {
                Subject subject = rule.getEventPattern().getSubject();

                if (subject instanceof AnyUserSubject ||
                        subject.matches(new UserContext(attribute), graphReviewer)) {
                    List<Rule> matchingRules = rulesMap.getOrDefault(obligation.getName(), new ArrayList<>());
                    matchingRules.add(rule);
                    rulesMap.put(obligation.getName(), matchingRules);
                }
            }
        }

        return rulesMap;
    }

    @Override
    public Map<String, List<Rule>> getRulesWithEventTarget(String attribute) throws PMException {
        Map<String, List<Rule>> rulesMap = new HashMap<>();
        for (Obligation obligation : policy.obligations().getAll()) {
            List<Rule> rules = obligation.getRules();
            for (Rule rule : rules) {
                Target target = rule.getEventPattern().getTarget();

                if (target instanceof AnyTarget || target.matches(attribute, graphReviewer)) {
                    List<Rule> matchingRules = rulesMap.getOrDefault(obligation.getName(), new ArrayList<>());
                    matchingRules.add(rule);
                    rulesMap.put(obligation.getName(), matchingRules);
                }
            }
        }

        return rulesMap;
    }

    @Override
    public List<Response> getMatchingEventResponses(EventContext evt) throws PMException {
        List<Response> responses = new ArrayList<>();
        for (Obligation obligation : policy.obligations().getAll()) {
            for (Rule rule : obligation.getRules()) {
                if (evt.matchesPattern(rule.getEventPattern(), graphReviewer)) {
                    responses.add(rule.getResponse());
                }
            }
        }

        return responses;
    }

}
