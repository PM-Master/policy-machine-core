package gov.nist.csd.pm.pap.op.graph;

import java.util.Collection;
import java.util.Objects;

public class DeleteNodeOp extends GraphOp {
    private final String name;
    private final Collection<String> descendants;

    public DeleteNodeOp(String name, Collection<String> descendants) {
        this.name = name;
        this.descendants = descendants;
    }

    @Override
    public String getOpName() {
        return "delete_node";
    }

    @Override
    public Object[] getOperands() {
        return operands(name, descendants);
    }

    public String getName() {
        return name;
    }

    public Collection<String> getDescendants() {
        return descendants;
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
        return Objects.equals(name, that.name) && Objects.equals(descendants, that.descendants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, descendants);
    }

    @Override
    public String toString() {
        return "DeleteNodeOp{" +
                "name='" + name + '\'' +
                ", descendants=" + descendants +
                '}';
    }
}
