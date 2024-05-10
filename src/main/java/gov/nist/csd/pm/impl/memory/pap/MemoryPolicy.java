package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.exception.PMRuntimeException;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.common.tx.Transactional;
import gov.nist.csd.pm.impl.memory.pap.unmodifiable.UnmodifiableAccessRightSet;
import gov.nist.csd.pm.impl.memory.pap.unmodifiable.UnmodifiableObligation;
import gov.nist.csd.pm.impl.memory.pap.unmodifiable.UnmodifiableProhibition;
import gov.nist.csd.pm.pap.exception.ObligationDoesNotExistException;
import gov.nist.csd.pm.pap.exception.ProhibitionDoesNotExistException;
import gov.nist.csd.pm.pap.modification.*;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.graph.*;
import gov.nist.csd.pm.pap.op.obligation.CreateObligationOp;
import gov.nist.csd.pm.pap.op.prohibition.CreateProhibitionOp;
import gov.nist.csd.pm.pap.op.userdefinedpml.CreateConstantOp;
import gov.nist.csd.pm.pap.op.userdefinedpml.CreateFunctionOp;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.query.PolicyQuery;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.*;

import static gov.nist.csd.pm.common.graph.node.NodeType.*;

public class MemoryPolicy extends PolicyModifier implements Transactional {

    protected Map<String, Vertex> graph;
    protected AccessRightSet resourceAccessRights;
    protected List<String> pcs;
    protected List<String> oas;
    protected List<String> uas;
    protected List<String> os;
    protected List<String> us;

    protected Map<String, List<Prohibition>> prohibitions;

    protected List<Obligation> obligations;

    protected Map<String, FunctionDefinitionStatement> functions;
    protected Map<String, Value> constants;

    private TxGraphModifier txGraph;
    private TxProhibitionsModifier txPros;
    private TxObligationsModifier txObls;
    private TxPMLModifier txPml;
    private TxOpTracker txOpTracker;
    private MemoryTx tx;
    private MemoryPolicyQuerier querier;

    public MemoryPolicy() {
        reset();

        this.tx = new MemoryTx();
        this.txOpTracker = new TxOpTracker();
        this.txGraph = new TxGraphModifier(txOpTracker);
        this.txPros = new TxProhibitionsModifier(txOpTracker);
        this.txObls = new TxObligationsModifier(txOpTracker);
        this.txPml = new TxPMLModifier(txOpTracker);
    }

    @Override
    public GraphModifier graph() {
        return txGraph;
    }

    @Override
    public ProhibitionsModifier prohibitions() {
        return txPros;
    }

    @Override
    public ObligationsModifier obligations() {
        return txObls;
    }

    @Override
    public PMLModifier pml() {
        return txPml;
    }

    public void reset() {
        this.graph = new HashMap<>();
        this.resourceAccessRights = new AccessRightSet();
        this.pcs = new ArrayList<>();
        this.oas = new ArrayList<>();
        this.uas = new ArrayList<>();
        this.os = new ArrayList<>();
        this.us = new ArrayList<>();
        this.prohibitions = new HashMap<>();
        this.obligations = new ArrayList<>();
        this.functions = new HashMap<>();
        this.constants = new HashMap<>();
    }

    @Override
    public void beginTx() {
        tx.beginTx();
    }

    @Override
    public void commit() {
        tx.commit();

        if (tx.getCounter() == 0) {
            txOpTracker.clearOps();
        }
    }

    @Override
    public void rollback() throws PMException {
        tx.rollback();

        List<Operation> events = txOpTracker.getOperations();
        for (Operation event : events) {
            try {
                TxCmd txCmd = TxCmd.eventToCmd(event);
                txCmd.rollback(this);
            } catch (PMException e) {
                throw new PMException("error during tx rollback", e);
            }
        }

        txOpTracker.clearOps();
    }

    @Override
    public PolicyQuery query() {
        if (querier == null) {
            querier = new MemoryPolicyQuerier(this);
        }

        return querier;
    }

    class TxGraphModifier extends GraphModifier {

        private TxOpTracker txOpTracker;

