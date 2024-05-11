package gov.nist.csd.pm.pap.op.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.exception.NodeDoesNotExistException;
import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.pml.pattern.PatternExpression;
import gov.nist.csd.pm.pap.query.PolicyQuery;

import java.io.Serializable;

public abstract class Pattern implements Serializable {

    public abstract boolean matches(Object value, PAP pap) throws PMException;

    public abstract ReferencedNodes getReferencedNodes();

    public abstract PatternExpression toPatternExpression();

    public void checkReferencedNodesExist(PolicyQuery querier) throws PMException {
        ReferencedNodes ref = getReferencedNodes();
        for (String entity : ref.nodes()) {
            if (!querier.graph().nodeExists(entity)) {
                throw new NodeDoesNotExistException(entity);
            }
        }
    }
}
