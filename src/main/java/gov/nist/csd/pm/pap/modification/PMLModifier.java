package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.exception.*;
import gov.nist.csd.pm.pap.query.PolicyQuery;

public abstract class PMLModifier extends Modifier {

    public PMLModifier(PolicyQuery policyQuery) {
        super(policyQuery);
    }

    /**
     * Check the function being created.
     *
     * @param name The name of the new function.
     * @throws PMLFunctionAlreadyDefinedException If a function already exists with the given name.
     * @throws PMBackendException If there is an error in the backend implementation.
     */
    protected void checkCreateFunctionInput(String name) throws PMException {
        if (querier.pml().getFunctions().containsKey(name)) {
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
            querier.pml().getFunction(name);
            return true;
        } catch (PMLFunctionNotDefinedException e) {
            return false;
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
        if (!querier.pml().getFunctions().containsKey(name)) {
            throw new PMLFunctionNotDefinedException(name);
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
        if (querier.pml().getConstants().containsKey(name)) {
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
            querier.pml().getConstant(name);
            return true;
        } catch (PMLConstantNotDefinedException e) {
            return false;
        }
    }

    /**
     * Check the constant being retrieved.
     *
     * @param name The name of the constant.
     * @throws PMLConstantNotDefinedException If the given constant is not defined.
     * @throws PMBackendException If there is an error in the backend implementation.
     */
    protected void checkGetConstantInput(String name) throws PMException {
        if (!querier.pml().getConstants().containsKey(name)) {
            throw new PMLConstantNotDefinedException(name);
        }
    }
}