        public TxGraphModifier(TxOpTracker txOpTracker) {
            this.txOpTracker = txOpTracker;
        }

        @Override
        protected void setResourceAccessRightsInternal(AccessRightSet accessRightSet) throws PMException {
            resourceAccessRights = new UnmodifiableAccessRightSet(accessRightSet);
        }

        @Override
        protected void createNodeInternal(String name, NodeType type, Map<String, String> properties)
                throws PMException {
            Map<String, String> props = new HashMap<>();
            if (properties != null) {
                props.putAll(properties);
            }

            graph.put(name, buildVertex(name, type, properties));
            if (type == PC) {
                pcs.add(name);
            } else if (type == OA) {
                oas.add(name);
            } else if (type == UA) {
                uas.add(name);
            } else if (type == O) {
                os.add(name);
            } else if (type == U) {
                us.add(name);
            }
        }

        private Vertex buildVertex(String name, NodeType type, Map<String, String> properties) {
            switch (type) {
                case PC -> {
                    return new VertexPolicyClass(name, properties);
                }
                case OA -> {
                    return new VertexAttribute(name, OA, properties);
                }
                case UA -> {
                    return new VertexAttribute(name, UA, properties);
                }
                case O -> {
                    return new VertexLeaf(name, O, properties);
                }
                default -> {
                    return new VertexLeaf(name, U, properties);
                }
            }
        }

        @Override
        protected void deleteNodeInternal(String name) throws PMException {
            Vertex vertex = graph.get(name);

            List<String> parents = vertex.getParents();
            List<Association> incomingAssociations = vertex.getIncomingAssociations();
            List<Association> outgoingAssociations = vertex.getOutgoingAssociations();

            for (String parent : parents) {
                graph.get(parent).deleteAssignment(name, parent);
            }

            for (Association association : incomingAssociations) {
                Vertex v = graph.get(association.getSource());
                if(v == null) {
                    continue;
                }

                v.deleteAssociation(association.getSource(), association.getTarget());
            }

            for (Association association : outgoingAssociations) {
                Vertex v = graph.get(association.getTarget());
                if(v == null) {
                    continue;
                }

                v.deleteAssociation(association.getSource(), association.getTarget());
            }

            graph.remove(name);

            if (vertex.getNode().getType() == PC) {
                pcs.remove(name);
            } else if (vertex.getNode().getType() == OA) {
                oas.remove(name);
            } else if (vertex.getNode().getType() == UA) {
                uas.remove(name);
            } else if (vertex.getNode().getType() == O) {
                os.remove(name);
            } else if (vertex.getNode().getType() == U) {
                us.remove(name);
            }
        }

        @Override
        protected void setNodePropertiesInternal(String name, Map<String, String> properties) throws PMException {
            graph.get(name).setProperties(properties);
        }

        @Override
        protected void createAssignmentInternal(String start, String end) throws PMException {
            // TODO slows down policy building -- use fastutil sets
            if (graph.get(start).getParents().contains(end)) {
                return;
            }

            graph.get(start).addAssignment(start, end);
            graph.get(end).addAssignment(start, end);
        }

        @Override
        protected void deleteAssignmentInternal(String start, String end) throws PMException {
            graph.get(start).deleteAssignment(start, end);
            graph.get(end).deleteAssignment(start, end);
        }

        @Override
        protected void createAssociationInternal(String ua, String target, AccessRightSet arset) throws PMException {
            deleteAssociationInternal(ua, target);
            graph.get(ua).addAssociation(ua, target, arset);
            graph.get(target).addAssociation(ua, target, arset);
        }

        @Override
        protected void deleteAssociationInternal(String ua, String target) throws PMException {
            graph.get(ua).deleteAssociation(ua, target);
            graph.get(target).deleteAssociation(ua, target);
        }

        @Override
        public void setResourceAccessRights(AccessRightSet accessRightSet) throws PMException {
            AccessRightSet old = new AccessRightSet(resourceAccessRights);

            super.setResourceAccessRights(accessRightSet);

            txOpTracker.trackOp(tx, new TxOps.MemorySetResourceAccessRightsOp(
                    old,
                    accessRightSet)
            );
        }

