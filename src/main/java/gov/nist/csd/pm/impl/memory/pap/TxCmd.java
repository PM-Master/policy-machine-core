package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.graph.*;
import gov.nist.csd.pm.pap.op.obligation.CreateObligationOp;
import gov.nist.csd.pm.pap.op.prohibition.CreateProhibitionOp;
import gov.nist.csd.pm.pap.op.pml.CreateConstantOp;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.pml.CreateFunctionOp;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;
import java.util.Map;

abstract class TxCmd implements TxRollbackSupport {

    private Type type;

    public TxCmd(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    enum Type {
        GRAPH,
        PROHIBITIONS,
        OBLIGATIONS,
        USER_DEFINED_PML
    }

    static TxCmd eventToCmd(Operation op) throws UnsupportedPolicyEvent {
        if (op instanceof CreateConstantOp o) {
            return new TxCmd.AddConstantTxCmd(
                    o.name(),
                    o.value()
            );

        } else if (op instanceof CreateFunctionOp o) {
            return new TxCmd.AddFunctionTxCmd(
                    o.functionDefinitionStatement()
            );

        } else if (op instanceof AssignOp o) {
            return new TxCmd.AssignTxCmd(
                    o.getChild(),
                    o.getParent()
            );

        } else if (op instanceof AssociateOp o) {
            return new TxCmd.AssociateTxCmd(
                    new Association(o.getUa(), o.getTarget(), o.getAccessRightSet())
            );

        } else if (op instanceof CreateObjectAttributeOp o) {
            return new TxCmd.CreateObjectAttributeTxCmd(
                    o.getName(),
                    o.getProperties(),
                    o.getParents()
            );

        } else if (op instanceof CreateObjectOp o) {
            return new TxCmd.CreateObjectTxCmd(
                    o.getName(),
                    o.getProperties(),
                    o.getParents()
            );

        } else if (op instanceof CreateObligationOp o) {
            return new TxCmd.CreateObligationTxCmd(
                    new Obligation(o.getAuthor(), o.getName(), o.getRules())
            );

        } else if (op instanceof CreatePolicyClassOp o) {
            return new TxCmd.CreatePolicyClassTxCmd(
                    o.getName(),
                    o.getProperties()
            );

        } else if (op instanceof CreateProhibitionOp o) {
            return new TxCmd.CreateProhibitionTxCmd(
                    new Prohibition(o.getName(), o.getSubject(), o.getAccessRightSet(), o.isIntersection(), o.getContainerConditions())
            );

        } else if (op instanceof CreateUserAttributeOp o) {
            return new TxCmd.CreateUserAttributeTxCmd(
                    o.getName(),
                    o.getProperties(),
                    o.getParents()
            );

        } else if (op instanceof CreateUserOp o) {
            return new TxCmd.CreateUserTxCmd(
                    o.getName(),
                    o.getProperties(),
                    o.getParents()
            );

        } else if (op instanceof DeassignOp o) {
            return new TxCmd.DeassignTxCmd(
                    o.getChild(),
                    o.getParent()
            );

        } else if (op instanceof TxOps.MemoryDeleteNodeOp o) {
            return new TxCmd.DeleteNodeTxCmd(
                    o.getName(),
                    o.getNode(),
                    o.getParents()
            );

        } else if (op instanceof TxOps.MemoryDeleteObligationOp o) {
            return new TxCmd.DeleteObligationTxCmd(
                    o.getObligationToDelete()
            );

        } else if (op instanceof TxOps.MemoryDeleteProhibitionOp o) {
            return new TxCmd.DeleteProhibitionTxCmd(
                    o.getProhibitionToDelete()
            );

        } else if (op instanceof TxOps.MemoryDissociateOp o) {
            return new TxCmd.DissociateTxCmd(
                    new Association(o.getUa(), o.getTarget(), o.getAccessRightSet())
            );

        } else if (op instanceof TxOps.MemoryDeleteConstantOp o) {
            return new TxCmd.RemoveConstantTxCmd(
                    o.name(),
                    o.getValue()
            );

        } else if (op instanceof TxOps.MemoryDeleteFunctionOp o) {
            return new TxCmd.RemoveFunctionTxCmd(o.getFunctionDefinitionStatement());

        } else if (op instanceof TxOps.MemorySetNodePropertiesOp o) {
            return new TxCmd.SetNodePropertiesTxCmd(
                    o.getName(),
                    o.getOldProps(),
                    o.getProperties()
            );

        } else if (op instanceof TxOps.MemoryUpdateObligationOp o) {
            return new TxCmd.UpdateObligationTxCmd(
                    new Obligation(o.getAuthor(), o.getName(), o.getRules()), o.getOldObl()
            );

        } else if (op instanceof TxOps.MemoryUpdateProhibitionOp o) {
            return new TxCmd.UpdateProhibitionTxCmd(
                    new Prohibition(o.getName(), o.getSubject(), o.getAccessRightSet(), o.isIntersection(), o.getContainerConditions()), o.getOldPro()
            );

        } else if (op instanceof TxOps.MemorySetResourceAccessRightsOp o) {
            return new TxCmd.SetResourceAccessRightsTxCmd(
                    o.getOldAccessRights(),
                    o.getNewAccessRights()
            );
        }

       throw new UnsupportedPolicyEvent(op);
    }

    static class SetResourceAccessRightsTxCmd extends TxCmd {

        private AccessRightSet oldAccessRights;
        private AccessRightSet newAccessRights;

        public SetResourceAccessRightsTxCmd(AccessRightSet oldAccessRights, AccessRightSet newAccessRights) {
            super(Type.GRAPH);

            this.oldAccessRights = oldAccessRights;
            this.newAccessRights = newAccessRights;
        }

        @Override
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.graph().setResourceAccessRights(oldAccessRights);
        }
    }

