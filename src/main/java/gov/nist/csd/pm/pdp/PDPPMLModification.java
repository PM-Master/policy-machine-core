package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.epp.EventEmitter;
import gov.nist.csd.pm.epp.EventProcessor;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.userdefinedpml.CreateConstantOp;
import gov.nist.csd.pm.pap.op.userdefinedpml.CreateFunctionOp;
import gov.nist.csd.pm.pap.op.userdefinedpml.DeleteConstantOp;
import gov.nist.csd.pm.pap.op.userdefinedpml.DeleteFunctionOp;
import gov.nist.csd.pm.pdp.adjudicator.AdjudicatorPMLModification;
import gov.nist.csd.pm.pap.modification.PMLModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Map;

class PDPPMLModification implements PMLModification, EventEmitter {
    private UserContext userCtx;
    private AdjudicatorPMLModification adjudicator;
    private PAP pap;
    private EventProcessor listener;

    public PDPPMLModification(UserContext userCtx, AdjudicatorPMLModification adjudicator, PAP pap, EventProcessor listener) {
        this.userCtx = userCtx;
        this.adjudicator = adjudicator;
        this.pap = pap;
        this.listener = listener;
    }

    @Override
    public void createFunction(FunctionDefinitionStatement functionDefinitionStatement) throws PMException {
        adjudicator.createFunction(functionDefinitionStatement);

        pap.policy().pml().createFunction(functionDefinitionStatement);

        emitEvent(new EventContext(userCtx, new CreateFunctionOp(functionDefinitionStatement)));
    }

    @Override
    public void deleteFunction(String functionName) throws PMException {
        adjudicator.deleteFunction(functionName);

        pap.policy().pml().deleteFunction(functionName);

        emitEvent(new EventContext(userCtx, new DeleteFunctionOp(functionName)));

    }

    @Override
    public Map<String, FunctionDefinitionStatement> getFunctions() throws PMException {
        return pap.policy().pml().getFunctions();
    }

    @Override
    public FunctionDefinitionStatement getFunction(String name) throws PMException {
        return pap.policy().pml().getFunction(name);
    }

    @Override
    public void createConstant(String constantName, Value constantValue) throws PMException {
        adjudicator.createConstant(constantName, constantValue);

        pap.policy().pml().createConstant(constantName, constantValue);

        emitEvent(new EventContext(userCtx, new CreateConstantOp(constantName, constantValue)));

    }

    @Override
    public void deleteConstant(String constName) throws PMException {
        adjudicator.deleteConstant(constName);

        pap.policy().pml().deleteConstant(constName);

        emitEvent(new EventContext(userCtx, new DeleteConstantOp(constName)));
    }

    @Override
    public Map<String, Value> getConstants() throws PMException {
        return pap.policy().pml().getConstants();
    }

    @Override
    public Value getConstant(String name) throws PMException {
        return pap.policy().pml().getConstant(name);
    }

    @Override
    public void addEventListener(EventProcessor listener) {

    }

    @Override
    public void removeEventListener(EventProcessor listener) {

    }

    @Override
    public void emitEvent(EventContext event) throws PMException {
        this.listener.processEvent(event);
    }
}
