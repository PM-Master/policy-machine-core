package gov.nist.csd.pm.pap.op.graph;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DeleteNodeOp extends GraphOp {
    private final String name;
    private final Collection<String> parents;

    public DeleteNodeOp(String name, Collection<String> parents) {
        this.name = name;
        this.parents = parents;
    }

    @Override
    public String getOpName() {
        return "delete_node";
    }

    @Override
    public Object[] getOperands() {
        return operands(name, parents);
    }

    public String getName() {
        return name;
    }

    public Collection<String> getParents() {
        return parents;
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
        return Objects.equals(name, that.name) && Objects.equals(parents, that.parents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parents);
    }

    @Override
    public String toString() {
        return "DeleteNodeOp{" +
                "name='" + name + '\'' +
                ", parents=" + parents +
                '}';
    }
}