    static class CreatePolicyClassTxCmd extends TxCmd {
            
        private String name;
        private Map<String, String> properties;

        public CreatePolicyClassTxCmd(String name, Map<String, String> properties) {
            super(Type.GRAPH);
            this.name = name;
            this.properties = properties;
        }

        @Override
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.graph().deleteNode(name);
        }
    }

    static class CreateObjectAttributeTxCmd extends TxCmd {
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
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.graph().deleteNode(name);
        }
    }

    static class CreateUserAttributeTxCmd extends TxCmd {
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
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.graph().deleteNode(name);
        }
    }

    static class CreateObjectTxCmd extends TxCmd {
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
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.graph().deleteNode(name);
        }
    }

    static class CreateUserTxCmd extends TxCmd {
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
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.graph().deleteNode(name);
        }
    }

    static class SetNodePropertiesTxCmd extends TxCmd {
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
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.graph().setNodeProperties(name, oldProperties);
        }
    }

    static class DeleteNodeTxCmd extends TxCmd {
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
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            NodeType type = nodeToDelete.getType();
            Map<String, String> properties = nodeToDelete.getProperties();

            switch (type) {
                case PC -> memoryPolicy.graph().createPolicyClass(name, properties);
                case OA -> memoryPolicy.graph().createObjectAttribute(name, properties, parents);
                case UA -> memoryPolicy.graph().createUserAttribute(name, properties, parents);
                case O -> memoryPolicy.graph().createObject(name, properties, parents);
                case U -> memoryPolicy.graph().createUser(name, properties, parents);
            }
        }
    }

    static final class AssignTxCmd extends TxCmd {
        private final String child;
        private final String parent;

        public AssignTxCmd(String child, String parent) {
            super(Type.GRAPH);
            this.child = child;
            this.parent = parent;
        }

        @Override
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.graph().deassign(child, parent);
        }
    }

    static class DeassignTxCmd extends TxCmd {
        private final String child;
        private final String parent;

        public DeassignTxCmd(String child, String parent) {
            super(Type.GRAPH);
            this.child = child;
            this.parent = parent;
        }

        @Override
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.graph().assign(child, parent);
        }
    }

    static class AssociateTxCmd extends TxCmd {
        private final Association association;

        public AssociateTxCmd(Association association) {
            super(Type.GRAPH);
            this.association = association;
        }

        @Override
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.graph().dissociate(association.getSource(), association.getTarget());
        }
    }

    static class DissociateTxCmd extends TxCmd {
        private final Association association;

        public DissociateTxCmd(Association association) {
            super(Type.GRAPH);
            this.association = association;
        }

        @Override
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.graph().associate(association.getSource(), association.getTarget(), association.getAccessRightSet());
        }
    }

    static class CreateProhibitionTxCmd extends TxCmd {
        private final Prohibition prohibition;

        public CreateProhibitionTxCmd(Prohibition prohibition) {
            super(Type.PROHIBITIONS);
            this.prohibition = prohibition;
        }

        @Override
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.prohibitions().delete(prohibition.getName());
        }
    }

    static class UpdateProhibitionTxCmd extends TxCmd {
        private final Prohibition newProhibition;
        private final Prohibition oldProhibition;

        public UpdateProhibitionTxCmd(Prohibition newProhibition, Prohibition oldProhibition) {
            super(Type.PROHIBITIONS);
            this.newProhibition = newProhibition;
            this.oldProhibition = oldProhibition;
        }

        @Override
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.prohibitions().update(
                    oldProhibition.getName(),
                    oldProhibition.getSubject(),
                    oldProhibition.getAccessRightSet(),
                    oldProhibition.isIntersection(),
                    oldProhibition.getContainers().toArray(new ContainerCondition[]{})
            );
        }
    }

    static class DeleteProhibitionTxCmd extends TxCmd {
        private final Prohibition prohibitionToDelete;

        public DeleteProhibitionTxCmd(Prohibition prohibitionToDelete) {
            super(Type.PROHIBITIONS);
            this.prohibitionToDelete = prohibitionToDelete;
        }

        @Override
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.prohibitions().create(
                    prohibitionToDelete.getName(),
                    prohibitionToDelete.getSubject(),
                    prohibitionToDelete.getAccessRightSet(),
                    prohibitionToDelete.isIntersection(),
                    prohibitionToDelete.getContainers().toArray(new ContainerCondition[]{})
            );
        }
    }

    static class CreateObligationTxCmd extends TxCmd {
        private final Obligation obligation;

        public CreateObligationTxCmd(Obligation obligation) {
            super(Type.OBLIGATIONS);
            this.obligation = obligation;
        }

        @Override
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.obligations().delete(obligation.getName());
        }
    }

    static class UpdateObligationTxCmd extends TxCmd {
        private final Obligation newObligation;
        private final Obligation oldObligation;

        public UpdateObligationTxCmd(Obligation newObligation, Obligation oldObligation) {
            super(Type.OBLIGATIONS);
            this.newObligation = newObligation;
            this.oldObligation = oldObligation;
        }

        @Override
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.obligations().update(
                    oldObligation.getAuthor(),
                    oldObligation.getName(),
                    oldObligation.getRules().toArray(new Rule[]{})
            );
        }
    }

    static class DeleteObligationTxCmd extends TxCmd {
        private final Obligation obligationToDelete;
        public DeleteObligationTxCmd(Obligation obligationToDelete) {
            super(Type.OBLIGATIONS);
            this.obligationToDelete = obligationToDelete;
        }

        @Override
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.obligations().create(
                    obligationToDelete.getAuthor(),
                    obligationToDelete.getName(),
                    obligationToDelete.getRules().toArray(new Rule[]{})
            );
        }
    }

    static class AddFunctionTxCmd extends TxCmd {
        private final FunctionDefinitionStatement functionDefinitionStatement;

        public AddFunctionTxCmd(FunctionDefinitionStatement functionDefinitionStatement) {
            super(Type.USER_DEFINED_PML);
            this.functionDefinitionStatement = functionDefinitionStatement;
        }

        @Override
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.pml().deleteFunction(functionDefinitionStatement.getSignature().getFunctionName());
        }
    }

    static class RemoveFunctionTxCmd extends TxCmd {
        private final FunctionDefinitionStatement functionDefinitionStatement;

        public RemoveFunctionTxCmd(FunctionDefinitionStatement functionDefinitionStatement) {
            super(Type.USER_DEFINED_PML);
            this.functionDefinitionStatement = functionDefinitionStatement;
        }

        @Override
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.pml().createFunction(functionDefinitionStatement);
        }
        
    }

    static class AddConstantTxCmd extends TxCmd {
        private final String constantName;
        private final Value value;

        public AddConstantTxCmd(String constantName, Value value) {
            super(Type.USER_DEFINED_PML);
            this.constantName = constantName;
            this.value = value;
        }

        @Override
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.pml().deleteConstant(constantName);
        }
    }

    static class RemoveConstantTxCmd extends TxCmd {
        private final String constantName;
        private final Value oldValue;

        public RemoveConstantTxCmd(String constantName, Value oldValue) {
            super(Type.USER_DEFINED_PML);
            this.constantName = constantName;
            this.oldValue = oldValue;
        }

        @Override
        public void rollback(MemoryPolicy memoryPolicy) throws PMException {
            memoryPolicy.pml().createConstant(constantName, oldValue);
        }
    }
}