        @Override
        public String createPolicyClass(String name, Map<String, String> properties) throws PMException {
            String ret = super.createPolicyClass(name, properties);
            txOpTracker.trackOp(tx, new CreatePolicyClassOp(name, properties));
            return ret;
        }

        @Override
        public String createUserAttribute(String name, Map<String, String> properties, List<String> parents)
                throws PMException {
            String ret = super.createUserAttribute(name, properties, parents);
            txOpTracker.trackOp(tx, new CreateUserAttributeOp(name, properties, parents));
            return ret;
        }

        @Override
        public String createObjectAttribute(String name, Map<String, String> properties, List<String> parents)
                throws PMException {
            String ret = super.createObjectAttribute(name, properties, parents);
            txOpTracker.trackOp(tx, new CreateObjectAttributeOp(name, properties, parents));
            return ret;
        }

        @Override
        public String createObject(String name, Map<String, String> properties, List<String> parents)
                throws PMException {
            String ret = super.createObject(name, properties, parents);
            txOpTracker.trackOp(tx, new CreateObjectOp(name, properties, parents));
            return ret;
        }

        @Override
        public String createUser(String name, Map<String, String> properties, List<String> parents) throws PMException {
            String ret = super.createUser(name, properties, parents);
            txOpTracker.trackOp(tx, new CreateUserOp(name, properties, parents));
            return ret;
        }

        @Override
        public void setNodeProperties(String name, Map<String, String> properties) throws PMException {
            Vertex vertex = graph.get(name);
            super.setNodeProperties(name, properties);
            Map<String, String> oldProperties = vertex.getNode().getProperties();
            txOpTracker.trackOp(tx,
                    new TxOps.MemorySetNodePropertiesOp(name, oldProperties, properties)
            );
        }

        @Override
        public void deleteNode(String name) throws PMException {
            Vertex vertex = graph.get(name);

            super.deleteNode(name);

            if (vertex != null) {
                txOpTracker.trackOp(tx, new TxOps.MemoryDeleteNodeOp(
                        name,
                        vertex.getNode(),
                        vertex.getParents()
                ));
            }
        }

        @Override
        public void assign(String child, String parent) throws PMException {
            super.assign(child, parent);
            txOpTracker.trackOp(tx, new AssignOp(child, parent));
        }

        @Override
        public void deassign(String child, String parent) throws PMException {
            super.deassign(child, parent);
            txOpTracker.trackOp(tx, new DeassignOp(child, parent));
        }

        @Override
        public void associate(String ua, String target, AccessRightSet accessRights) throws PMException {
            super.associate(ua, target, accessRights);
            txOpTracker.trackOp(tx, new AssociateOp(ua, target, accessRights));
        }

        @Override
        public void dissociate(String ua, String target) throws PMException {
            Vertex vertex = graph.get(ua);

            super.dissociate(ua, target);

            AccessRightSet accessRightSet = new AccessRightSet();
            for (Association association : vertex.getOutgoingAssociations()) {
                if (association.getTarget().equals(target)) {
                    accessRightSet = association.getAccessRightSet();
                }
            }

            txOpTracker.trackOp(tx, new TxOps.MemoryDissociateOp(ua, target, accessRightSet));
        }

        @Override
        public PolicyQuery query() {
            return MemoryPolicy.this.query();
        }

        @Override
        public void beginTx() throws PMException {
            MemoryPolicy.this.beginTx();
        }

        @Override
        public void commit() throws PMException {
            MemoryPolicy.this.commit();
        }

        @Override
        public void rollback() throws PMException {
            MemoryPolicy.this.rollback();
        }
    }
    class TxProhibitionsModifier extends ProhibitionsModifier {

        private TxOpTracker txOpTracker;

        public TxProhibitionsModifier(TxOpTracker txOpTracker) {
            this.txOpTracker = txOpTracker;
        }

