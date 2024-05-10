package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.epp.EventEmitter;
import gov.nist.csd.pm.epp.EventProcessor;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.obligation.CreateObligationOp;
import gov.nist.csd.pm.pap.op.obligation.DeleteObligationOp;
import gov.nist.csd.pm.pap.op.obligation.UpdateObligationOp;
import gov.nist.csd.pm.pdp.adjudicator.AdjudicatorObligationsModification;
import gov.nist.csd.pm.pap.modification.ObligationsModification;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;

import java.util.List;

class PDPObligationsModification implements ObligationsModification, EventEmitter {
    private UserContext userCtx;
    private AdjudicatorObligationsModification adjudicator;
    private PAP pap;
    private EventProcessor listener;

    public PDPObligationsModification(UserContext userCtx, AdjudicatorObligationsModification adjudicator, PAP pap, EventProcessor listener) {
        this.userCtx = userCtx;
        this.adjudicator = adjudicator;
        this.pap = pap;
        this.listener = listener;
    }

    @Override
    public void create(UserContext author, String name, Rule... rules) throws PMException {
        adjudicator.create(author, name, rules);

        pap.modify().obligations().create(author, name, rules);

        emitObligationEvent(new CreateObligationOp(author, name, List.of(rules)), rules);
    }

    private void emitObligationEvent(Operation event, Rule... rules) throws PMException {
        // emit events for each rule
        for (Rule rule : rules) {
            // emit event for the subject
            throw new RuntimeException("TODO");
            /*Subject subject = rule.getEventPattern().getSubject();
            for (String user : subject.getSubjects()) {
                emitEvent(new EventContext(userCtx, event));
            }

            // emit event for each target
            Target target = rule.getEventPattern().getTarget();
            for (String policyElement : target.getTargets()) {
                emitEvent(new EventContext(userCtx, event));
            }*/
        }
    }

    @Override
    public void update(UserContext author, String name, Rule... rules) throws PMException {
        adjudicator.update(author, name, rules);

        pap.modify().obligations().update(author, name, rules);

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

        pap.modify().obligations().delete(name);

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
    public void addEventListener(EventProcessor processor) {

    }

    @Override
    public void removeEventListener(EventProcessor processor) {

    }

    @Override
    public void emitEvent(EventContext event) throws PMException {
        this.listener.processEvent(event);
    }
}
