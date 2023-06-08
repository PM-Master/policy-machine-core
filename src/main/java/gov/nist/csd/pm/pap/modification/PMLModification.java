package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.exception.*;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Map;

/**
 * Policy Machine Language (PML) function and constant storage methods.
 */
public interface PMLModification {

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
}
