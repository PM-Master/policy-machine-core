package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.epp.EventEmitter;
import gov.nist.csd.pm.epp.EventProcessor;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.op.obligation.CreateObligationOp;
import gov.nist.csd.pm.common.op.obligation.DeleteObligationOp;
import gov.nist.csd.pm.common.op.obligation.UpdateObligationOp;
import gov.nist.csd.pm.pdp.adjudicator.AdjudicatorObligations;
import gov.nist.csd.pm.pap.Obligations;
import gov.nist.csd.pm.common.op.Operation;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.obligation.event.subject.Subject;
import gov.nist.csd.pm.common.obligation.event.target.Target;

import java.util.List;

class PDPObligations implements Obligations, EventEmitter {
    private UserContext userCtx;
    private AdjudicatorObligations adjudicator;
    private PAP pap;
    private EventProcessor listener;

    public PDPObligations(UserContext userCtx, AdjudicatorObligations adjudicator, PAP pap, EventProcessor listener) {
        this.userCtx = userCtx;
        this.adjudicator = adjudicator;
        this.pap = pap;
        this.listener = listener;
    }

    @Override
    public void create(UserContext author, String name, Rule... rules) throws PMException {
        adjudicator.create(author, name, rules);

        pap.policy().obligations().create(author, name, rules);

        emitObligationEvent(new CreateObligationOp(author, name, List.of(rules)), rules);
    }

    private void emitObligationEvent(Operation event, Rule... rules) throws PMException {
        // emit events for each rule
        for (Rule rule : rules) {
            // emit event for the subject
            Subject subject = rule.getEventPattern().getSubject();
            for (String user : subject.getSubjects()) {
                emitEvent(new EventContext(userCtx, event));
            }

            // emit event for each target
            Target target = rule.getEventPattern().getTarget();
            for (String policyElement : target.getTargets()) {
                emitEvent(new EventContext(userCtx, event));
            }
        }
    }

    @Override
    public void update(UserContext author, String name, Rule... rules) throws PMException {
        adjudicator.update(author, name, rules);

        pap.policy().obligations().update(author, name, rules);

        emitObligationEvent(
                new UpdateObligationOp(author, name, List.of(rules)),
                rules
        );
    }

    @Override
    public void delete(String name) throws PMException {
        if (!exists(name)) {
            return;
        }

        adjudicator.delete(name);

        // get the obligation to use in the EPP before it is deleted
        Obligation obligation = get(name);

        pap.policy().obligations().delete(name);

        emitDeleteObligationEvent(obligation);
    }

    private void emitDeleteObligationEvent(Obligation obligation) throws PMException {
        emitObligationEvent(
                new DeleteObligationOp(obligation.getName()),
                obligation.getRules().toArray(Rule[]::new)
        );
    }

    @Override
    public List<Obligation> getAll() throws PMException {
        return adjudicator.getAll();
    }

    @Override
    public boolean exists(String name) throws PMException {
        return adjudicator.exists(name);
    }

    @Override
    public Obligation get(String name) throws PMException {
        return adjudicator.get(name);
    }

    @Override
    public void addEventListener(EventProcessor listener) {

    }

    @Override
    public void removeEventListener(EventProcessor listener) {

    }

    @Override
    public void emitEvent(EventContext event) throws PMException {
        this.listener.processEvent(event);
    }
}
