package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.op.Operation;

import java.util.*;

public abstract class ProhibitionOp extends Operation {

    protected final String name;
    protected final ProhibitionSubject subject;
    protected final AccessRightSet accessRightSet;
    protected final boolean intersection;
    protected final Map<String, Boolean> containers;

    public ProhibitionOp(String name,
                               ProhibitionSubject subject,
                               AccessRightSet accessRightSet,
                               boolean intersection,
                               List<ContainerCondition> containers) {
        this.name = name;
        this.subject = subject;
        this.accessRightSet = accessRightSet;
        this.intersection = intersection;
        this.containers = new HashMap<>();

        for (ContainerCondition c : containers) {
            this.containers.put(c.getName(), c.isComplement());
        }
    }

    @Override
    public abstract String getOpName();

    @Override
    public Object[] getOperands() {
        return Operation.operands(subject.getName(), containers);
    }

    public String getName() {
        return name;
    }

    public ProhibitionSubject getSubject() {
        return subject;
    }

    public AccessRightSet getAccessRightSet() {
        return accessRightSet;
    }

    public boolean isIntersection() {
        return intersection;
    }

    public Map<String, Boolean> getContainers() {
        return containers;
    }

    public List<ContainerCondition> getContainerConditions() {
        List<ContainerCondition> containerConditions = new ArrayList<>();
        for (Map.Entry<String, Boolean> e : containers.entrySet()) {
            containerConditions.add(new ContainerCondition(e.getKey(), e.getValue()));
        }

        return containerConditions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProhibitionOp that = (ProhibitionOp) o;
        return intersection == that.intersection && Objects.equals(name, that.name) && Objects.equals(
                subject,
                that.subject
        ) && Objects.equals(accessRightSet, that.accessRightSet) && Objects.equals(
                containers,
                that.containers
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, subject, accessRightSet, intersection, containers);
    }
}
