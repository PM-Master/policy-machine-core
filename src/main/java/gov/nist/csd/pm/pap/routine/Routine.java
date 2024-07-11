package gov.nist.csd.pm.pap.routine;

import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.ArrayList;
import java.util.List;

public abstract class Routine<T> extends Operation<T> {

    public Routine(String opName) {
        super(opName, new ArrayList<>());
    }

}
