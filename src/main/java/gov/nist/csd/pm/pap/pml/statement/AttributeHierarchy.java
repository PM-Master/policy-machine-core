package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class AttributeHierarchy extends PMLStatement{

    String pc;

    List<Attribute> attrs;

    public AttributeHierarchy(List<Attribute> attrs) {
        this.attrs = attrs;
    }

    public List<Attribute> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<Attribute> attrs) {
        this.attrs = attrs;
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return null;
    }
}