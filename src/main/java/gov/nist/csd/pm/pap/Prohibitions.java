package gov.nist.csd.pm.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.exception.*;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.Graph.checkAccessRightsValid;

/**
 * NGAC prohibition methods.
 */
public interface Prohibitions {

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

    /**
     * Get all prohibitions.
     *
     * @return All prohibitions.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    Map<String, List<Prohibition>> getAll() throws PMException;

    /**
     * Check if a prohibition exists with the given name.
     *
     * @param name The name of the prohibition to check.
     * @return True if a prohibition exists with the given name, false otherwise.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    boolean exists(String name) throws PMException;

    /**
     * Get prohibitions with the given subject.
     * @param subject The subject to get the prohibitions for (user, user attribute, process)
     * @return The prohibitions with the given subject.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    List<Prohibition> getWithSubject(String subject) throws PMException;

    /**
     * Get the prohibition with the given name.
     * @param name The public abstract of the prohibition to get.
     * @return The prohibition with the given public abstract.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    Prohibition get(String name) throws PMException;

    /**
     * Check the prohibition being created.
     *
     * @param graph The GraphStore used to check the subject and containers exist.
     * @param name The name of the prohibition.
     * @param subject The subject of the prohibition.
     * @param accessRightSet The denied access rights.
     * @param intersection The boolean flag indicating an evaluation of the union or intersection of the container conditions.
     * @param containerConditions The prohibition container conditions.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default void checkCreateInput(Graph graph, String name, ProhibitionSubject subject, AccessRightSet accessRightSet,
                                  boolean intersection, ContainerCondition ... containerConditions) throws PMException {
        if (exists(name)) {
            throw new ProhibitionExistsException(name);
        }

        // check the prohibition parameters are valid
        checkAccessRightsValid(graph.getResourceAccessRights(), accessRightSet);
        checkProhibitionSubjectExists(graph, subject);
        checkProhibitionContainersExist(graph, containerConditions);
    }

    /**
     * Check the prohibition being updated.
     *
     * @param graph The GraphStore used to check the subject and containers exist.
     * @param name The name of the prohibition.
     * @param subject The subject of the prohibition.
     * @param accessRightSet The denied access rights.
     * @param intersection The boolean flag indicating an evaluation of the union or intersection of the container conditions.
     * @param containerConditions The prohibition container conditions.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default void checkUpdateInput(Graph graph, String name, ProhibitionSubject subject, AccessRightSet accessRightSet,
                                  boolean intersection , ContainerCondition ... containerConditions) throws PMException {
        if (!exists(name)) {
            throw new ProhibitionDoesNotExistException(name);
        }

        // check the prohibition parameters are valid
        checkAccessRightsValid(graph.getResourceAccessRights(), accessRightSet);
        checkProhibitionSubjectExists(graph, subject);
        checkProhibitionContainersExist(graph, containerConditions);
    }

    /**
     * Check if the prohibition exists. If it doesn't, return false to indicate to the caller that execution should not
     * proceed.
     *
     * @param name The name of the prohibition.
     * @return True if the execution should proceed, false otherwise.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default boolean checkDeleteInput(String name) throws PMException {
        if (!exists(name)) {
            return false;
        }

        return true;
    }

    /**
     * Check if the prohibition exists.
     * @param name The prohibition name.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default void checkGetInput(String name) throws PMException {
        if (!exists(name)) {
            throw new ProhibitionDoesNotExistException(name);
        }
    }

    default void checkProhibitionSubjectExists(Graph graph, ProhibitionSubject subject)
            throws PMException {
        if (subject.getType() != ProhibitionSubject.Type.PROCESS) {
            if (!graph.nodeExists(subject.getName())) {
                throw new ProhibitionSubjectDoesNotExistException(subject.getName());
            }
        }
    }

    default void checkProhibitionContainersExist(Graph graph, ContainerCondition ... containerConditions)
            throws PMException {
        for (ContainerCondition container : containerConditions) {
            if (!graph.nodeExists(container.getName())) {
                throw new ProhibitionContainerDoesNotExistException(container.getName());
            }
        }
    }
}
