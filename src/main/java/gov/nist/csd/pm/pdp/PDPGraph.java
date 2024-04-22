package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.epp.EventEmitter;
import gov.nist.csd.pm.epp.EventProcessor;
import gov.nist.csd.pm.pap.Graph;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pdp.adjudicator.AdjudicatorGraph;
import gov.nist.csd.pm.pap.op.PolicyEvent;
import gov.nist.csd.pm.pap.op.graph.*;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.nodes.Node;
import gov.nist.csd.pm.common.graph.nodes.NodeType;
import gov.nist.csd.pm.common.graph.relationships.Association;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PDPGraph implements Graph, EventEmitter {

    private UserContext userCtx;
    private AdjudicatorGraph adjudicator;
    private PAP pap;
    private EventProcessor listener;

    public PDPGraph(UserContext userCtx, AdjudicatorGraph adjudicator, PAP pap, EventProcessor listener) {
        this.userCtx = userCtx;
        this.adjudicator = adjudicator;
        this.pap = pap;
        this.listener = listener;
    }

    @Override
    public void setResourceAccessRights(AccessRightSet accessRightSet) throws PMException {
        adjudicator.setResourceAccessRights(accessRightSet);

        pap.graph().setResourceAccessRights(accessRightSet);
    }

    @Override
    public AccessRightSet getResourceAccessRights() throws PMException {
        return pap.graph().getResourceAccessRights();
    }

    @Override
    public String createPolicyClass(String name, Map<String, String> properties) throws PMException {
        adjudicator.createPolicyClass(name, properties);

        pap.graph().createPolicyClass(name, properties);

        emitEvent(new EventContext(userCtx, name, new CreatePolicyClassEvent(name, new HashMap<>())));

        return name;
    }

    @Override
    public String createUserAttribute(String name, Map<String, String> properties, List<String> parents) throws PMException {
        adjudicator.createUserAttribute(name, properties, parents);

        pap.graph().createUserAttribute(name, properties, parents);

        CreateUserAttributeEvent event =
                new CreateUserAttributeEvent(name, new HashMap<>(), parents);

        emitCreateNodeEvent(event, name, parents);

        return name;
    }

    @Override
    public String createObjectAttribute(String name, Map<String, String> properties, List<String> parents) throws PMException {
        adjudicator.createObjectAttribute(name, properties, parents);

        pap.graph().createObjectAttribute(name, properties, parents);

        CreateObjectAttributeEvent event =
                new CreateObjectAttributeEvent(name, new HashMap<>(), parents);

        emitCreateNodeEvent(event, name, parents);

        return name;
    }

    @Override
    public String createObject(String name, Map<String, String> properties, List<String> parents) throws PMException {
        adjudicator.createObject(name, properties, parents);

        pap.graph().createObject(name, properties, parents);

        CreateObjectEvent event =
                new CreateObjectEvent(name, new HashMap<>(), parents);

        emitCreateNodeEvent(event, name, parents);

        return name;
    }

    @Override
    public String createUser(String name, Map<String, String> properties, List<String> parents) throws PMException {
        adjudicator.createUser(name, properties, parents);

        pap.graph().createUser(name, properties, parents);

        CreateUserEvent event = new CreateUserEvent(name, new HashMap<>(), parents);

        emitCreateNodeEvent(event, name, parents);

        return name;
    }

    private void emitCreateNodeEvent(PolicyEvent event, String name, List<String> parents) throws PMException {
        // emit event for the new node
        emitEvent(new EventContext(userCtx, name, event));

        // do the same for any additional parents
        for (String p : parents) {
            emitEvent(new EventContext(userCtx, p, event));
        }
    }

    @Override
    public void setNodeProperties(String name, Map<String, String> properties) throws PMException {
        adjudicator.setNodeProperties(name, properties);

        pap.graph().setNodeProperties(name, properties);

        emitEvent(new EventContext(userCtx, name,
                new SetNodePropertiesEvent(name, properties)));
    }

    @Override
    public void deleteNode(String name) throws PMException {
        adjudicator.deleteNode(name);

        // get parents of the deleted node before deleting to process event in the EPP
        List<String> parents = getParents(name);

        pap.graph().deleteNode(name);

        emitDeleteNodeEvent(new DeleteNodeEvent(name), name, parents);
    }

    private void emitDeleteNodeEvent(PolicyEvent event, String name, List<String> parents) throws PMException {
        // emit delete node event on the deleted node
        emitEvent(new EventContext(userCtx, name, event));

        // emit delete node on each parent
        for (String parent : parents) {
            emitEvent(new EventContext(userCtx, parent, event));
        }
    }

    @Override
    public boolean nodeExists(String name) throws PMException {
        return adjudicator.nodeExists(name);
    }

    @Override
    public Node getNode(String name) throws PMException {
        return adjudicator.getNode(name);
    }

    @Override
    public List<String> search(NodeType type, Map<String, String> properties) throws PMException {
        return adjudicator.search(type, properties);
    }

    @Override
    public List<String> getPolicyClasses() throws PMException {
        return pap.graph().getPolicyClasses();
    }

    @Override
    public void assign(String child, String parent) throws PMException {
        adjudicator.assign(child, parent);

        pap.graph().assign(child, parent);

        emitEvent(new EventContext(userCtx, child,
                new AssignEvent(child, parent)));
        emitEvent(new EventContext(userCtx, parent,
                new AssignToEvent(child, parent)));
    }

    @Override
    public void deassign(String child, String parent) throws PMException {
        adjudicator.deassign(child, parent);

        pap.graph().deassign(child, parent);

        emitEvent(new EventContext(userCtx, child,
                new DeassignEvent(child, parent)));
        emitEvent(new EventContext(userCtx, parent,
                new DeassignFromEvent(child, parent)));
    }

    @Override
    public List<String> getChildren(String node) throws PMException {
        return adjudicator.getChildren(node);
    }

    @Override
    public void associate(String ua, String target, AccessRightSet accessRights) throws PMException {
        adjudicator.associate(ua, target, accessRights);

        pap.graph().associate(ua, target, accessRights);

        emitEvent(new EventContext(userCtx, ua,
                new AssociateEvent(ua, target, accessRights)));
        emitEvent(new EventContext(userCtx, target,
                new AssociateEvent(ua, target, accessRights)));
    }

    @Override
    public void dissociate(String ua, String target) throws PMException {
        adjudicator.dissociate(ua, target);

        pap.graph().dissociate(ua, target);

        emitEvent(new EventContext(userCtx, ua,
                new DissociateEvent(ua, target)));
        emitEvent(new EventContext(userCtx, target,
                new DissociateEvent(ua, target)));
    }

    @Override
    public List<String> getParents(String node) throws PMException {
        return adjudicator.getParents(node);
    }

    @Override
    public List<Association> getAssociationsWithSource(String ua) throws PMException {
        return adjudicator.getAssociationsWithSource(ua);
    }

    @Override
    public List<Association> getAssociationsWithTarget(String target) throws PMException {
        return adjudicator.getAssociationsWithTarget(target);
    }


    @Override
    public void addEventListener(EventProcessor listener) {

    }

    @Override
    public void removeEventListener(EventProcessor listener) {

    }

    @Override
    public void emitEvent(EventContext event) throws PMException {
        this.listener.processEvent(event);
    }
}
