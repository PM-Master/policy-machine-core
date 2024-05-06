package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;

/**
 * NGAC prohibition methods.
 */
public interface ProhibitionsModification {

    /**
     * Create a new prohibition.
     *
     * @param name the identifier of this prohibition.
     * @param subject ths subject of the prohibition (user, user attribute, or process).
     * @param accessRightSet the access rights to be denied
     * @param intersection a boolean flag that determines if the intersection of the containers should be denied or not.
     * @param containerConditions the containers to deny the subject access to.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void create(String name, ProhibitionSubject subject, AccessRightSet accessRightSet,
                boolean intersection, ContainerCondition... containerConditions) throws PMException;

    /**
     * Update an existing prohibition.
     *
     * @param name the identifier of this prohibition.
     * @param subject ths subject of the prohibition (user, user attribute, or process).
     * @param accessRightSet the access rights to be denied
     * @param intersection a boolean flag that determines if the intersection of the containers should be denied or not.
     * @param containerConditions the containers to deny the subject access to.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void update(String name, ProhibitionSubject subject, AccessRightSet accessRightSet,
                boolean intersection, ContainerCondition... containerConditions) throws PMException;

    /**
     * Delete the prohibition with the given name. No exception will be thrown if the prohibition does not exist.
     *
     * @param name The name of the prohibition to delete.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void delete(String name) throws PMException;

}
