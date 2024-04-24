package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.Prohibitions;
import gov.nist.csd.pm.impl.memory.pap.unmodifiable.UnmodifiableProhibition;
import gov.nist.csd.pm.pdp.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.common.tx.Transactional;
import gov.nist.csd.pm.pap.exception.*;

import java.util.*;


class MemoryProhibitions extends MemoryStore<TxProhibitions> implements Prohibitions, Transactional, BaseMemoryTx {

    private Map<String, List<Prohibition>> prohibitions;
    private MemoryGraph graph;

    public MemoryProhibitions() {
        this.prohibitions = Collections.unmodifiableMap(new HashMap<>());
    }

    public void setMemoryGraph(MemoryGraph graph) {
        this.graph = graph;
    }

    public void clear() {
        this.prohibitions = Collections.unmodifiableMap(new HashMap<>());
    }

    @Override
    public void beginTx() {
        if (tx == null) {
            tx = new MemoryTx<>(false, 0, new TxProhibitions(new TxOpTracker(), this));
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
    public void create(String name,
                       ProhibitionSubject subject,
                       AccessRightSet accessRightSet,
                       boolean intersection,
                       ContainerCondition... containerConditions)
            throws PMException {
        checkCreateInput(graph, name, subject, accessRightSet, intersection, containerConditions);

        // log the command if in a tx
        handleTxIfActive(tx -> tx.create(name, subject, accessRightSet, intersection, containerConditions));

        // add the prohibition to the data structure
        createInternal(name, subject, accessRightSet, intersection, containerConditions);
    }

    @Override
    public void update(String name,
                       ProhibitionSubject subject,
                       AccessRightSet accessRightSet,
                       boolean intersection,
                       ContainerCondition... containerConditions)
            throws PMException {
        checkUpdateInput(graph, name, subject, accessRightSet, intersection, containerConditions);

        // log the command if in a tx
        handleTxIfActive(tx -> tx.update(name, subject, accessRightSet, intersection, containerConditions));

        deleteInternal(name);
        createInternal(name, subject, accessRightSet, intersection, containerConditions);
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
    public Map<String, List<Prohibition>> getAll() {
        Map<String, List<Prohibition>> retProhibitions = new HashMap<>();
        for (String subject : prohibitions.keySet()) {
            retProhibitions.put(subject, prohibitions.get(subject));
        }

        return retProhibitions;
    }

    @Override
    public boolean exists(String name) {
        for (Map.Entry<String, List<Prohibition>> e : prohibitions.entrySet()) {
            for (Prohibition p : e.getValue()) {
                if (p.getName().equals(name)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<Prohibition> getWithSubject(String subject) {
        List<Prohibition> subjectPros = prohibitions.get(subject);
        if (subjectPros == null) {
            return new ArrayList<>();
        }

        return subjectPros;
    }

    @Override
    public Prohibition get(String name) throws PMException {
        checkGetInput(name);

        for (String subject : prohibitions.keySet()) {
            List<Prohibition> subjectPros = prohibitions.get(subject);
            for (Prohibition p : subjectPros) {
                if (p.getName().equals(name)) {
                    return p;
                }
            }
        }

        throw new ProhibitionDoesNotExistException(name);
    }

    private void createInternal(String name,
                                ProhibitionSubject subject,
                                AccessRightSet accessRightSet,
                                boolean intersection,
                                ContainerCondition... containerConditions) {
        List<Prohibition> existingPros = new ArrayList<>(prohibitions.getOrDefault(
                subject.getName(),
                new ArrayList<>()
        ));
        existingPros.add(new UnmodifiableProhibition(
                name,
                subject,
                accessRightSet,
                intersection,
                Arrays.asList(containerConditions)
        ));

        HashMap<String, List<Prohibition>> m = new HashMap<>(this.prohibitions);
        m.put(subject.getName(), Collections.unmodifiableList(existingPros));

        this.prohibitions = Collections.unmodifiableMap(m);
    }

    private void deleteInternal(String name) {
        for (String subject : prohibitions.keySet()) {
            List<Prohibition> ps = new ArrayList<>(prohibitions.get(subject));
            if (ps.removeIf(p -> p.getName().equals(name))) {
                HashMap<String, List<Prohibition>> m = new HashMap<>(this.prohibitions);
                m.put(subject, Collections.unmodifiableList(ps));
                this.prohibitions = Collections.unmodifiableMap(m);
            }
        }
    }
}
