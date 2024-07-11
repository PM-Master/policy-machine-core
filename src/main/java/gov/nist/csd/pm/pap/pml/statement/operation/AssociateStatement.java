package gov.nist.csd.pm.pap.pml.statement.operation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.graph.AssociateOp;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;
import java.util.Objects;


public class AssociateStatement extends OperationStatement {

    private Expression ua;
    private Expression target;
    private Expression accessRights;
    
    public AssociateStatement(Expression ua, Expression target, Expression accessRights) {
        super(new AssociateOp());
        
        this.ua = ua;
        this.target = target;
        this.accessRights = accessRights;
    }

    @Override
    public List<Object> prepareOperands(ExecutionContext ctx, PAP pap) throws PMException {
        Value uaValue = ctx.executeStatement(pap, ua);
        Value targetValue = ctx.executeStatement(pap, target);
        Value accessRightsValue = ctx.executeStatement(pap, accessRights);

        AccessRightSet accessRightSet = new AccessRightSet();
        for (Value v : accessRightsValue.getArrayValue()) {
            accessRightSet.add(v.getStringValue());
        }

        return List.of(uaValue.getStringValue(), targetValue.getStringValue(), accessRightSet);
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return indent(indentLevel) + String.format("associate %s and %s with %s",
                ua, target, accessRights);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssociateStatement that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(ua, that.ua) && Objects.equals(
                target,
                that.target
        ) && Objects.equals(accessRights, that.accessRights);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ua, target, accessRights);
    }
}
