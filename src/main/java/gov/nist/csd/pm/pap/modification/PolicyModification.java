package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.serialization.PolicyDeserializer;
import gov.nist.csd.pm.common.serialization.PolicySerializer;

/**
 * General interface for managing a NGAC policy.
 */
public interface PolicyModification {

    /**
     * Get the graph component of the policy.
     * @return The Graph implementation.
     */
    GraphModification graph();

    /**
     * Get the prohibitions component of the policy.
     * @return The Prohibitions implementation.
     */
    ProhibitionsModification prohibitions();

    /**
     * Get the obligations component of the policy.
     * @return The Obligations implementation.
     */
    ObligationsModification obligations();

    /**
     * Get the user defined pml component of the policy.
     * @return The PML implementation.
     */
    PMLModification pml();

    /**
     * Deserialize the provided input into the current policy state.
     *
     * @param author The UserContext describing the author of the deserialized policy elements.
     * @param input The string representation of the policy to deserialize.
     * @param policyDeserializer The PolicyDeserializer to apply the input string to the policy.
     * @throws PMException If there is an error deserializing the provided policy input by the PolicyDeserializer.
     */
    void deserialize(UserContext author, String input, PolicyDeserializer policyDeserializer) throws PMException;

    /**
     * Reset the current policy state.
     * @throws PMException If there is an error resetting the policy state.
     */
    void reset() throws PMException;
}
