package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.pap.exception.ObligationDoesNotExistException;
import gov.nist.csd.pm.pap.query.ObligationsQuerier;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.ArrayList;
import java.util.List;

public class MemoryObligationsQuerier extends ObligationsQuerier {

    private MemoryPolicy memoryPolicy;

    public MemoryObligationsQuerier(MemoryPolicy memoryPolicy) {
        this.memoryPolicy = memoryPolicy;
    }

    @Override
    public List<Obligation> getAll() throws PMException {
        return memoryPolicy.obligations;
    }

    @Override
    public boolean exists(String name) throws PMException {
        for (Obligation o : memoryPolicy.obligations) {
            if (o.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<Obligation> getObligationsWithAuthor(UserContext userCtx) throws PMException {
        List<Obligation> obls = new ArrayList<>();
        for (Obligation obligation : getAll()) {
            if (obligation.getAuthor().equals(userCtx)) {
                obls.add(obligation);
            }
        }

        return obls;
    }

    @Override
    public Obligation getInternal(String name) throws PMException {
        for (Obligation obligation : memoryPolicy.obligations) {
            if (obligation.getName().equals(name)) {
                return obligation;
            }
        }

        throw new ObligationDoesNotExistException(name);
    }
}
