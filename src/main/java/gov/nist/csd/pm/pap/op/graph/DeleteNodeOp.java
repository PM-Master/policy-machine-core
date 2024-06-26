package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.operand.Operand;
import gov.nist.csd.pm.pap.op.operand.PolicyElementListOperand;
import gov.nist.csd.pm.pap.op.operand.PolicyElementOperand;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.common.graph.node.NodeType.PC;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class DeleteNodeOp extends GraphOp {
    private final String name;
    private final NodeType type;
    private final Collection<String> descendants;

    public DeleteNodeOp(String name, NodeType type, Collection<String> descendants) {
        super("delete_node",
              new Operand("name", name),
              new Operand("type", type),
              new PolicyElementListOperand("descendants", descendants, getReqCapForType(type)));
        this.name = name;
        this.type = type;
        this.descendants = descendants;
    }

    public String getName() {
        return name;
    }

    public NodeType getType() {
        return type;
    }

    public Collection<String> getDescendants() {
        return descendants;
    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().graph().deleteNode(name);
    }

    @Override
    public void canExecute(PAP pap, UserContext userCtx) throws PMException {
        if (type == PC) {
            checkPrivilegesOnAdminNode(pap, userCtx, AdminPolicyNode.POLICY_CLASS_TARGETS, DELETE_POLICY_CLASS);
        } else {
            checkPrivilegesOnOperand(pap, userCtx, (PolicyElementOperand) operands.get(3));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeleteNodeOp that = (DeleteNodeOp) o;
        return Objects.equals(name, that.name) && Objects.equals(descendants, that.descendants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, descendants);
    }

    @Override
    public String toString() {
        return "DeleteNodeOp{" +
                "name='" + name + '\'' +
                ", descendants=" + descendants +
                '}';
    }

    private static String getReqCapForType(NodeType type) {
        return switch (type) {
            case OA -> DELETE_OBJECT_ATTRIBUTE;
            case UA -> DELETE_USER_ATTRIBUTE;
            case O -> DELETE_OBJECT;
            case U -> DELETE_USER;
            default -> DELETE_POLICY_CLASS;
        };
    }
}
