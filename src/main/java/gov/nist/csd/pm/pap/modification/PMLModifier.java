package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.exception.*;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

public abstract class PMLModifier extends Modifier implements PMLModification {

    protected abstract void createFunctionInternal(FunctionDefinitionStatement func) throws PMException;
    protected abstract void deleteFunctionInternal(String name) throws PMException;
    protected abstract void createConstantInternal(String name, Value value) throws PMException;
    protected abstract void deleteConstantInternal(String name) throws PMException;

    @Override
    public void createFunction(FunctionDefinitionStatement functionDefinitionStatement) throws PMException {
        checkCreateFunctionInput(functionDefinitionStatement.getSignature().getFunctionName());

        createFunctionInternal(functionDefinitionStatement);
    }

    @Override
    public void deleteFunction(String functionName) throws PMException {
        if(!checkDeleteFunctionInput(functionName)) {
            return;
        }

        deleteFunctionInternal(functionName);
    }

    @Override
    public void createConstant(String constantName, Value constantValue) throws PMException {
        checkCreateConstantInput(constantName);

        createConstantInternal(constantName, constantValue);
    }

    @Override
    public void deleteConstant(String constName) throws PMException {
        if(!checkDeleteConstantInput(constName)) {
            return;
        }

        deleteConstantInternal(constName);
    }

    /**
     * Check the function being created.
     *
     * @param name The name of the new function.
     * @throws PMLFunctionAlreadyDefinedException If a function already exists with the given name.
     * @throws PMBackendException If there is an error in the backend implementation.
     */
    protected void checkCreateFunctionInput(String name) throws PMException {
        if (query().pml().getFunctions().containsKey(name)) {
            throw new PMLFunctionAlreadyDefinedException(name);
        }
    }

    /**
     * Check the function being deleted. If the function does not exist return false to indicate to the caller that
     * execution should not proceed.
     *
     * @param name The name of the function being deleted.
     * @return True if the execution should proceed, false otherwise.
     * @throws PMBackendException If there is an error in the backend implementation.
     */
    protected boolean checkDeleteFunctionInput(String name) throws PMException {
        // if function does not exist the check returns false
        try {
            query().pml().getFunction(name);
            return true;
        } catch (PMLFunctionNotDefinedException e) {
            return false;
        }
    }

    /**
     * Check the constant being created.
     *
     * @param name The name of the new constant.
     * @throws PMLConstantAlreadyDefinedException If a constant already has the given name.
     * @throws PMBackendException If there is an error in the backend implementation.
     */
    protected void checkCreateConstantInput(String name) throws PMException {
        if (query().pml().getConstants().containsKey(name)) {
            throw new PMLConstantAlreadyDefinedException(name);
        }
    }

    /**
     * Check the constant being deleted. If the constant does not exist return false to indicate to the caller that
     * execution should not proceed.
     *
     * @param name The name of the constant being deleted.
     * @return True if the execution should proceed, false otherwise.
     * @throws PMBackendException If there is an error in the backend implementation.
     */
    protected boolean checkDeleteConstantInput(String name) throws PMException {
        // if function does not exist the check returns false
        try {
            query().pml().getConstant(name);
            return true;
        } catch (PMLConstantNotDefinedException e) {
            return false;
        }
    }
}
