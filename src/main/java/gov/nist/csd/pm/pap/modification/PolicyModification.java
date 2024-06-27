package gov.nist.csd.pm.pap.modification;

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

}
