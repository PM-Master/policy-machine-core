package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.query.PMLQuery;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.Map;

public class PMLQueryAdjudicator implements PMLQuery {

    private UserContext userCtx;
    private PAP pap;

    public PMLQueryAdjudicator(UserContext userCtx, PAP pap) {
        this.userCtx = userCtx;
        this.pap = pap;
    }

    @Override
    public Map<String, FunctionDefinitionStatement> getFunctions() throws PMException {
        return pap.query().pml().getFunctions();
    }

    @Override
    public FunctionDefinitionStatement getFunction(String name) throws PMException {
        return pap.query().pml().getFunction(name);
    }

    @Override
    public Map<String, Value> getConstants() throws PMException {
        return pap.query().pml().getConstants();
    }

    @Override
    public Value getConstant(String name) throws PMException {
        return pap.query().pml().getConstant(name);
    }
}