        @Override
        protected void createInternal(String name,
                                      ProhibitionSubject subject,
                                      AccessRightSet accessRightSet,
                                      boolean intersection,
                                      ContainerCondition... containerConditions) {
            List<Prohibition> existingPros = new ArrayList<>(prohibitions.getOrDefault(
                    subject.getName(),
                    new ArrayList<>()
            ));
            existingPros.add(new UnmodifiableProhibition(
                    name,
                    subject,
                    accessRightSet,
                    intersection,
                    Arrays.asList(containerConditions)
            ));

            HashMap<String, List<Prohibition>> m = new HashMap<>(prohibitions);
            m.put(subject.getName(), Collections.unmodifiableList(existingPros));

            prohibitions = Collections.unmodifiableMap(m);
        }

        @Override
        protected void updateInternal(String name,
                                      ProhibitionSubject subject,
                                      AccessRightSet accessRightSet,
                                      boolean intersection,
                                      ContainerCondition... containerConditions) throws PMException {
            runTx(() -> {
                deleteInternal(name);
                create(name, subject, accessRightSet, intersection, containerConditions);
            });
        }

        @Override
        protected void deleteInternal(String name) {
            for (String subject : prohibitions.keySet()) {
                List<Prohibition> ps = new ArrayList<>(prohibitions.get(subject));
                if (ps.removeIf(p -> p.getName().equals(name))) {
                    HashMap<String, List<Prohibition>> m = new HashMap<>(prohibitions);
                    m.put(subject, Collections.unmodifiableList(ps));
                    prohibitions = Collections.unmodifiableMap(m);
                }
            }
        }

        @Override
        public void create(String name, ProhibitionSubject subject, AccessRightSet accessRightSet, boolean intersection, ContainerCondition... containerConditions)
                throws PMException {
            super.create(name, subject, accessRightSet, intersection, containerConditions);
            txOpTracker.trackOp(tx, new CreateProhibitionOp(name, subject, accessRightSet, intersection, List.of(containerConditions)));
        }

        @Override
        public void update(String name, ProhibitionSubject subject, AccessRightSet accessRightSet, boolean intersection, ContainerCondition... containerConditions)
                throws PMException {
            Prohibition old = getProhibition(name);
            super.update(name, subject, accessRightSet, intersection, containerConditions);
            txOpTracker.trackOp(tx, new TxOps.MemoryUpdateProhibitionOp(
                    new Prohibition(name, subject, accessRightSet, intersection, List.of(containerConditions)), old
            ));
        }

        private Prohibition getProhibition(String name) {
            for (String subject : prohibitions.keySet()) {
                List<Prohibition> subjectPros = prohibitions.get(subject);
                for (Prohibition p : subjectPros) {
                    if (p.getName().equals(name)) {
                        return p;
                    }
                }
            }

            return null;
        }

        @Override
        public void delete(String name) throws PMException {
            Prohibition old = getProhibition(name);
            super.delete(name);

            if(old != null) {
                txOpTracker.trackOp(tx, new TxOps.MemoryDeleteProhibitionOp(old));
            }
        }

        @Override
        public PolicyQuery query() {
            return MemoryPolicy.this.query();
        }

        @Override
        public void beginTx() throws PMException {
            MemoryPolicy.this.beginTx();
        }

        @Override
        public void commit() throws PMException {
            MemoryPolicy.this.commit();
        }

        @Override
        public void rollback() throws PMException {
            MemoryPolicy.this.rollback();
        }
    }
    class TxObligationsModifier extends ObligationsModifier {

        private TxOpTracker txOpTracker;

        public TxObligationsModifier(TxOpTracker txOpTracker) {
            this.txOpTracker = txOpTracker;
        }

        @Override
        protected void createInternal(UserContext author, String name, Rule... rules) {
            createWithIndex(obligations.size() - 1, author, name, rules);
        }

        private void createWithIndex(int index, UserContext author, String name, Rule... rules) {
            ArrayList<Obligation> copy = new ArrayList<>(obligations);
            copy.add(Math.max(index, 0), new UnmodifiableObligation(author, name, Arrays.asList(rules)));
            obligations = Collections.unmodifiableList(copy);
        }

