package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.UserDefinedPML;
import gov.nist.csd.pm.pap.op.PolicyEvent;
import gov.nist.csd.pm.pap.op.userdefinedpml.CreateConstantEvent;
import gov.nist.csd.pm.pap.op.userdefinedpml.CreateFunctionEvent;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.exception.PMRuntimeException;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;
import java.util.Map;

public class TxUserDefinedPML implements UserDefinedPML, BaseMemoryTx {

    private final TxPolicyEventTracker txPolicyEventTracker;
    private final MemoryUserDefinedPML memoryUserDefinedPMLStore;

    public TxUserDefinedPML(TxPolicyEventTracker txPolicyEventTracker, MemoryUserDefinedPML memoryUserDefinedPMLStore) {
        this.txPolicyEventTracker = txPolicyEventTracker;
        this.memoryUserDefinedPMLStore = memoryUserDefinedPMLStore;
    }

    @Override
    public void rollback() {
        List<PolicyEvent> events = txPolicyEventTracker.getEvents();
        for (PolicyEvent event : events) {
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
        txPolicyEventTracker.trackPolicyEvent(new CreateFunctionEvent(functionDefinitionStatement));
    }

    @Override
    public void deleteFunction(String functionName) {
        txPolicyEventTracker.trackPolicyEvent(new TxEvents.MemoryDeleteFunctionEvent(memoryUserDefinedPMLStore.getFunctions().get(functionName)));
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
        txPolicyEventTracker.trackPolicyEvent(new CreateConstantEvent(constantName, constantValue));
    }

    @Override
    public void deleteConstant(String constName) {
        txPolicyEventTracker.trackPolicyEvent(new TxEvents.MemoryDeleteConstantEvent(constName, memoryUserDefinedPMLStore.getConstants().get(constName)));
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
