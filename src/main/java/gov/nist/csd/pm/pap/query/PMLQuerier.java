package gov.nist.csd.pm.pap.query;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.exception.*;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

public abstract class PMLQuerier implements PMLQuery{

    protected abstract FunctionDefinitionStatement getFunctionInternal(String name) throws PMException;
    protected abstract Value getConstantInternal(String name) throws PMException;

    @Override
    public FunctionDefinitionStatement getFunction(String name) throws PMException {
        checkGetFunctionInput(name);
        return getFunctionInternal(name);
    }

    @Override
    public Value getConstant(String name) throws PMException {
        checkGetConstantInput(name);
        return getConstantInternal(name);
    }

    /**
     * Check the constant being retrieved.
     *
     * @param name The name of the constant.
     * @throws PMLConstantNotDefinedException If the given constant is not defined.
     * @throws PMBackendException If there is an error in the backend implementation.
     */
    protected void checkGetConstantInput(String name) throws PMException {
        if (!getConstants().containsKey(name)) {
            throw new PMLConstantNotDefinedException(name);
        }
    }

    /**
     * Check the function being retrieved.
     *
     * @param name The name of the function to get.
     * @throws PMLFunctionNotDefinedException If a function with the given name is not defined.
     * @throws PMBackendException If there is an error in the backend implementation.
     */
    protected void checkGetFunctionInput(String name) throws PMException {
        if (!getFunctions().containsKey(name)) {
            throw new PMLFunctionNotDefinedException(name);
        }
    }

}
