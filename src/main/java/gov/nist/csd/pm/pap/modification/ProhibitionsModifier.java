package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.exception.ProhibitionContainerDoesNotExistException;
import gov.nist.csd.pm.pap.exception.ProhibitionDoesNotExistException;
import gov.nist.csd.pm.pap.exception.ProhibitionExistsException;
import gov.nist.csd.pm.pap.exception.ProhibitionSubjectDoesNotExistException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;

import static gov.nist.csd.pm.pap.modification.GraphModifier.checkAccessRightsValid;

public abstract class ProhibitionsModifier extends Modifier implements ProhibitionsModification {

    protected abstract void createInternal(String name,
                                           ProhibitionSubject subject,
                                           AccessRightSet accessRightSet,
                                           boolean intersection,
                                           ContainerCondition... containerConditions) throws PMException;
    protected abstract void updateInternal(String name,
                                           ProhibitionSubject subject,
                                           AccessRightSet accessRightSet,
                                           boolean intersection,
                                           ContainerCondition... containerConditions) throws PMException;
    protected abstract void deleteInternal(String name) throws PMException;

    @Override
    public void create(String name,
                       ProhibitionSubject subject,
                       AccessRightSet accessRightSet,
                       boolean intersection,
                       ContainerCondition... containerConditions) throws PMException {
        checkCreateInput(name, subject, accessRightSet, intersection, containerConditions);

        createInternal(name, subject, accessRightSet, intersection, containerConditions);
    }

    @Override
    public void update(String name,
                       ProhibitionSubject subject,
                       AccessRightSet accessRightSet,
                       boolean intersection,
                       ContainerCondition... containerConditions) throws PMException {
        checkUpdateInput(name, subject, accessRightSet, intersection, containerConditions);

        updateInternal(name, subject, accessRightSet, intersection, containerConditions);
    }

    @Override
    public void delete(String name) throws PMException {
        if(!checkDeleteInput(name)) {
            return;
        }

        deleteInternal(name);
    }

    /**
     * Check the prohibition being created.
     *
     * @param name The name of the prohibition.
     * @param subject The subject of the prohibition.
     * @param accessRightSet The denied access rights.
     * @param intersection The boolean flag indicating an evaluation of the union or intersection of the container conditions.
     * @param containerConditions The prohibition container conditions.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected void checkCreateInput(String name, ProhibitionSubject subject, AccessRightSet accessRightSet,
                                    boolean intersection, ContainerCondition... containerConditions) throws PMException {
        if (query().prohibitions().exists(name)) {
            throw new ProhibitionExistsException(name);
        }

        // check the prohibition parameters are valid
        checkAccessRightsValid(query().graph().getResourceAccessRights(), accessRightSet);
        checkProhibitionSubjectExists(subject);
        checkProhibitionContainersExist(containerConditions);
    }

    /**
     * Check the prohibition being updated.
     *
     * @param name The name of the prohibition.
     * @param subject The subject of the prohibition.
     * @param accessRightSet The denied access rights.
     * @param intersection The boolean flag indicating an evaluation of the union or intersection of the container conditions.
     * @param containerConditions The prohibition container conditions.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected void checkUpdateInput(String name, ProhibitionSubject subject, AccessRightSet accessRightSet,
                                    boolean intersection , ContainerCondition ... containerConditions) throws PMException {
        if (!query().prohibitions().exists(name)) {
            throw new ProhibitionDoesNotExistException(name);
        }

        // check the prohibition parameters are valid
        checkAccessRightsValid(query().graph().getResourceAccessRights(), accessRightSet);
        checkProhibitionSubjectExists(subject);
        checkProhibitionContainersExist(containerConditions);
    }

    /**
     * Check if the prohibition exists. If it doesn't, return false to indicate to the caller that execution should not
     * proceed.
     *
     * @param name The name of the prohibition.
     * @return True if the execution should proceed, false otherwise.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected boolean checkDeleteInput(String name) throws PMException {
        if (!query().prohibitions().exists(name)) {
            return false;
        }

        return true;
    }

    protected void checkProhibitionSubjectExists(ProhibitionSubject subject)
            throws PMException {
        if (subject.getType() != ProhibitionSubject.Type.PROCESS) {
            if (!query().graph().nodeExists(subject.getName())) {
                throw new ProhibitionSubjectDoesNotExistException(subject.getName());
            }
        }
    }

    protected void checkProhibitionContainersExist(ContainerCondition ... containerConditions)
            throws PMException {
        for (ContainerCondition container : containerConditions) {
            if (!query().graph().nodeExists(container.getName())) {
                throw new ProhibitionContainerDoesNotExistException(container.getName());
            }
        }
    }

}
