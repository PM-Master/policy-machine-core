package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.pap.exception.PMException;

public interface PDPTxRunner {
    Object run(PDPTx policy) throws PMException;
}
