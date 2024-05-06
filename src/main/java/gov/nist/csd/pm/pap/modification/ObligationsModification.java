package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.Rule;

/**
 * NGAC obligation methods.
 */
public interface ObligationsModification {

    /**
     * Create a new obligation with the given author, name, and rules. The author of the obligation is the user that the
     * responses will be executed as in the EPP. This means the author will need the privileges to carry out each action
     * in the response at the time it's executed. If they do not have sufficient privileges no action in the response
     * will be executed. <p>
     *
     * @param author The user/process that is creating the obligation.
     * @param name The name of the obligation.
     * @param rules The rules of the obligation.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void create(UserContext author, String name, Rule... rules) throws PMException;

    /**
     * Update the author and rules of the obligation with the given name. This will overwrite any existing rules to the rules
     * provided and update the existing author. <p>
     *
     * @param author The user/process that updated the obligation.
     * @param name The name of the obligation to update.
     * @param rules The updated obligation rules.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void update(UserContext author, String name, Rule... rules) throws PMException;

    /**
     * Delete the obligation with the given name. If the obligation does not exist, no exception is thrown as this is
     * the desired state. <p>
     *
     * @param name The name of the obligation to delete.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void delete(String name) throws PMException;

}
