package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.expression.NegatedExpression;
import gov.nist.csd.pm.pap.pml.expression.literal.ArrayLiteral;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.expression.reference.ReferenceByID;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ComplementedValue;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.isAdminAccessRight;

public class CreateProhibitionStatement extends PMLStatement {

    private Expression name;
    private Expression subject;
    private ProhibitionSubject.Type subjectType;
    private Expression accessRights;
    private boolean isIntersection;
    private Expression containers;

    public CreateProhibitionStatement(Expression name, Expression subject, ProhibitionSubject.Type subjectType, Expression accessRights,
                                      boolean isIntersection, Expression containers) {
        this.name = name;
        this.subject = subject;
        this.subjectType = subjectType;
        this.accessRights = accessRights;
        this.isIntersection = isIntersection;
        this.containers = containers;
    }

    public Expression getName() {
        return name;
    }

    public Expression getSubject() {
        return subject;
    }

    public ProhibitionSubject.Type getSubjectType() {
        return subjectType;
    }

    public Expression getAccessRights() {
        return accessRights;
    }

    public boolean isIntersection() {
        return isIntersection;
    }

    public Expression getContainers() {
        return containers;
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        Value idValue = this.name .execute(ctx, pap);
        Value subjectValue = this.subject.execute(ctx, pap);
        Value permissionsValue = this.accessRights.execute(ctx, pap);

        List<Value> arrayValue = permissionsValue.getArrayValue();
        AccessRightSet ops = new AccessRightSet();
        for (Value v : arrayValue) {
            ops.add(v.getStringValue());
        }

        List<ContainerCondition> containerConditions = new ArrayList<>();
        for (Value container : containers.execute(ctx, pap).getArrayValue()) {
            boolean isComplement = container instanceof ComplementedValue;
            String containerName = container.getStringValue();

            containerConditions.add(new ContainerCondition(containerName, isComplement));
        }


        pap.modify().prohibitions().create(
                idValue.getStringValue(),
                new ProhibitionSubject(subjectValue.getStringValue(), subjectType),
                ops,
                isIntersection,
                containerConditions
        );

        return new VoidValue();
    }

    @Override
    public String toFormattedString(int indentLevel) {
        String subjectStr = getSubjectStr();
        String indent = indent(indentLevel);
        return String.format(
                """
                %screate prohibition %s
                %s  deny %s %s
                %s  access rights %s
                %s  on %s of %s""",
                indent, name, indent, subjectStr, subject, indent, accessRights, indent, (isIntersection ? "intersection" : "union"), containers
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateProhibitionStatement that = (CreateProhibitionStatement) o;
        return isIntersection == that.isIntersection && Objects.equals(name, that.name) && Objects.equals(subject, that.subject) && Objects.equals(accessRights, that.accessRights) && Objects.equals(containers, that.containers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, subject, accessRights, isIntersection, containers);
    }

    private String getSubjectStr() {
        String subjectStr = "";
        switch (subjectType) {
            case USER_ATTRIBUTE -> subjectStr = "UA";
            case USER -> subjectStr = "U";
            case PROCESS -> subjectStr = "process";
        }

        return subjectStr;
    }

    public static class Container implements Serializable {
        private boolean isComplement;
        private Expression name;

        public Container(boolean isComplement, Expression name) {
            this.isComplement = isComplement;
            this.name = name;
        }

        @Override
        public String toString() {
            return (isComplement ? "!" : "") + name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Container container = (Container) o;
            return isComplement == container.isComplement && Objects.equals(name, container.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(isComplement, name);
        }
    }

    public static CreateProhibitionStatement fromProhibition(Prohibition prohibition) {
        ArrayLiteral arrayLiteral = new ArrayLiteral(Type.string());
        for (String ar : prohibition.getAccessRightSet()) {
            if (isAdminAccessRight(ar)) {
                arrayLiteral.add(new ReferenceByID(ar));
            } else {
                arrayLiteral.add(new StringLiteral(ar));
            }
        }

        ArrayLiteral containers = new ArrayLiteral(Type.string());
        for (ContainerCondition cc : prohibition.getContainers()) {
            StringLiteral s = new StringLiteral(cc.getName());
            if (cc.isComplement()) {
                containers.add(new NegatedExpression(s));
            } else {
                containers.add(s);
            }
        }

        return new CreateProhibitionStatement(
                new StringLiteral(prohibition.getName()),
                new StringLiteral(prohibition.getSubject().getName()),
                prohibition.getSubject().getType(),
                arrayLiteral,
                prohibition.isIntersection(),
                containers
        );
    }
}
