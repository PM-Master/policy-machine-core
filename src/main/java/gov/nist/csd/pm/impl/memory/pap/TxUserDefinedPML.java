package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.UserDefinedPML;
import gov.nist.csd.pm.common.op.Operation;
import gov.nist.csd.pm.common.op.userdefinedpml.CreateConstantOp;
import gov.nist.csd.pm.common.op.userdefinedpml.CreateFunctionOp;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.exception.PMRuntimeException;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;
import java.util.Map;

public class TxUserDefinedPML implements UserDefinedPML, BaseMemoryTx {

    private final TxOpTracker txOpTracker;
    private final MemoryUserDefinedPML memoryUserDefinedPMLStore;

    public TxUserDefinedPML(TxOpTracker txOpTracker, MemoryUserDefinedPML memoryUserDefinedPMLStore) {
        this.txOpTracker = txOpTracker;
        this.memoryUserDefinedPMLStore = memoryUserDefinedPMLStore;
    }

    @Override
    public void rollback() {
        List<Operation> events = txOpTracker.getOperations();
        for (Operation event : events) {
            try {
                TxCmd<MemoryUserDefinedPML> txCmd = (TxCmd<MemoryUserDefinedPML>) TxCmd.eventToCmd(event);
                txCmd.rollback(memoryUserDefinedPMLStore);
            } catch (PMException e) {
                // throw runtime exception because there is noway back if the rollback fails
                throw new PMRuntimeException("", e);
            }
        }
    }

    @Override
    public void createFunction(FunctionDefinitionStatement functionDefinitionStatement) {
        txOpTracker.trackOp(new CreateFunctionOp(functionDefinitionStatement));
    }

    @Override
    public void deleteFunction(String functionName) {
        txOpTracker.trackOp(new TxOps.MemoryDeleteFunctionOp(memoryUserDefinedPMLStore.getFunctions().get(functionName)));
    }

    @Override
    public Map<String, FunctionDefinitionStatement> getFunctions() {
        return null;
    }

    @Override
    public FunctionDefinitionStatement getFunction(String name) {
        return null;
    }

    @Override
    public void createConstant(String constantName, Value constantValue) {
        txOpTracker.trackOp(new CreateConstantOp(constantName, constantValue));
    }

    @Override
    public void deleteConstant(String constName) {
        txOpTracker.trackOp(new TxOps.MemoryDeleteConstantOp(constName, memoryUserDefinedPMLStore.getConstants().get(constName)));
    }

    @Override
    public Map<String, Value> getConstants() {
        return null;
    }

    @Override
    public Value getConstant(String name) {
        return null;
    }
}
