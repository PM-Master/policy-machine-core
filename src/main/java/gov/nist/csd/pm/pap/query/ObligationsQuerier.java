package gov.nist.csd.pm.pap.query;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.pap.exception.ObligationDoesNotExistException;

public abstract class ObligationsQuerier implements ObligationsQuery{

    protected abstract Obligation getInternal(String name) throws PMException;

    @Override
    public Obligation get(String name) throws PMException {
        checkObligationExists(name);

        return getInternal(name);
    }

    /**
     * Check that the given prohibition exists.
     * @param name The prohibition name to check.
     */
    protected void checkObligationExists(String name) throws PMException {
        if (!exists(name)) {
            throw new ObligationDoesNotExistException(name);
        }
    }

}
