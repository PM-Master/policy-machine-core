package gov.nist.csd.pm.impl.mysql;

import gov.nist.csd.pm.policy.exceptions.PMBackendException;

public class MysqlPolicyException extends PMBackendException {

    public MysqlPolicyException(String message) {
        super(message);
    }
}
