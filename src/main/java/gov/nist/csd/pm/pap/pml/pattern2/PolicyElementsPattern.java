package gov.nist.csd.pm.pap.pml.pattern2;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.pattern.ReferencedNodes;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.pattern.PMLPattern;
import gov.nist.csd.pm.pap.pml.pattern.PMLPatternReqCap;
import gov.nist.csd.pm.pap.pml.pattern.PatternExpression;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public class PolicyElementsPattern extends PMLPattern {
    public PolicyElementsPattern(String varName, List<Value> argValues,
                                 List<PMLPatternReqCap> capMap)
            throws PMException {
        super(varName, argValues, capMap);
    }

    @Override
    public boolean matches(Object value, PAP pap) throws PMException {
        return false;
    }

    @Override
    public ReferencedNodes getReferencedNodes() {
        return null;
    }

    @Override
    public PatternExpression toPatternExpression() {
        return null;
    }
}
