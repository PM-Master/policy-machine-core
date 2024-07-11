package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.common.obligation.EventContext;
import gov.nist.csd.pm.epp.EventEmitter;
import gov.nist.csd.pm.pap.modification.GraphModification;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.op.graph.*;
import gov.nist.csd.pm.pap.op.operation.SetResourceOperationsOp;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.graph.node.NodeType;

import java.util.Collection;
import java.util.Map;

public class GraphModificationAdjudicator extends OperationExecutor implements GraphModification {

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
        EventContext event = new SetResourceOperationsOp()
                .withOperands(accessRightSet)
                .execute(pap, userCtx);

        eventEmitter.emitEvent(event);
    }

    @Override
    public String createPolicyClass(String name, Map<String, String> properties) throws PMException {
        CreatePolicyClassOp op = new CreatePolicyClassOp(name, properties);

        EventContext event = op.execute(pap, userCtx);

        eventEmitter.emitEvent(event);

        return name;
    }

    @Override
    public String createUserAttribute(String name, Map<String, String> properties, Collection<String> assignments) throws PMException {
        CreateUserAttributeOp op = new CreateUserAttributeOp(name, properties, assignments);

        EventContext event = op.execute(pap, userCtx);

        eventEmitter.emitEvent(event);

        return name;
    }

    @Override
    public String createObjectAttribute(String name, Map<String, String> properties, Collection<String> assignments) throws PMException {
        CreateObjectAttributeOp op = new CreateObjectAttributeOp(name, properties, assignments);

        EventContext event = op.execute(pap, userCtx);

        eventEmitter.emitEvent(event);

        return name;
    }

    @Override
    public String createObject(String name, Map<String, String> properties, Collection<String> assignments) throws PMException {
        CreateObjectOp op = new CreateObjectOp(name, properties, assignments);

        EventContext event = op.execute(pap, userCtx);

        eventEmitter.emitEvent(event);

        return name;
    }

    @Override
    public String createUser(String name, Map<String, String> properties, Collection<String> assignments) throws PMException {
        CreateUserOp op = new CreateUserOp(name, properties, assignments);

        EventContext event = op.execute(pap, userCtx);

        eventEmitter.emitEvent(event);

        return name;
    }

    @Override
    public void setNodeProperties(String name, Map<String, String> properties) throws PMException {
        SetNodePropertiesOp op = new SetNodePropertiesOp(name, properties);

        EventContext event = op.execute(pap, userCtx);

        eventEmitter.emitEvent(event);
    }

    @Override
    public void deleteNode(String name) throws PMException {
        NodeType nodeType = pap.query().graph().getNode(name).getType();
        Collection<String> descendants = pap.query().graph().getAdjacentDescendants(name);

        DeleteNodeOp op = new DeleteNodeOp(name, nodeType, descendants);
        executeOpAndEmitEvent(pap, userCtx, op, eventEmitter);
    }

    @Override
    public void assign(String ascendant, String descendant) throws PMException {
        AssignOp op = new AssignOp(ascendant, descendant);

        EventContext event = op.execute(pap, userCtx);

        eventEmitter.emitEvent(event);
    }

    @Override
    public void deassign(String ascendant, String descendant) throws PMException {
        DeassignOp op = new DeassignOp(ascendant, descendant);

        executeOpAndEmitEvent(pap, userCtx, op, eventEmitter);
    }

    @Override
    public void associate(String ua, String target, AccessRightSet accessRights) throws PMException {
        AssociateOp op = new AssociateOp(ua, target, accessRights);

        EventContext event = op.execute(pap, userCtx);

        eventEmitter.emitEvent(event);
    }

    @Override
    public void dissociate(String ua, String target) throws PMException {
        DissociateOp op = new DissociateOp(ua, target);

        executeOpAndEmitEvent(pap, userCtx, op, eventEmitter);

    }
}
