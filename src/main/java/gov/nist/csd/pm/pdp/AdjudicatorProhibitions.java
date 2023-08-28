package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.pap.AdminPolicy;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.policy.Prohibitions;
import gov.nist.csd.pm.policy.exceptions.PMException;
import gov.nist.csd.pm.policy.exceptions.UnauthorizedException;
import gov.nist.csd.pm.policy.model.access.AccessRightSet;
import gov.nist.csd.pm.policy.model.access.UserContext;
import gov.nist.csd.pm.policy.model.prohibition.ContainerCondition;
import gov.nist.csd.pm.policy.model.prohibition.Prohibition;
import gov.nist.csd.pm.policy.model.prohibition.ProhibitionSubject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.policy.model.access.AdminAccessRights.*;
import static gov.nist.csd.pm.policy.model.access.AdminAccessRights.ADD_CONTAINER_COMPLEMENT_TO_PROHIBITION;

public class AdjudicatorProhibitions implements Prohibitions {
    private final UserContext userCtx;
    private final PAP pap;
    private final AccessRightChecker accessRightChecker;

    public AdjudicatorProhibitions(UserContext userCtx, PAP pap, AccessRightChecker accessRightChecker) {
        this.userCtx = userCtx;
        this.pap = pap;
        this.accessRightChecker = accessRightChecker;
    }

    @Override
    public void create(String name, ProhibitionSubject subject, AccessRightSet accessRightSet, boolean intersection, ContainerCondition... containerConditions) throws PMException {
        if (subject.getType() == ProhibitionSubject.Type.PROCESS) {
            accessRightChecker.check(userCtx, AdminPolicy.ADMIN_POLICY_TARGET, CREATE_PROCESS_PROHIBITION);
        } else {
            accessRightChecker.check(userCtx, subject.getName(), CREATE_PROHIBITION);
        }


        // check that the user can create a prohibition for each container in the condition
        for (ContainerCondition contCond : containerConditions) {
            accessRightChecker.check(userCtx, contCond.name(), ADD_CONTAINER_TO_PROHIBITION);

            // there is another access right needed if the condition is a complement
            if (contCond.complement()) {
                accessRightChecker.check(userCtx, AdminPolicy.ADMIN_POLICY_TARGET, ADD_CONTAINER_COMPLEMENT_TO_PROHIBITION);
            }
        }
    }

    @Override
    public void update(String name, ProhibitionSubject subject, AccessRightSet accessRightSet, boolean intersection, ContainerCondition... containerConditions) throws PMException {
        create(name, subject, accessRightSet, intersection, containerConditions);
    }

    @Override
    public void delete(String name) throws PMException {
        Prohibition prohibition = pap.prohibitions().get(name);

        // check that the user can create a prohibition for the subject
        if (prohibition.getSubject().getType() == ProhibitionSubject.Type.PROCESS) {
            accessRightChecker.check(userCtx, AdminPolicy.ADMIN_POLICY_TARGET, DELETE_PROCESS_PROHIBITION);
        } else {
            accessRightChecker.check(userCtx, prohibition.getSubject().getName(), DELETE_PROHIBITION);
        }

        // check that the user can create a prohibition for each container in the condition
        for (ContainerCondition contCond : prohibition.getContainers()) {
            accessRightChecker.check(userCtx, contCond.name(), REMOVE_CONTAINER_FROM_PROHIBITION);

            // there is another access right needed if the condition is a complement
            if (contCond.complement()) {
                accessRightChecker.check(userCtx, AdminPolicy.ADMIN_POLICY_TARGET, REMOVE_CONTAINER_COMPLEMENT_FROM_PROHIBITION);
            }
        }
    }

    @Override
    public Map<String, List<Prohibition>> getAll() throws PMException {
        Map<String, List<Prohibition>> prohibitions = pap.prohibitions().getAll();
        Map<String, List<Prohibition>> retProhibitions = new HashMap<>();
        for (String subject : prohibitions.keySet()) {
            List<Prohibition> subjectPros = filterProhibitions(prohibitions.get(subject));
            retProhibitions.put(subject, subjectPros);
        }

        return retProhibitions;
    }

    @Override
    public boolean exists(String name) throws PMException {
        boolean exists = pap.prohibitions().exists(name);
        if (!exists) {
            return false;
        }

        try {
            get(name);
        } catch (UnauthorizedException e) {
            return false;
        }


        return true;
    }

    @Override
    public List<Prohibition> getWithSubject(String subject) throws PMException {
        return filterProhibitions(pap.prohibitions().getWithSubject(subject));
    }

    @Override
    public Prohibition get(String name) throws PMException {
        Prohibition prohibition = pap.prohibitions().get(name);

        // check user has access to subject prohibitions
        if (prohibition.getSubject().getType() == ProhibitionSubject.Type.PROCESS) {
            accessRightChecker.check(userCtx, AdminPolicy.ADMIN_POLICY_TARGET, GET_PROCESS_PROHIBITIONS);
        } else {
            accessRightChecker.check(userCtx, prohibition.getSubject().getName(), GET_PROHIBITIONS);
        }

        // check user has access to each target prohibitions
        for (ContainerCondition containerCondition : prohibition.getContainers()) {
            accessRightChecker.check(userCtx, containerCondition.name(), GET_PROHIBITIONS);
        }

        return prohibition;
    }

    private List<Prohibition> filterProhibitions(List<Prohibition> prohibitions) {
        prohibitions.removeIf(prohibition -> {
            try {
                // check user has access to subject prohibitions
                if (prohibition.getSubject().getType() == ProhibitionSubject.Type.PROCESS) {
                    accessRightChecker.check(userCtx, AdminPolicy.ADMIN_POLICY_TARGET, GET_PROCESS_PROHIBITIONS);
                } else {
                    accessRightChecker.check(userCtx, prohibition.getSubject().getName(), GET_PROHIBITIONS);
                }

                // check user has access to each target prohibitions
                for (ContainerCondition containerCondition : prohibition.getContainers()) {
                    accessRightChecker.check(userCtx, containerCondition.name(), GET_PROHIBITIONS);
                }

                return false;
            } catch (PMException e) {
                return true;
            }
        });

        return prohibitions;
    }
}