package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.op.graph.DeleteNodeOp;
import gov.nist.csd.pm.pap.op.graph.SetNodePropertiesOp;
import gov.nist.csd.pm.pap.op.graph.DissociateOp;
import gov.nist.csd.pm.pap.op.operation.SetResourceOperationsOp;
import gov.nist.csd.pm.pap.op.obligation.DeleteObligationOp;
import gov.nist.csd.pm.pap.op.obligation.UpdateObligationOp;
import gov.nist.csd.pm.pap.op.prohibition.DeleteProhibitionOp;
import gov.nist.csd.pm.pap.op.prohibition.UpdateProhibitionOp;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.prohibition.Prohibition;

import java.util.Collection;
import java.util.Map;

public class TxOps {

    private TxOps() {}

    public static class MemorySetResourceOperationsOp extends SetResourceOperationsOp {

        private AccessRightSet oldAccessRights;
        private AccessRightSet newAccessRights;

        public MemorySetResourceOperationsOp(AccessRightSet oldAccessRights, AccessRightSet newAccessRights) {
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

    public static class MemoryDeleteNodeOp extends DeleteNodeOp {

        private Node node;

        public MemoryDeleteNodeOp(String name, Node node, Collection<String> descendants) {
            super(name, node.getType(), descendants);
            this.node = node;
        }

        public Node getNode() {
            return node;
        }
    }

    public static class MemoryDeleteObligationOp extends DeleteObligationOp {

        private Obligation obligationToDelete;

        public MemoryDeleteObligationOp(Obligation obligationToDelete) {
            super(obligationToDelete.getAuthor(), obligationToDelete.getName(), obligationToDelete.getRules());
            this.obligationToDelete = obligationToDelete;
        }

        public Obligation getObligationToDelete() {
            return obligationToDelete;
        }
    }

    public static class MemoryDeleteProhibitionOp extends DeleteProhibitionOp {

        private Prohibition prohibitionToDelete;

        public MemoryDeleteProhibitionOp(Prohibition prohibition) {
            super(prohibition.getName(), prohibition.getSubject(), prohibition.getAccessRightSet(),
                    prohibition.isIntersection(), prohibition.getContainers());
            prohibitionToDelete = prohibition;
        }

        public Prohibition getProhibitionToDelete() {
            return prohibitionToDelete;
        }
    }

    public static class MemoryDissociateOp extends DissociateOp {

        private AccessRightSet accessRightSet;

        public MemoryDissociateOp(String ua, String target, AccessRightSet accessRightSet) {
            super(ua, target);
            this.accessRightSet = accessRightSet;
        }

        public AccessRightSet getAccessRightSet() {
            return accessRightSet;
        }
    }

    public static class MemorySetNodePropertiesOp extends SetNodePropertiesOp {

        private Map<String, String> oldProps;

        public MemorySetNodePropertiesOp(String name, Map<String, String> oldProps, Map<String, String> newProps) {
            super(name, newProps);
            this.oldProps = oldProps;
        }

        public Map<String, String> getOldProps() {
            return oldProps;
        }
    }

    public static class MemoryUpdateObligationOp extends UpdateObligationOp {

        private Obligation oldObl;

        public MemoryUpdateObligationOp(Obligation newObl, Obligation oldObl) {
            super(newObl.getAuthor(), newObl.getName(), newObl.getRules());
            this.oldObl = oldObl;
        }

        public Obligation getOldObl() {
            return oldObl;
        }
    }

    public static class MemoryUpdateProhibitionOp extends UpdateProhibitionOp {

        private Prohibition oldPro;

        public MemoryUpdateProhibitionOp(Prohibition newPro, Prohibition oldPro) {
            super(newPro.getName(), newPro.getSubject(), newPro.getAccessRightSet(), newPro.isIntersection(), newPro.getContainers());
            this.oldPro = oldPro;
        }

        public Prohibition getOldPro() {
            return oldPro;
        }
    }
}
