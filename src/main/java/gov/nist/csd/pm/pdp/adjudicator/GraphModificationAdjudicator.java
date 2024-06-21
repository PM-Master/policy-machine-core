package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.common.obligation.EventContext;
import gov.nist.csd.pm.epp.EventEmitter;
import gov.nist.csd.pm.pap.admin.AdminPolicy;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.modification.GraphModification;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.op.graph.*;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.graph.node.NodeType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;
import static gov.nist.csd.pm.common.graph.node.NodeType.PC;

public class GraphModificationAdjudicator implements GraphModification {

    private final UserContext userCtx;
    private final PAP pap;
    private final EventEmitter eventEmitter;

    public GraphModificationAdjudicator(UserContext userCtx, PAP pap, EventEmitter eventEmitter) {
        this.userCtx = userCtx;
        this.pap = pap;
        this.eventEmitter = eventEmitter;
    }

    @Override
    public void setResourceAccessRights(AccessRightSet accessRightSet) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(), SET_RESOURCE_ACCESS_RIGHTS);

        pap.modify().graph().setResourceAccessRights(accessRightSet);

        eventEmitter.emitEvent(new EventContext(userCtx, new SetResourceAccessRightsOp(accessRightSet)));
    }

    @Override
    public String createPolicyClass(String name, Map<String, String> properties) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName(), CREATE_POLICY_CLASS);

        pap.modify().graph().createPolicyClass(name, properties);

        eventEmitter.emitEvent(new EventContext(userCtx, new CreatePolicyClassOp(name, new HashMap<>())));

        return name;
    }

    @Override
    public String createUserAttribute(String name, Map<String, String> properties, Collection<String> assignments) throws PMException {
        if (!assignments.isEmpty()) {
            checkAssignments(CREATE_USER_ATTRIBUTE, assignments);
        } else {
            checkAssignments(CREATE_USER_ATTRIBUTE, List.of(AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName()));
        }

        pap.modify().graph().createUserAttribute(name, properties, assignments);

        eventEmitter.emitEvent(new EventContext(userCtx, new CreateUserAttributeOp(name, new HashMap<>(), assignments)));

        return name;
    }

    @Override
    public String createObjectAttribute(String name, Map<String, String> properties, Collection<String> assignments) throws PMException {
        checkAssignments(CREATE_OBJECT_ATTRIBUTE, assignments);

        pap.modify().graph().createObjectAttribute(name, properties, assignments);

        eventEmitter.emitEvent(new EventContext(userCtx, new CreateObjectAttributeOp(name, new HashMap<>(), assignments)));

        return name;
    }

    @Override
    public String createObject(String name, Map<String, String> properties, Collection<String> assignments) throws PMException {
        checkAssignments(CREATE_OBJECT, assignments);

        pap.modify().graph().createObject(name, properties, assignments);

        eventEmitter.emitEvent(new EventContext(userCtx, new CreateObjectOp(name, new HashMap<>(), assignments)));

        return name;
    }

    @Override
    public String createUser(String name, Map<String, String> properties, Collection<String> assignments) throws PMException {
        checkAssignments(CREATE_USER, assignments);

        pap.modify().graph().createUser(name, properties, assignments);

        eventEmitter.emitEvent(new EventContext(userCtx, new CreateUserOp(name, new HashMap<>(), assignments)));

        return name;
    }

    @Override
    public void setNodeProperties(String name, Map<String, String> properties) throws PMException {
        PrivilegeChecker.check(pap, userCtx, name, SET_NODE_PROPERTIES);

        pap.modify().graph().setNodeProperties(name, properties);

        eventEmitter.emitEvent(new EventContext(userCtx, new SetNodePropertiesOp(name, properties)));
    }

    @Override
    public void deleteNode(String name) throws PMException {
        NodeType nodeType = pap.query().graph().getNode(name).getType();

        if (nodeType == PC) {
            PrivilegeChecker.check(pap, userCtx, AdminPolicy.policyClassTargetName(name), DELETE_POLICY_CLASS);
            return;
        }

        String op = switch (nodeType) {
            case OA -> DELETE_OBJECT_ATTRIBUTE;
            case UA -> DELETE_USER_ATTRIBUTE;
            case O -> DELETE_OBJECT;
            case U -> DELETE_USER;
            default -> DELETE_POLICY_CLASS;
        };

        // check the user can delete the node
        PrivilegeChecker.check(pap, userCtx, name, op);

        // check that the user can delete the node from the node's descendants
        Collection<String> descendants = pap.query().graph().getAdjacentDescendants(name);

        for(String descendant : descendants) {
            PrivilegeChecker.check(pap, userCtx, descendant, op);
        }

        pap.modify().graph().deleteNode(name);

        eventEmitter.emitEvent(new EventContext(userCtx, new DeleteNodeOp(name, descendants)));
    }

    @Override
    public void assign(String ascendant, String descendant) throws PMException {
        pap.query().graph().getNode(ascendant);
        pap.query().graph().getNode(descendant);

        //check the user can assign the ascendant
        PrivilegeChecker.check(pap, userCtx, ascendant, ASSIGN);

        // check that the user can assign to the descendant node
        PrivilegeChecker.check(pap, userCtx, descendant, ASSIGN_TO);

        pap.modify().graph().assign(ascendant, descendant);

        eventEmitter.emitEvent(new EventContext(userCtx,
                new AssignOp(ascendant, descendant)));
    }

    @Override
    public void deassign(String ascendant, String descendant) throws PMException {
        pap.query().graph().getNode(ascendant);
        pap.query().graph().getNode(descendant);

        //check the user can deassign the ascendant
        PrivilegeChecker.check(pap, userCtx, ascendant, DEASSIGN);

        // check that the user can deassign from the descendant node
        PrivilegeChecker.check(pap, userCtx, descendant, DEASSIGN_FROM);

        pap.modify().graph().deassign(ascendant, descendant);

        eventEmitter.emitEvent(new EventContext(userCtx, new DeassignOp(ascendant, descendant)));
    }

    @Override
    public void associate(String ua, String target, AccessRightSet accessRights) throws PMException {
        PrivilegeChecker.check(pap, userCtx, ua, ASSOCIATE);
        PrivilegeChecker.check(pap, userCtx, target, ASSOCIATE_TO);

        pap.modify().graph().associate(ua, target, accessRights);

        eventEmitter.emitEvent(new EventContext(userCtx, new AssociateOp(ua, target, accessRights)));
    }

    @Override
    public void dissociate(String ua, String target) throws PMException {
        PrivilegeChecker.check(pap, userCtx, ua, DISSOCIATE);
        PrivilegeChecker.check(pap, userCtx, target, DISSOCIATE_FROM);

        pap.modify().graph().dissociate(ua, target);

        eventEmitter.emitEvent(new EventContext(userCtx, new DissociateOp(ua, target)));
    }

    private void checkAssignments(String accessRight, Collection<String> descendants) throws PMException {
        for (String descendant : descendants) {
            PrivilegeChecker.check(pap, userCtx, descendant, accessRight);
        }
    }
}
