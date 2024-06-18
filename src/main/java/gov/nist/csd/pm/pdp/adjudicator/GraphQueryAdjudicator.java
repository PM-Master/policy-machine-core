package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.pap.admin.AdminPolicy;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.query.GraphQuery;
import gov.nist.csd.pm.pdp.exception.UnauthorizedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.GET_ASSOCIATIONS;

public class GraphQueryAdjudicator implements GraphQuery {

    private final UserContext userCtx;
    private final PAP pap;

    public GraphQueryAdjudicator(UserContext userCtx, PAP pap) {
        this.userCtx = userCtx;
        this.pap = pap;
    }

    @Override
    public AccessRightSet getResourceAccessRights() throws PMException {
        return pap.query().graph().getResourceAccessRights();
    }

    @Override
    public boolean nodeExists(String name) throws PMException {
        boolean exists = pap.query().graph().nodeExists(name);

        // check user has permissions on the node
        PrivilegeChecker.check(pap, userCtx, name);

        return exists;
    }

    @Override
    public Node getNode(String name) throws PMException {
        // get node
        Node node = pap.query().graph().getNode(name);

        // check user has permissions on the node
        PrivilegeChecker.check(pap, userCtx, name);

        return node;
    }

    @Override
    public Collection<String> search(NodeType type, Map<String, String> properties) throws PMException {
        Collection<String> search = pap.query().graph().search(type, properties);
        search.removeIf(node -> {
            try {
                PrivilegeChecker.check(pap, userCtx, node);
                return false;
            } catch (PMException e) {
                return true;
            }
        });

        return search;
    }

    @Override
    public Collection<String> getPolicyClasses() throws PMException {
        List<String> policyClasses = new ArrayList<>();
        for (String pc : pap.query().graph().getPolicyClasses()) {
            try {
                PrivilegeChecker.check(pap, userCtx, AdminPolicy.policyClassTargetName(pc));
            } catch (UnauthorizedException e) {
                continue;
            }

            policyClasses.add(pc);
        }

        return policyClasses;
    }

    @Override
    public Collection<String> getParents(String node) throws PMException {
        List<String> parents = new ArrayList<>();
        for (String parent : pap.query().graph().getParents(node)) {
            try {
                PrivilegeChecker.check(pap, userCtx, parent);
            } catch (UnauthorizedException e) {
                continue;
            }

            parents.add(parent);
        }

        return parents;
    }

    @Override
    public Collection<String> getChildren(String node) throws PMException {
        List<String> children = new ArrayList<>();
        for (String child : pap.query().graph().getChildren(node)) {
            try {
                PrivilegeChecker.check(pap, userCtx, child);
            } catch (UnauthorizedException e) {
                continue;
            }

            children.add(child);
        }

        return children;
    }

    @Override
    public Collection<Association> getAssociationsWithSource(String ua) throws PMException {
        return getAssociations(pap.query().graph().getAssociationsWithSource(ua));
    }

    @Override
    public Collection<Association> getAssociationsWithTarget(String target) throws PMException {
        return getAssociations(pap.query().graph().getAssociationsWithTarget(target));
    }

    @Override
    public Collection<String> getAttributeContainers(String node) throws PMException {
        PrivilegeChecker.check(pap, userCtx, node, AdminAccessRights.REVIEW_POLICY);

        return pap.query().graph().getAttributeContainers(node);
    }

    @Override
    public Collection<String> getPolicyClassContainers(String node) throws PMException {
        PrivilegeChecker.check(pap, userCtx, node, AdminAccessRights.REVIEW_POLICY);

        return pap.query().graph().getPolicyClassContainers(node);
    }

    @Override
    public boolean isContained(String subject, String container) throws PMException {
        PrivilegeChecker.check(pap, userCtx, subject, AdminAccessRights.REVIEW_POLICY);
        PrivilegeChecker.check(pap, userCtx, container, AdminAccessRights.REVIEW_POLICY);

        return pap.query().graph().isContained(subject, container);
    }

    private List<Association> getAssociations(Collection<Association> associations) {
        List<Association> ret = new ArrayList<>();
        for (Association association : associations) {
            try {
                PrivilegeChecker.check(pap, userCtx, association.getSource(), GET_ASSOCIATIONS);
                PrivilegeChecker.check(pap, userCtx, association.getTarget(), GET_ASSOCIATIONS);
            } catch (PMException e) {
                continue;
            }

            ret.add(association);
        }

        return ret;
    }
}
