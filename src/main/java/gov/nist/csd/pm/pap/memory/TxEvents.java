package gov.nist.csd.pm.pap.memory;

import gov.nist.csd.pm.policy.events.graph.DeleteNodeEvent;
import gov.nist.csd.pm.policy.events.graph.DissociateEvent;
import gov.nist.csd.pm.policy.events.graph.SetNodePropertiesEvent;
import gov.nist.csd.pm.policy.events.graph.SetResourceAccessRightsEvent;
import gov.nist.csd.pm.policy.events.obligations.DeleteObligationEvent;
import gov.nist.csd.pm.policy.events.obligations.UpdateObligationEvent;
import gov.nist.csd.pm.policy.events.prohibitions.DeleteProhibitionEvent;
import gov.nist.csd.pm.policy.events.prohibitions.UpdateProhibitionEvent;
import gov.nist.csd.pm.policy.events.userdefinedpml.DeleteConstantEvent;
import gov.nist.csd.pm.policy.events.userdefinedpml.DeleteFunctionEvent;
import gov.nist.csd.pm.policy.model.access.AccessRightSet;
import gov.nist.csd.pm.policy.model.graph.nodes.Node;
import gov.nist.csd.pm.policy.model.obligation.Obligation;
import gov.nist.csd.pm.policy.model.prohibition.Prohibition;
import gov.nist.csd.pm.policy.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.policy.pml.value.Value;

import java.util.List;
import java.util.Map;

public class TxEvents {

    private TxEvents() {}

    public static class MemorySetResourceAccessRightsEvent extends SetResourceAccessRightsEvent {

        private AccessRightSet oldAccessRights;
        private AccessRightSet newAccessRights;

        public MemorySetResourceAccessRightsEvent(AccessRightSet oldAccessRights, AccessRightSet newAccessRights) {
            super(newAccessRights);

            this.oldAccessRights = oldAccessRights;
            this.newAccessRights = newAccessRights;
        }

        public AccessRightSet getOldAccessRights() {
            return oldAccessRights;
        }

        public AccessRightSet getNewAccessRights() {
            return newAccessRights;
        }
    }

    public static class MemoryDeleteNodeEvent extends DeleteNodeEvent {

        private Node node;
        private List<String> parents;

        public MemoryDeleteNodeEvent(String name, Node node, List<String> parents) {
            super(name);
            this.node = node;
            this.parents = parents;
        }

        public Node getNode() {
            return node;
        }

        public List<String> getParents() {
            return parents;
        }
    }

    public static class MemoryDeleteObligationEvent extends DeleteObligationEvent {

        private Obligation obligationToDelete;

        public MemoryDeleteObligationEvent(Obligation obligationToDelete) {
            super(obligationToDelete.getName());
            this.obligationToDelete = obligationToDelete;
        }

        public Obligation getObligationToDelete() {
            return obligationToDelete;
        }
    }

    public static class MemoryDeleteProhibitionEvent extends DeleteProhibitionEvent {

        private Prohibition prohibitionToDelete;

        public MemoryDeleteProhibitionEvent(Prohibition prohibition) {
            super(prohibition.getName());
            prohibitionToDelete = prohibition;
        }

        public Prohibition getProhibitionToDelete() {
            return prohibitionToDelete;
        }
    }

    public static class MemoryDissociateEvent extends DissociateEvent {

        private AccessRightSet accessRightSet;

        public MemoryDissociateEvent(String ua, String target, AccessRightSet accessRightSet) {
            super(ua, target);
            this.accessRightSet = accessRightSet;
        }

        public AccessRightSet getAccessRightSet() {
            return accessRightSet;
        }
    }

    public static class MemoryDeleteConstantEvent extends DeleteConstantEvent {

        private Value value;

        public MemoryDeleteConstantEvent(String constantName, Value value) {
            super(constantName);
            this.value = value;
        }

        public Value getValue() {
            return value;
        }
    }

    public static class MemoryDeleteFunctionEvent extends DeleteFunctionEvent {

        private FunctionDefinitionStatement functionDefinitionStatement;

        public MemoryDeleteFunctionEvent(FunctionDefinitionStatement functionDefinitionStatement) {
            super(functionDefinitionStatement.signature().getFunctionName());
            this.functionDefinitionStatement = functionDefinitionStatement;
        }

        public FunctionDefinitionStatement getFunctionDefinitionStatement() {
            return functionDefinitionStatement;
        }
    }

    public static class MemorySetNodePropertiesEvent extends SetNodePropertiesEvent {

        private Map<String, String> oldProps;

        public MemorySetNodePropertiesEvent(String name, Map<String, String> oldProps, Map<String, String> newProps) {
            super(name, newProps);
            this.oldProps = oldProps;
        }

        public Map<String, String> getOldProps() {
            return oldProps;
        }
    }

    public static class MemoryUpdateObligationEvent extends UpdateObligationEvent {

        private Obligation oldObl;

        public MemoryUpdateObligationEvent(Obligation newObl, Obligation oldObl) {
            super(newObl.getAuthor(), newObl.getName(), newObl.getRules());
            this.oldObl = oldObl;
        }

        public Obligation getOldObl() {
            return oldObl;
        }
    }

    public static class MemoryUpdateProhibitionEvent extends UpdateProhibitionEvent {

        private Prohibition oldPro;

        public MemoryUpdateProhibitionEvent(Prohibition newPro, Prohibition oldPro) {
            super(newPro.getName(), newPro.getSubject(), newPro.getAccessRightSet(), newPro.isIntersection(), newPro.getContainers());
            this.oldPro = oldPro;
        }

        public Prohibition getOldPro() {
            return oldPro;
        }
    }
}
