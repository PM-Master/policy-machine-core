package gov.nist.csd.pm.pap.op;

import java.util.Map;
import java.util.Objects;

public abstract class Operation<T> {

    private String name;
    private Map<String, RequiredCapability> capMap;
    protected OperationPrivilegeChecker checker;
    protected OperationExecutor<T> executor;

    public Operation(String name, Map<String, RequiredCapability> capMap, OperationExecutor<T> executor) {
        this.name = name;
        this.capMap = capMap;
        this.checker = new DefaultPrivilegeChecker();
        this.executor = executor;
    }

    public Operation(String name, Map<String, RequiredCapability> capMap, OperationPrivilegeChecker checker, OperationExecutor<T> executor) {
        this.name = name;
        this.capMap = capMap;
        this.checker = checker;
        this.executor = executor;
    }

    public String getName() {
        return name;
    }

    public Map<String, RequiredCapability> getCapMap() {
        return capMap;
    }

    public OperationPrivilegeChecker getChecker() {
        return checker;
    }

    public OperationExecutor<T> getExecutor() {
        return executor;
    }

    public PreparedOperation<T> withOperands(Map<String, Object> operands) {
        return new PreparedOperation<>(this, operands);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Operation<?> operation = (Operation<?>) o;
        return Objects.equals(name, operation.name) && Objects.equals(
                capMap,
                operation.capMap
        ) && Objects.equals(checker, operation.checker) && Objects.equals(executor, operation.executor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, capMap, checker, executor);
    }

    @Override
    public String toString() {
        return "Operation{" +
                "name='" + name + '\'' +
                ", capMap=" + capMap +
                ", checker=" + checker +
                ", executor=" + executor +
                '}';
    }
}
