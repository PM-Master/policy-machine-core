package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.Obligations;
import gov.nist.csd.pm.impl.memory.pap.unmodifiable.UnmodifiableObligation;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.tx.Transactional;
import gov.nist.csd.pm.pap.exception.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class MemoryObligations extends MemoryStore<TxObligations> implements Obligations, Transactional, BaseMemoryTx {

    private List<Obligation> obligations;
    private MemoryGraph graph;

    public MemoryObligations() {
        this.obligations = Collections.unmodifiableList(new ArrayList<>());
    }

    public void setMemoryGraph(MemoryGraph graph) {
        this.graph = graph;
    }

    public void clear() {
        this.obligations = Collections.unmodifiableList(new ArrayList<>());
    }

    @Override
    public void beginTx() {
        if (tx == null) {
            tx = new MemoryTx<>(false, 0, new TxObligations(new TxOpTracker(), this));
        }

        tx.beginTx();
    }

    @Override
    public void commit() {
        tx.commit();
    }

    @Override
    public void rollback() {
        tx.getStore().rollback();

        tx.rollback();
    }

    @Override
    public void create(UserContext author, String name, Rule... rules) throws PMException {
        checkCreateInput(graph, author, name, rules);

        // log the command if in a tx
        handleTxIfActive(tx -> tx.create(author, name, rules));

        createInternal(obligations.size() - 1, author, name, rules);
    }

    @Override
    public void update(UserContext author, String name, Rule... rules) throws PMException {
        checkUpdateInput(graph, author, name, rules);

        // log the command if in a tx
        handleTxIfActive(tx -> tx.update(author, name, rules));

        for (int i = 0; i < obligations.size(); i++) {
            if (obligations.get(i).getName().equals(name)) {
                deleteInternal(name);
                createInternal(i, author, name, rules);

                return;
            }
        }
    }

    @Override
    public void delete(String name) throws PMException {
        if (!checkDeleteInput(name)) {
            return;
        }

        // log the command if in a tx
        handleTxIfActive(tx -> tx.delete(name));

        deleteInternal(name);
    }

    @Override
    public List<Obligation> getAll() {
        return obligations;
    }

    @Override
    public boolean exists(String name) {
        for (Obligation o : obligations) {
            if (o.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Obligation get(String name)
            throws PMException {
        checkGetInput(name);

        for (Obligation obligation : obligations) {
            if (obligation.getName().equals(name)) {
                return obligation;
            }
        }

        // this shouldn't be reached due to the checkGet call, but just to be safe
        throw new ObligationDoesNotExistException(name);
    }

    private void createInternal(int index, UserContext author, String name, Rule... rules) {
        ArrayList<Obligation> copy = new ArrayList<>(obligations);
        copy.add(Math.max(index, 0), new UnmodifiableObligation(author, name, Arrays.asList(rules)));
        this.obligations = Collections.unmodifiableList(copy);
    }

    private void deleteInternal(String name) {
        ArrayList<Obligation> copy = new ArrayList<>(obligations);
        copy.removeIf(o -> o.getName().equals(name));
        this.obligations = Collections.unmodifiableList(copy);
    }
}
