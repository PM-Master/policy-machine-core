package gov.nist.csd.pm.pap.pml.context;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatementSerializer;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ExecutionContext implements Serializable {

    private final UserContext author;
    private final Scope<Value> scope;
    private PMLExecutor executor;

    public ExecutionContext(UserContext author, Scope<Value> scope) {
        this.author = author;
        this.scope = scope;
        this.executor = new PMLExecutor();
    }

    public ExecutionContext(UserContext author, GlobalScope<Value> globalScope) {
        this.author = author;
        this.scope = new Scope<>(globalScope);
        this.executor = new PMLExecutor();
    }

    public UserContext author() {
        return author;
    }

    public Scope<Value> scope() {
        return scope;
    }

    public ExecutionContext copy() {
        return new ExecutionContext(this.author, this.scope.copy());
    }

    public ExecutionContext withPMLExecutor(PMLExecutor executor) {
        this.executor = executor;

        return this;
    }

    public Value executeStatement(PAP pap, PMLStatement stmt) throws PMException {
        return executor.executeStatement(this, pap, stmt);
    }

    public Value executeStatements(PAP pap, List<PMLStatement> statements) throws PMException {
        return executor.executeStatements(this, pap, statements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExecutionContext that)) {
            return false;
        }
        return Objects.equals(author, that.author) && Objects.equals(
                scope,
                that.scope
        ) && Objects.equals(executor, that.executor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, scope);
    }
}
