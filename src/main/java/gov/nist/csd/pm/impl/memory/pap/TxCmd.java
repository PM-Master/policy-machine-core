package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.op.PolicyEvent;
import gov.nist.csd.pm.pap.op.graph.*;
import gov.nist.csd.pm.pap.op.obligations.CreateObligationEvent;
import gov.nist.csd.pm.pap.op.prohibitions.CreateProhibitionEvent;
import gov.nist.csd.pm.pap.op.userdefinedpml.CreateConstantEvent;
import gov.nist.csd.pm.pap.op.userdefinedpml.CreateFunctionEvent;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.AccessRightSet;
import gov.nist.csd.pm.common.graph.nodes.Node;
import gov.nist.csd.pm.common.graph.nodes.NodeType;
import gov.nist.csd.pm.common.graph.relationships.Association;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;
import java.util.Map;

abstract class TxCmd<T extends MemoryStore<?>> {

    private Type type;

    public TxCmd(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public abstract void rollback(T store) throws PMException;

    enum Type {
        GRAPH,
        PROHIBITIONS,
        OBLIGATIONS,
        USER_DEFINED_PML
    }


    static TxCmd<?> eventToCmd(PolicyEvent event) throws UnsupportedPolicyEvent {
        if (event instanceof CreateConstantEvent e) {
            return new TxCmd.AddConstantTxCmd(
                    e.getName(),
                    e.getValue()
            );

        } else if (event instanceof CreateFunctionEvent e) {
            return new TxCmd.AddFunctionTxCmd(
                    e.getFunctionDefinitionStatement()
            );

        } else if (event instanceof AssignEvent e) {
            return new TxCmd.AssignTxCmd(
                    e.getChild(),
                    e.getParent()
            );

        } else if (event instanceof AssignToEvent e) {
            return new TxCmd.AssignTxCmd(
                    e.getChild(),
                    e.getParent()
            );

        } else if (event instanceof AssociateEvent e) {
            return new TxCmd.AssociateTxCmd(
                    new Association(e.getUa(), e.getTarget(), e.getAccessRightSet())
            );

        } else if (event instanceof CreateObjectAttributeEvent e) {
            return new TxCmd.CreateObjectAttributeTxCmd(
                    e.getName(),
                    e.getProperties(),
                    e.getParents()
            );

        } else if (event instanceof CreateObjectEvent e) {
            return new TxCmd.CreateObjectTxCmd(
                    e.getName(),
                    e.getProperties(),
                    e.getParents()
            );

        } else if (event instanceof CreateObligationEvent e) {
            return new TxCmd.CreateObligationTxCmd(
                    new Obligation(e.getAuthor(), e.getName(), e.getRules())
            );

        } else if (event instanceof CreatePolicyClassEvent e) {
            return new TxCmd.CreatePolicyClassTxCmd(
                    e.getName(),
                    e.getProperties()
            );

        } else if (event instanceof CreateProhibitionEvent e) {
            return new TxCmd.CreateProhibitionTxCmd(
                    new Prohibition(e.getName(), e.getSubject(), e.getAccessRightSet(), e.isIntersection(), e.getContainers())
            );

        } else if (event instanceof CreateUserAttributeEvent e) {
            return new TxCmd.CreateUserAttributeTxCmd(
                    e.getName(),
                    e.getProperties(),
                    e.getParents()
            );

        } else if (event instanceof CreateUserEvent e) {
            return new TxCmd.CreateUserTxCmd(
                    e.getName(),
                    e.getProperties(),
                    e.getParents()
            );

        } else if (event instanceof DeassignEvent e) {
            return new TxCmd.DeassignTxCmd(
                    e.getChild(),
                    e.getParent()
            );

        } else if (event instanceof TxEvents.MemoryDeleteNodeEvent e) {
            return new TxCmd.DeleteNodeTxCmd(
                    e.getName(),
                    e.getNode(),
                    e.getParents()
            );

        } else if (event instanceof TxEvents.MemoryDeleteObligationEvent e) {
            return new TxCmd.DeleteObligationTxCmd(
                    e.getObligationToDelete()
            );

        } else if (event instanceof TxEvents.MemoryDeleteProhibitionEvent e) {
            return new TxCmd.DeleteProhibitionTxCmd(
                    e.getProhibitionToDelete()
            );

        } else if (event instanceof TxEvents.MemoryDissociateEvent e) {
            return new TxCmd.DissociateTxCmd(
                    new Association(e.getUa(), e.getTarget(), e.getAccessRightSet())
            );

        } else if (event instanceof TxEvents.MemoryDeleteConstantEvent e) {
            return new TxCmd.RemoveConstantTxCmd(
                    e.getName(),
                    e.getValue()
            );

        } else if (event instanceof TxEvents.MemoryDeleteFunctionEvent e) {
            return new TxCmd.RemoveFunctionTxCmd(e.getFunctionDefinitionStatement());

        } else if (event instanceof TxEvents.MemorySetNodePropertiesEvent e) {
            return new TxCmd.SetNodePropertiesTxCmd(
                    e.getName(),
                    e.getOldProps(),
                    e.getProperties()
            );

        } else if (event instanceof TxEvents.MemoryUpdateObligationEvent e) {
            return new TxCmd.UpdateObligationTxCmd(
                    new Obligation(e.getAuthor(), e.getName(), e.getRules()), e.getOldObl()
            );

        } else if (event instanceof TxEvents.MemoryUpdateProhibitionEvent e) {
            return new TxCmd.UpdateProhibitionTxCmd(
                    new Prohibition(e.getName(), e.getSubject(), e.getAccessRightSet(), e.isIntersection(), e.getContainers()),
                    e.getOldPro()
            );

        } else if (event instanceof TxEvents.MemorySetResourceAccessRightsEvent e) {
            return new TxCmd.SetResourceAccessRightsTxCmd(
                    e.getOldAccessRights(),
                    e.getNewAccessRights()
            );
        }

       throw new UnsupportedPolicyEvent(event);
    }

    static class SetResourceAccessRightsTxCmd extends TxCmd<MemoryGraph> {

        private AccessRightSet oldAccessRights;
        private AccessRightSet newAccessRights;

        public SetResourceAccessRightsTxCmd(AccessRightSet oldAccessRights, AccessRightSet newAccessRights) {
            super(Type.GRAPH);

            this.oldAccessRights = oldAccessRights;
            this.newAccessRights = newAccessRights;
        }

        @Override
        public void rollback(MemoryGraph store) throws PMException {
            store.setResourceAccessRights(oldAccessRights);
        }
    }

    static class CreatePolicyClassTxCmd extends TxCmd<MemoryGraph> {
            
        private String name;
        private Map<String, String> properties;

        public CreatePolicyClassTxCmd(String name, Map<String, String> properties) {
            super(Type.GRAPH);
            this.name = name;
            this.properties = properties;
        }

        @Override
        public void rollback(MemoryGraph store) throws PMException {
            store.deleteNode(name);
        }
    }

    static class CreateObjectAttributeTxCmd extends TxCmd<MemoryGraph> {
        private final String name;
        private final Map<String, String> properties;
        private final List<String> parents;

        public CreateObjectAttributeTxCmd(String name, Map<String, String> properties, List<String> parents) {
            super(Type.GRAPH);
            this.name = name;
            this.properties = properties;
            this.parents = parents;
        }

        @Override
        public void rollback(MemoryGraph store) throws PMException {
            store.deleteNode(name);
        }
    }

    static class CreateUserAttributeTxCmd extends TxCmd<MemoryGraph> {
        private final String name;
        private final Map<String, String> properties;
        private final List<String> parents;

        public CreateUserAttributeTxCmd(String name, Map<String, String> properties, List<String> parents) {
            super(Type.GRAPH);
            this.name = name;
            this.properties = properties;
            this.parents = parents;
        }

        @Override
        public void rollback(MemoryGraph store) throws PMException {
            store.deleteNode(name);
        }
    }

    static class CreateObjectTxCmd extends TxCmd<MemoryGraph> {
        private final String name;
        private final Map<String, String> properties;
        private final List<String> parents;

        public CreateObjectTxCmd(String name, Map<String, String> properties, List<String> parents) {
            super(Type.GRAPH);
            this.name = name;
            this.properties = properties;
            this.parents = parents;
        }

        @Override
        public void rollback(MemoryGraph store) throws PMException {
            store.deleteNode(name);
        }
    }

    static class CreateUserTxCmd extends TxCmd<MemoryGraph> {
        private final String name;
        private final Map<String, String> properties;
        private final List<String> parents;

        public CreateUserTxCmd(String name, Map<String, String> properties, List<String> parents) {
            super(Type.GRAPH);
            this.name = name;
            this.properties = properties;
            this.parents = parents;
        }

        @Override
        public void rollback(MemoryGraph store) throws PMException {
            store.deleteNode(name);
        }
    }

    static class SetNodePropertiesTxCmd extends TxCmd<MemoryGraph> {
        private final String name;
        private final Map<String, String> oldProperties;
        private final Map<String, String> newProperties;

        public SetNodePropertiesTxCmd(String name, Map<String, String> oldProperties, Map<String, String> newProperties) {
            super(Type.GRAPH);
            this.name = name;
            this.oldProperties = oldProperties;
            this.newProperties = newProperties;
        }

        @Override
        public void rollback(MemoryGraph store) throws PMException {
            store.setNodeProperties(name, oldProperties);
        }
    }

    static class DeleteNodeTxCmd extends TxCmd<MemoryGraph> {
        private final String name;
        private final Node nodeToDelete;
        private final List<String> parents;

        public DeleteNodeTxCmd(String name, Node nodeToDelete, List<String> parents) {
            super(Type.GRAPH);
            this.name = name;
            this.nodeToDelete = nodeToDelete;
            this.parents = parents;
        }

        @Override
        public void rollback(MemoryGraph store) throws PMException {
            NodeType type = nodeToDelete.getType();
            Map<String, String> properties = nodeToDelete.getProperties();

            switch (type) {
                case PC -> store.createPolicyClass(name, properties);
                case OA -> store.createObjectAttribute(name, properties, parents);
                case UA -> store.createUserAttribute(name, properties, parents);
                case O -> store.createObject(name, properties, parents);
                case U -> store.createUser(name, properties, parents);
            }
        }
    }

    static final class AssignTxCmd extends TxCmd<MemoryGraph> {
        private final String child;
        private final String parent;

        public AssignTxCmd(String child, String parent) {
            super(Type.GRAPH);
            this.child = child;
            this.parent = parent;
        }

        @Override
        public void rollback(MemoryGraph store) throws PMException {
            store.deassign(child, parent);
        }
    }

    static class DeassignTxCmd extends TxCmd<MemoryGraph> {
        private final String child;
        private final String parent;

        public DeassignTxCmd(String child, String parent) {
            super(Type.GRAPH);
            this.child = child;
            this.parent = parent;
        }

        @Override
        public void rollback(MemoryGraph store) throws PMException {
            store.assign(child, parent);
        }
    }

    static class AssociateTxCmd extends TxCmd<MemoryGraph> {
        private final Association association;

        public AssociateTxCmd(Association association) {
            super(Type.GRAPH);
            this.association = association;
        }

        @Override
        public void rollback(MemoryGraph store) throws PMException {
            store.dissociate(association.getSource(), association.getTarget());
        }
    }

    static class DissociateTxCmd extends TxCmd<MemoryGraph> {
        private final Association association;

        public DissociateTxCmd(Association association) {
            super(Type.GRAPH);
            this.association = association;
        }

        @Override
        public void rollback(MemoryGraph store) throws PMException {
            store.associate(association.getSource(), association.getTarget(), association.getAccessRightSet());
        }
    }

    static class CreateProhibitionTxCmd extends TxCmd<MemoryProhibitions> {
        private final Prohibition prohibition;

        public CreateProhibitionTxCmd(Prohibition prohibition) {
            super(Type.PROHIBITIONS);
            this.prohibition = prohibition;
        }

        @Override
        public void rollback(MemoryProhibitions store) throws PMException {
            store.delete(prohibition.getName());
        }
    }

    static class UpdateProhibitionTxCmd extends TxCmd<MemoryProhibitions> {
        private final Prohibition newProhibition;
        private final Prohibition oldProhibition;

        public UpdateProhibitionTxCmd(Prohibition newProhibition, Prohibition oldProhibition) {
            super(Type.PROHIBITIONS);
            this.newProhibition = newProhibition;
            this.oldProhibition = oldProhibition;
        }

        @Override
        public void rollback(MemoryProhibitions store) throws PMException {
            store.update(
                    oldProhibition.getName(),
                    oldProhibition.getSubject(),
                    oldProhibition.getAccessRightSet(),
                    oldProhibition.isIntersection(),
                    oldProhibition.getContainers().toArray(new ContainerCondition[]{})
            );
        }
    }

    static class DeleteProhibitionTxCmd extends TxCmd<MemoryProhibitions> {
        private final Prohibition prohibitionToDelete;

        public DeleteProhibitionTxCmd(Prohibition prohibitionToDelete) {
            super(Type.PROHIBITIONS);
            this.prohibitionToDelete = prohibitionToDelete;
        }

        @Override
        public void rollback(MemoryProhibitions store) throws PMException {
            store.create(
                    prohibitionToDelete.getName(),
                    prohibitionToDelete.getSubject(),
                    prohibitionToDelete.getAccessRightSet(),
                    prohibitionToDelete.isIntersection(),
                    prohibitionToDelete.getContainers().toArray(new ContainerCondition[]{})
            );
        }
    }

    static class CreateObligationTxCmd extends TxCmd<MemoryObligations> {
        private final Obligation obligation;

        public CreateObligationTxCmd(Obligation obligation) {
            super(Type.OBLIGATIONS);
            this.obligation = obligation;
        }

        @Override
        public void rollback(MemoryObligations store) throws PMException {
            store.delete(obligation.getName());
        }
    }

    static class UpdateObligationTxCmd extends TxCmd<MemoryObligations> {
        private final Obligation newObligation;
        private final Obligation oldObligation;

        public UpdateObligationTxCmd(Obligation newObligation, Obligation oldObligation) {
            super(Type.OBLIGATIONS);
            this.newObligation = newObligation;
            this.oldObligation = oldObligation;
        }

        @Override
        public void rollback(MemoryObligations store) throws PMException {
            store.update(
                    oldObligation.getAuthor(),
                    oldObligation.getName(),
                    oldObligation.getRules().toArray(new Rule[]{})
            );
        }
    }

    static class DeleteObligationTxCmd extends TxCmd<MemoryObligations> {
        private final Obligation obligationToDelete;
        public DeleteObligationTxCmd(Obligation obligationToDelete) {
            super(Type.OBLIGATIONS);
            this.obligationToDelete = obligationToDelete;
        }

        @Override
        public void rollback(MemoryObligations store) throws PMException {
            store.create(
                    obligationToDelete.getAuthor(),
                    obligationToDelete.getName(),
                    obligationToDelete.getRules().toArray(new Rule[]{})
            );
        }
    }

    static class AddFunctionTxCmd extends TxCmd<MemoryUserDefinedPML> {
        private final FunctionDefinitionStatement functionDefinitionStatement;

        public AddFunctionTxCmd(FunctionDefinitionStatement functionDefinitionStatement) {
            super(Type.USER_DEFINED_PML);
            this.functionDefinitionStatement = functionDefinitionStatement;
        }

        @Override
        public void rollback(MemoryUserDefinedPML store) throws PMException {
            store.deleteFunction(functionDefinitionStatement.getSignature().getFunctionName());
        }
    }

    static class RemoveFunctionTxCmd extends TxCmd<MemoryUserDefinedPML> {
        private final FunctionDefinitionStatement functionDefinitionStatement;

        public RemoveFunctionTxCmd(FunctionDefinitionStatement functionDefinitionStatement) {
            super(Type.USER_DEFINED_PML);
            this.functionDefinitionStatement = functionDefinitionStatement;
        }

        @Override
        public void rollback(MemoryUserDefinedPML store) throws PMException {
            store.createFunction(functionDefinitionStatement);
        }
        
    }

    static class AddConstantTxCmd extends TxCmd<MemoryUserDefinedPML> {
        private final String constantName;
        private final Value value;

        public AddConstantTxCmd(String constantName, Value value) {
            super(Type.USER_DEFINED_PML);
            this.constantName = constantName;
            this.value = value;
        }

        @Override
        public void rollback(MemoryUserDefinedPML store) throws PMException {
            store.deleteConstant(constantName);
        }
    }

    static class RemoveConstantTxCmd extends TxCmd<MemoryUserDefinedPML> {
        private final String constantName;
        private final Value oldValue;

        public RemoveConstantTxCmd(String constantName, Value oldValue) {
            super(Type.USER_DEFINED_PML);
            this.constantName = constantName;
            this.oldValue = oldValue;
        }

        @Override
        public void rollback(MemoryUserDefinedPML store) throws PMException {
            store.createConstant(constantName, oldValue);
        }
    }
}
