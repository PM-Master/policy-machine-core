package gov.nist.csd.pm.pap.query;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.exception.PMBackendException;
import gov.nist.csd.pm.pap.exception.PMLConstantNotDefinedException;
import gov.nist.csd.pm.pap.exception.PMLFunctionNotDefinedException;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Map;

public interface PMLQuery {

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

}
