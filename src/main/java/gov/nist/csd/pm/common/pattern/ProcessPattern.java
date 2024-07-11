package gov.nist.csd.pm.common.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;

public class ProcessPattern extends Pattern {

    private String process;

    public ProcessPattern(String process) {
        this.process = process;
    }

    @Override
    public boolean matches(Object value, PAP pap) throws PMException {
        return process.equals(value);
    }

    @Override
    public ReferencedNodes getReferencedNodes() {
        return new ReferencedNodes(false);
    }
}
