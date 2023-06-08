package gov.nist.csd.pm.pap.query;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.exception.ProhibitionDoesNotExistException;

public abstract class ProhibitionsQuerier implements ProhibitionsQuery {

    protected abstract Prohibition getInternal(String name) throws PMException;

    @Override
    public Prohibition get(String name) throws PMException {
        checkProhibitionExists(name);

        return getInternal(name);
    }

    /**
     * Check that the given prohibition exists.
     * @param name The prohibition name to check.
     */
    protected void checkProhibitionExists(String name) throws PMException {
        if (!exists(name)) {
            throw new ProhibitionDoesNotExistException(name);
        }
    }
}
