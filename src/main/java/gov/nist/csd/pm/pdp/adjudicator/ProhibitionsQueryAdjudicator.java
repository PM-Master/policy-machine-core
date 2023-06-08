package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.query.ProhibitionsQuery;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.GET_PROCESS_PROHIBITIONS;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.GET_PROHIBITIONS;

public class ProhibitionsQueryAdjudicator implements ProhibitionsQuery {

    private final UserContext userCtx;
    private final PAP pap;

    public ProhibitionsQueryAdjudicator(UserContext userCtx, PAP pap) {
        this.userCtx = userCtx;
        this.pap = pap;
    }

    @Override
    public Map<String, Collection<Prohibition>> getAll() throws PMException {
        Map<String, Collection<Prohibition>> prohibitions = pap.query().prohibitions().getAll();
        Map<String, Collection<Prohibition>> retProhibitions = new HashMap<>();
        for (String subject : prohibitions.keySet()) {
            Collection<Prohibition> subjectPros = filterProhibitions(prohibitions.get(subject));
            retProhibitions.put(subject, subjectPros);
        }

        return retProhibitions;
    }

    @Override
    public boolean exists(String name) throws PMException {
        boolean exists = pap.query().prohibitions().exists(name);
        if (!exists) {
            return false;
        }

        // get will check privileges
        get(name);

        return true;
    }

    @Override
    public Collection<Prohibition> getWithSubject(String subject) throws PMException {
        return filterProhibitions(pap.query().prohibitions().getWithSubject(subject));
    }

    @Override
    public Prohibition get(String name) throws PMException {
        Prohibition prohibition = pap.query().prohibitions().get(name);

        // check user has access to subject
        if (prohibition.getSubject().getType() == ProhibitionSubject.Type.PROCESS) {
            PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(), GET_PROCESS_PROHIBITIONS);
        } else {
            PrivilegeChecker.check(pap, userCtx, prohibition.getSubject().getName(), GET_PROHIBITIONS);
        }

        // check user has access to each container condition
        for (ContainerCondition containerCondition : prohibition.getContainers()) {
            PrivilegeChecker.check(pap, userCtx, containerCondition.getName(), GET_PROHIBITIONS);
        }

        return prohibition;
    }

    @Override
    public Collection<Prohibition> getInheritedProhibitionsFor(String subject) throws PMException {
        PrivilegeChecker.check(pap, this.userCtx, subject, AdminAccessRights.REVIEW_POLICY);

        return pap.query().prohibitions().getInheritedProhibitionsFor(subject);
    }

    @Override
    public Collection<Prohibition> getProhibitionsWithContainer(String container) throws PMException {
        PrivilegeChecker.check(pap, this.userCtx, container, AdminAccessRights.REVIEW_POLICY);

        return pap.query().prohibitions().getProhibitionsWithContainer(container);
    }

    private Collection<Prohibition> filterProhibitions(Collection<Prohibition> prohibitions) {
        prohibitions.removeIf(prohibition -> {
            try {
                // check user has access to subject prohibitions
                if (prohibition.getSubject().getType() == ProhibitionSubject.Type.PROCESS) {
                    PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.PROHIBITIONS_TARGET.nodeName(),
                            GET_PROCESS_PROHIBITIONS);
                } else {
                    PrivilegeChecker.check(pap, userCtx, prohibition.getSubject().getName(), GET_PROHIBITIONS);
                }

                // check user has access to each target prohibitions
                for (ContainerCondition containerCondition : prohibition.getContainers()) {
                    PrivilegeChecker.check(pap, userCtx, containerCondition.getName(), GET_PROHIBITIONS);
                }

                return false;
            } catch (PMException e) {
                return true;
            }
        });

        return prohibitions;
    }
}
