package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;

import java.util.Objects;


public class SetResourceAccessRightsStatement extends PMLStatement{

    private final Expression arExpr;

    public SetResourceAccessRightsStatement(Expression arExprList) {
        this.arExpr = arExprList;
    }

    @Override
    public Value execute(ExecutionContext ctx, PolicyModification policyModification) throws PMException {
        Value arValue = arExpr.execute(ctx, policyModification);
        AccessRightSet accessRightSet = new AccessRightSet();
        for (Value v : arValue.getArrayValue()) {
            accessRightSet.add(v.getStringValue());
        }

        policyModification.graph().setResourceAccessRights(accessRightSet);

        return new VoidValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SetResourceAccessRightsStatement that = (SetResourceAccessRightsStatement) o;
        return Objects.equals(arExpr, that.arExpr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arExpr);
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return indent(indentLevel) + String.format("set resource access rights %s", arExpr);
    }
}
