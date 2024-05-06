package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.op.Operation;

import java.util.List;
import java.util.Objects;

public class CreateProhibitionOp extends ProhibitionsOp {
    private final String name;
    private final ProhibitionSubject subject;
    private final AccessRightSet accessRightSet;
    private final boolean intersection;
    private final List<ContainerCondition> containers;

    public CreateProhibitionOp(String name,
                               ProhibitionSubject subject,
                               AccessRightSet accessRightSet,
                               boolean intersection,
                               List<ContainerCondition> containers) {
        super(Operation.operands(name, subject, accessRightSet, intersection, containers));
        this.name = name;
        this.subject = subject;
        this.accessRightSet = accessRightSet;
        this.intersection = intersection;
        this.containers = containers;
    }

    @Override
    public String getOpName() {
        return "create_prohibition";
    }

    public String name() {
        return name;
    }

    public ProhibitionSubject subject() {
        return subject;
    }

    public AccessRightSet accessRightSet() {
        return accessRightSet;
    }

    public boolean intersection() {
        return intersection;
    }

    public List<ContainerCondition> containers() {
        return containers;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (CreateProhibitionOp) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.subject, that.subject) &&
                Objects.equals(this.accessRightSet, that.accessRightSet) &&
                this.intersection == that.intersection &&
                Objects.equals(this.containers, that.containers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, subject, accessRightSet, intersection, containers);
    }

    @Override
    public String toString() {
        return "CreateProhibitionOp[" +
                "name=" + name + ", " +
                "subject=" + subject + ", " +
                "accessRightSet=" + accessRightSet + ", " +
                "intersection=" + intersection + ", " +
                "containers=" + containers + ']';
    }


}
