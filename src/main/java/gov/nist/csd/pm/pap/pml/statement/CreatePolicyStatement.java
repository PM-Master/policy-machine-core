package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;
import gov.nist.csd.pm.pap.pml.expression.literal.ArrayLiteral;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.type.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreatePolicyStatement extends PMLStatement {

    private Expression name;
    private Expression properties;
    private List<CreateOrAssignAttributeStatement> uas;
    private List<CreateOrAssignAttributeStatement> oas;
    private List<AssociateStatement> assocs;

    public CreatePolicyStatement(Expression name, Expression properties) {
        this.name = name;
        this.properties = properties;
    }

    public CreatePolicyStatement(Expression name) {
        this.name = name;
        this.properties = null;
    }

    public CreatePolicyStatement(Expression name, Expression properties, List<CreateOrAssignAttributeStatement> uas,
                                 List<CreateOrAssignAttributeStatement> oas, List<AssociateStatement> assocs) {
        this.name = name;
        this.properties = properties;
        this.uas = uas;
        this.oas = oas;
        this.assocs = assocs;
    }

    public Expression getName() {
        return name;
    }

    public Expression getProperties() {
        return properties;
    }

    public List<CreateOrAssignAttributeStatement> getUas() {
        return uas;
    }

    public List<CreateOrAssignAttributeStatement> getOas() {
        return oas;
    }

    public List<AssociateStatement> getAssocs() {
        return assocs;
    }

    @Override
    public Value execute(ExecutionContext ctx, PolicyPoint policy) throws PMException {
        Map<String, String> props = new HashMap<>();

        if (this.properties != null) {
            Value propertiesValue = properties.execute(ctx, policy);
            for (Map.Entry<Value, Value> e : propertiesValue.getMapValue().entrySet()) {
                props.put(e.getKey().getStringValue(), e.getValue().getStringValue());
            }
        }

        policy.modify().graph().createPolicyClass(name.execute(ctx, policy).getStringValue(), props);

        // create hierarchy
        createHierarchy(ctx, policy);

        return new VoidValue();
    }

    private void createHierarchy(ExecutionContext ctx, PolicyPoint pap) throws PMException {
        // create uas
        if (uas != null) {
            createUas(ctx, pap);
        }

        // create oas
        if (oas != null) {
            createOas(ctx, pap);
        }

        // assocs
        if (assocs != null) {
            createAssocs(ctx, pap);
        }
    }

    private void createUas(ExecutionContext ctx, PolicyPoint pap) throws PMException {
        for (CreateOrAssignAttributeStatement stmt : uas) {
            stmt.execute(ctx, pap);
        }
    }

    private void createOas(ExecutionContext ctx, PolicyPoint pap) throws PMException {
        for (CreateOrAssignAttributeStatement stmt : oas) {
            stmt.execute(ctx, pap);
        }
    }

    private void createAssocs(ExecutionContext ctx, PolicyPoint pap) throws PMException {
        for (AssociateStatement associateStatement : assocs) {
            associateStatement.execute(ctx, pap);
        }
    }

    @Override
    public String toFormattedString(int indentLevel) {
        String propertiesStr = (properties == null ? "" : " with properties " + properties);
        String hierarchyStr = getHierarchyStr(indentLevel);
        return indent(indentLevel) + String.format("create PC %s%s%s", name, propertiesStr, hierarchyStr);
    }

    private String getHierarchyStr(int indentLevel) {
        String hierarchyStr = "";

        if (uas != null && !uas.isEmpty()) {
            hierarchyStr += getUaStr(indentLevel) + "\n";
        }

        if (oas != null && !oas.isEmpty()) {
            hierarchyStr += getOaStr(indentLevel) + "\n";
        }

        if (assocs != null && !assocs.isEmpty()) {
            hierarchyStr += getAssocStr(indentLevel);
        }

        if (hierarchyStr.isEmpty()) {
            return hierarchyStr;
        }

        return String.format(" {\n%s\n%s}", hierarchyStr, indent(indentLevel));
    }

    private String getUaStr(int indentLevel) {
        return getAttrStr(indentLevel, uas, "user attributes");
    }

    private String getOaStr(int indentLevel) {
        return getAttrStr(indentLevel, oas, "object attributes");
    }

    private String getAssocStr(int indentLevel) {
        indentLevel++;

        StringBuilder assocsStr = new StringBuilder();
        for (AssociateStatement associateStatement : assocs) {
            assocsStr.append("\n")
                     .append(indent(indentLevel+1))
                     .append(associateStatement.getUa())
                     .append(" and ")
                     .append(associateStatement.getTarget())
                     .append(" with ")
                     .append(associateStatement.getAccessRights());
        }

        String rootIndent = indent(indentLevel);
        return String.format("%sassociations {%s\n%s}", rootIndent, assocsStr, rootIndent);
    }

    private String getAttrStr(int indentLevel, List<CreateOrAssignAttributeStatement> attrs, String label) {
        indentLevel++;

        Map<Expression, Integer> parentIndents = new HashMap<>();
        parentIndents.put(name, indentLevel);

        StringBuilder uaStr = new StringBuilder();
        for (CreateOrAssignAttributeStatement stmt : attrs) {
            String propertiesStr = (stmt.getWithProperties() == null ? "" : " " + stmt.getWithProperties());

            int parentIndent = parentIndents.get(stmt.parent);
            int indent = parentIndent+1;

            uaStr.append("\n")
                 .append(indent(indent))
                 .append(stmt.getName())
                 .append(propertiesStr);

            parentIndents.put(stmt.getName(), indent);
        }

        String rootIndent = indent(indentLevel);
        return String.format("%s%s {%s\n%s}", rootIndent, label, uaStr, rootIndent);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreatePolicyStatement that = (CreatePolicyStatement) o;
        return Objects.equals(name, that.name) && Objects.equals(
                properties, that.properties) && Objects.equals(uas, that.uas) && Objects.equals(
                oas, that.oas) && Objects.equals(assocs, that.assocs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static class CreateOrAssignAttributeStatement extends CreateNonPCStatement {

        private Expression parent;

        public CreateOrAssignAttributeStatement(Expression name, NodeType type, Expression assignTo) {
            super(name, type, new ArrayLiteral(Type.string(), assignTo));

            this.parent = assignTo;
        }

        public CreateOrAssignAttributeStatement(Expression name, NodeType type, Expression assignTo, Expression withProperties) {
            super(name, type, new ArrayLiteral(Type.string(), assignTo), withProperties);

            this.parent = assignTo;
        }

        @Override
        public Value execute(ExecutionContext ctx, PolicyPoint policy) throws PMException {
            Value nameValue = getName().execute(ctx, policy);
            
            if (!policy.query().graph().nodeExists(nameValue.getStringValue())) {
                return super.execute(ctx, policy);
            }

            AssignStatement assignStatement = new AssignStatement(getName(), getAssignTo());
            return assignStatement.execute(ctx, policy);
        }

        @Override
        public String toFormattedString(int indentLevel) {
            Expression withProperties = getWithProperties();
            return indent(indentLevel) + getName() + (withProperties == null ? "" : " " + withProperties);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            CreateOrAssignAttributeStatement that = (CreateOrAssignAttributeStatement) o;
            return Objects.equals(parent, that.parent);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), parent);
        }
    }
}
