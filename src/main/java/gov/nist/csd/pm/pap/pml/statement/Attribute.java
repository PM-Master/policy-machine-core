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

    private String descendant;
    private Expression nameExpr;
    private List<Attribute> ascendantAttrs;

    public Attribute(Expression nameExpr, List<Attribute> ascendantAttrs) {
        this.nameExpr = nameExpr;
        this.ascendantAttrs = ascendantAttrs;
    }

    public Expression getNameExpr() {
        return nameExpr;
    }

    public void setNameExpr(Expression nameExpr) {
        this.nameExpr = nameExpr;
    }

    public List<Attribute> getAscendantAttrs() {
        return ascendantAttrs;
    }

    public void setAscendantAttrs(List<Attribute> ascendantAttrs) {
        this.ascendantAttrs = ascendantAttrs;
    }

    public String getDescendant() {
        return descendant;
    }

    public void setDescendant(String descendant) {
        this.descendant = descendant;
    }

    @Override
    public Value execute(ExecutionContext ctx, PolicyPoint policy) throws PMException {
        String name = nameExpr.execute(ctx, policy).getStringValue();

        for (Attribute ascendant : ascendantAttrs) {
            ascendant.setDescendant(name);

            ascendant.execute(ctx, policy);
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
        return Objects.equals(descendant, attribute.descendant) && Objects.equals(
                nameExpr, attribute.nameExpr) && Objects.equals(ascendantAttrs, attribute.ascendantAttrs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descendant, nameExpr, ascendantAttrs);
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return null;
    }
}
