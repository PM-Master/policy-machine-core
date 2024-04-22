package gov.nist.csd.pm.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.exception.*;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Map;

/**
 * Policy Machine Language (PML) function and constant storage methods.
 */
public interface UserDefinedPML {

    /**
     * Add a new user defined PML function. The function will be available to any subsequent PML statements.
     * @param functionDefinitionStatement The function definition to add.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void createFunction(FunctionDefinitionStatement functionDefinitionStatement) throws PMException;

    /**
     * Remove a user defined PML function.
     * @param functionName The name of the function to be removed.
     * @throws PMBackendException If there is an error executing the command in the PIP.
     */
    void deleteFunction(String functionName) throws PMException;

    /**
     * Get all user defined PML functions.
     * @return A map of function names to function definitions for all functions.
     * @throws PMBackendException If there is an error executing the command in the PIP.
     */
    Map<String, FunctionDefinitionStatement> getFunctions() throws PMException;

    /**
     * Get the function definition with the given name.
     * @param name The name of the function to get.
     * @return The function definition of the function with the given name.
     * @throws PMLFunctionNotDefinedException If a function with the given name does not exist.
     * @throws PMBackendException             If there is an error executing the command in the PIP.
     */
    FunctionDefinitionStatement getFunction(String name) throws PMException;

    /**
     * Add a new user defined PML constant. The constant will be available to any subsequent PML statements.
     * @param constantName The name of the constant.
     * @param constantValue The value of the constant.
     * @throws PMLConstantAlreadyDefinedException If a constant with the given name is already defined.
     * @throws PMBackendException                 If there is an error executing the command in the PIP.
     */
    void createConstant(String constantName, Value constantValue) throws PMException;

    /**
     * Remove a PML constant.
     * @param constName The name of the constant to remove.
     * @throws PMBackendException If there is an error executing the command in the PIP.
     */
    void deleteConstant(String constName) throws PMException;

    /**
     * Get all user defined constants.
     * @return A map of constant names to constant values for all constants.
     * @throws PMBackendException If there is an error executing the command in the PIP.
     */
    Map<String, Value> getConstants() throws PMException;

    /**
     * Get the constant value with the given name.
     * @param name The name of the constant to get.
     * @return The value of the constant with the given name.
     * @throws PMLConstantNotDefinedException If a constant with the given name does not exist.
     * @throws PMBackendException             If there is an error executing the command in the PIP.
     */
    Value getConstant(String name) throws PMException;

    /**
     * Check the function being created.
     *
     * @param name The name of the new function.
     * @throws PMLFunctionAlreadyDefinedException If a function already exists with the given name.
     * @throws PMBackendException If there is an error in the backend implementation.
     */
    default void checkCreateFunctionInput(String name) throws PMException {
        if (getFunctions().containsKey(name)) {
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
    default boolean checkDeleteFunctionInput(String name) throws PMException {
        // if function does not exist the check returns false
        try {
            getFunction(name);
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
    default void checkGetFunctionInput(String name) throws PMException {
        if (!getFunctions().containsKey(name)) {
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
    default void checkCreateConstantInput(String name) throws PMException {
        if (getConstants().containsKey(name)) {
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
    default boolean checkDeleteConstantInput(String name) throws PMException {
        // if function does not exist the check returns false
        try {
            getConstant(name);
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
    default void checkGetConstantInput(String name) throws PMException {
        if (!getConstants().containsKey(name)) {
            throw new PMLConstantNotDefinedException(name);
        }
    }
}
