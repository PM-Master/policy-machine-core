package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;

import java.util.List;
import java.util.Objects;

public class Attribute extends PMLStatement {

    private String parent;
    private Expression nameExpr;
    private List<Attribute> childAttrs;

    public Attribute(Expression nameExpr, List<Attribute> childAttrs) {
        this.nameExpr = nameExpr;
        this.childAttrs = childAttrs;
    }

    public Expression getNameExpr() {
        return nameExpr;
    }

    public void setNameExpr(Expression nameExpr) {
        this.nameExpr = nameExpr;
    }

    public List<Attribute> getChildAttrs() {
        return childAttrs;
    }

    public void setChildAttrs(
            List<Attribute> childAttrs) {
        this.childAttrs = childAttrs;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public Value execute(ExecutionContext ctx, PolicyPoint policy) throws PMException {
        String name = nameExpr.execute(ctx, policy).getStringValue();

        for (Attribute child : childAttrs) {
            child.setParent(name);

            child.execute(ctx, policy);
        }

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
        Attribute attribute = (Attribute) o;
        return Objects.equals(parent, attribute.parent) && Objects.equals(
                nameExpr, attribute.nameExpr) && Objects.equals(childAttrs, attribute.childAttrs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, nameExpr, childAttrs);
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return null;
    }
}
