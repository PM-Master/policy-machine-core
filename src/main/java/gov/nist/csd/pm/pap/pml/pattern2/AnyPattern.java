package gov.nist.csd.pm.pap.pml.pattern2;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.pattern.ReferencedNodes;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;

public class AnyPattern extends PMLPattern {


    public AnyPattern(VisitorContext visitorContext,
                      PMLParser.PatternContext ctx) {
        super(visitorContext, ctx);
    }

    @Override
    public boolean matches(Object value, PAP pap) throws PMException {
        return true;
    }

    @Override
    public ReferencedNodes getReferencedNodes() {
        return new ReferencedNodes(true);
    }
}
