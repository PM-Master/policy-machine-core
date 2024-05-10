package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.epp.EventEmitter;
import gov.nist.csd.pm.epp.EventProcessor;
import gov.nist.csd.pm.pap.modification.GraphModification;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.graph.*;
import gov.nist.csd.pm.pdp.adjudicator.AdjudicatorGraphModification;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.Association;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PDPGraphModifier implements GraphModification, EventEmitter {

    private UserContext userCtx;
    private AdjudicatorGraphModification adjudicator;
    private PAP pap;
    private EventProcessor listener;

    public PDPGraphModifier(UserContext userCtx, AdjudicatorGraphModification adjudicator, PAP pap, EventProcessor listener) {
        this.userCtx = userCtx;
        this.adjudicator = adjudicator;
        this.pap = pap;
        this.listener = listener;
    }

    @Override
    public void setResourceAccessRights(AccessRightSet accessRightSet) throws PMException {
        adjudicator.setResourceAccessRights(accessRightSet);

        pap.modify().graph().setResourceAccessRights(accessRightSet);
    }

    @Override
    public String createPolicyClass(String name, Map<String, String> properties) throws PMException {
        adjudicator.createPolicyClass(name, properties);

        pap.modify().graph().createPolicyClass(name, properties);

        emitEvent(new EventContext(userCtx, new CreatePolicyClassOp(name, new HashMap<>())));

        return name;
    }

    @Override
    public String createUserAttribute(String name, Map<String, String> properties, List<String> parents) throws PMException {
        adjudicator.createUserAttribute(name, properties, parents);

        pap.modify().graph().createUserAttribute(name, properties, parents);

        CreateUserAttributeOp op =
                new CreateUserAttributeOp(name, new HashMap<>(), parents);

        emitEvent(new EventContext(userCtx, op));

        return name;
    }

    @Override
    public String createObjectAttribute(String name, Map<String, String> properties, List<String> parents) throws PMException {
        adjudicator.createObjectAttribute(name, properties, parents);

        pap.modify().graph().createObjectAttribute(name, properties, parents);

        CreateObjectAttributeOp op =
                new CreateObjectAttributeOp(name, new HashMap<>(), parents);

        emitEvent(new EventContext(userCtx, op));

        return name;
    }

    @Override
    public String createObject(String name, Map<String, String> properties, List<String> parents) throws PMException {
        adjudicator.createObject(name, properties, parents);

        pap.modify().graph().createObject(name, properties, parents);

        CreateObjectOp op =
                new CreateObjectOp(name, new HashMap<>(), parents);

        emitEvent(new EventContext(userCtx, op));

        return name;
    }

    @Override
    public String createUser(String name, Map<String, String> properties, List<String> parents) throws PMException {
        adjudicator.createUser(name, properties, parents);

        pap.modify().graph().createUser(name, properties, parents);

        CreateUserOp op = new CreateUserOp(name, new HashMap<>(), parents);

        emitEvent(new EventContext(userCtx, op));

        return name;
    }

    @Override
    public void setNodeProperties(String name, Map<String, String> properties) throws PMException {
        adjudicator.setNodeProperties(name, properties);

        pap.modify().graph().setNodeProperties(name, properties);

        emitEvent(new EventContext(userCtx,
                new SetNodePropertiesOp(name, properties)));
    }

    @Override
    public void deleteNode(String name) throws PMException {
        adjudicator.deleteNode(name);

        // get parents of the deleted node before deleting to process event in the EPP
        List<String> parents = pap.query().graph().getParents(name);

        pap.modify().graph().deleteNode(name);

        emitDeleteNodeOp(new DeleteNodeOp(name), name, parents);
    }

    private void emitDeleteNodeOp(Operation event, String name, List<String> parents) throws PMException {
        // emit delete node event on the deleted node
        emitEvent(new EventContext(userCtx, event));

        // emit delete node on each parent
        for (String parent : parents) {
            emitEvent(new EventContext(userCtx, event));
        }
    }

    @Override
    public void assign(String child, String parent) throws PMException {
        adjudicator.assign(child, parent);

        pap.modify().graph().assign(child, parent);

        emitEvent(new EventContext(userCtx,
                new AssignOp(child, parent)));
        emitEvent(new EventContext(userCtx,
                new AssignToOp(child, parent)));
    }

    @Override
    public void deassign(String child, String parent) throws PMException {
        adjudicator.deassign(child, parent);

        pap.modify().graph().deassign(child, parent);

        emitEvent(new EventContext(userCtx,
                new DeassignOp(child, parent)));
        emitEvent(new EventContext(userCtx,
                new DeassignFromOp(child, parent)));
    }

    @Override
    public void associate(String ua, String target, AccessRightSet accessRights) throws PMException {
        adjudicator.associate(ua, target, accessRights);

        pap.modify().graph().associate(ua, target, accessRights);

        emitEvent(new EventContext(userCtx,
                new AssociateOp(ua, target, accessRights)));
        emitEvent(new EventContext(userCtx,
                new AssociateOp(ua, target, accessRights)));
    }

    @Override
    public void dissociate(String ua, String target) throws PMException {
        adjudicator.dissociate(ua, target);

        pap.modify().graph().dissociate(ua, target);

        emitEvent(new EventContext(userCtx,
                new DissociateOp(ua, target)));
        emitEvent(new EventContext(userCtx,
                new DissociateOp(ua, target)));
    }

    @Override
    public void addEventListener(EventProcessor processor) {

    }

    @Override
    public void removeEventListener(EventProcessor processor) {

    }

    @Override
    public void emitEvent(EventContext event) throws PMException {
        this.listener.processEvent(event);
    }
}