        @Override
        protected void updateInternal(UserContext author, String name, Rule... rules) {
            for (int i = 0; i < obligations.size(); i++) {
                if (obligations.get(i).getName().equals(name)) {
                    deleteInternal(name);
                    createWithIndex(i, author, name, rules);

                    return;
                }
            }
        }

        @Override
        protected void deleteInternal(String name) {
            ArrayList<Obligation> copy = new ArrayList<>(obligations);
            copy.removeIf(o -> o.getName().equals(name));
            obligations = Collections.unmodifiableList(copy);
        }

        @Override
        public void create(UserContext author, String name, Rule... rules) throws PMException {
            super.create(author, name, rules);
            txOpTracker.trackOp(tx, new CreateObligationOp(author, name, List.of(rules)));
        }

        @Override
        public void update(UserContext author, String name, Rule... rules) throws PMException {
            Obligation old = getObligation(name);
            super.update(author, name, rules);
            txOpTracker.trackOp(tx, new TxOps.MemoryUpdateObligationOp(
                    new Obligation(author, name, List.of(rules)), old
            ));
        }

        private Obligation getObligation(String name) {
            for(Obligation obligation : obligations) {
                if(obligation.getName().equals(name)) {
                    return obligation;
                }
            }

            for (Obligation obligation : obligations) {
                if (obligation.getName().equals(name)) {
                    return obligation;
                }
            }

            return null;
        }

        @Override
        public void delete(String name) throws PMException {
            Obligation old = getObligation(name);
            super.delete(name);
            txOpTracker.trackOp(tx, new TxOps.MemoryDeleteObligationOp(old));
        }

        @Override
        public PolicyQuery query() {
            return MemoryPolicy.this.query();
        }

        @Override
        public void beginTx() throws PMException {
            MemoryPolicy.this.beginTx();
        }

        @Override
        public void commit() throws PMException {
            MemoryPolicy.this.commit();
        }

        @Override
        public void rollback() throws PMException {
            MemoryPolicy.this.rollback();
        }
    }
    class TxPMLModifier extends PMLModifier {

        private TxOpTracker txOpTracker;

        public TxPMLModifier(TxOpTracker txOpTracker) {
            this.txOpTracker = txOpTracker;
        }

        @Override
        protected void createFunctionInternal(FunctionDefinitionStatement func) throws PMException {
            functions.put(func.getSignature().getFunctionName(), new FunctionDefinitionStatement(func));
        }

        @Override
        protected void deleteFunctionInternal(String name) throws PMException {
            functions.remove(name);
        }

        @Override
        protected void createConstantInternal(String name, Value value) throws PMException {
            constants.put(name, value);
        }

        @Override
        protected void deleteConstantInternal(String name) throws PMException {
            constants.remove(name);
        }

        @Override
        public void createFunction(FunctionDefinitionStatement functionDefinitionStatement) throws PMException {
            super.createFunction(functionDefinitionStatement);
            txOpTracker.trackOp(tx, new CreateFunctionOp(functionDefinitionStatement));
        }

        @Override
        public void deleteFunction(String functionName) throws PMException {
            FunctionDefinitionStatement old = functions.get(functionName);
            super.deleteFunction(functionName);

            if (old != null) {
                txOpTracker.trackOp(tx, new TxOps.MemoryDeleteFunctionOp(old));
            }
        }

        @Override
        public void createConstant(String constantName, Value constantValue) throws PMException {
            super.createConstant(constantName, constantValue);
            txOpTracker.trackOp(tx, new CreateConstantOp(constantName, constantValue));
        }

        @Override
        public void deleteConstant(String constName) throws PMException {
            Value old = constants.get(constName);
            super.deleteConstant(constName);
            txOpTracker.trackOp(tx, new TxOps.MemoryDeleteConstantOp(constName, old));
        }

        @Override
        public PolicyQuery query() {
            return MemoryPolicy.this.query();
        }

        @Override
        public void beginTx() throws PMException {
            MemoryPolicy.this.beginTx();
        }

        @Override
        public void commit() throws PMException {
            MemoryPolicy.this.commit();
        }

        @Override
        public void rollback() throws PMException {
            MemoryPolicy.this.rollback();
        }
    }
}
