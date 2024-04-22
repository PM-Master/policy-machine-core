package gov.nist.csd.pm.pap.pml.context;

import gov.nist.csd.pm.pap.exception.PMLFunctionNotDefinedException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.scope.UnknownVariableInScopeException;
import gov.nist.csd.pm.pap.pml.scope.UnknownFunctionInScopeException;
import gov.nist.csd.pm.pap.pml.scope.FunctionAlreadyDefinedInScopeException;
import gov.nist.csd.pm.pap.pml.scope.VariableAlreadyDefinedInScopeException;

import java.io.Serializable;
import java.util.Objects;

public class ExecutionContext implements Serializable {

    private final UserContext author;
    private final Scope<Value, FunctionDefinitionStatement> scope;

    public ExecutionContext(UserContext author, Scope<Value, FunctionDefinitionStatement> scope) {
        this.author = author;
        this.scope = scope;
    }

    public ExecutionContext(UserContext author, GlobalScope<Value, FunctionDefinitionStatement> globalScope) {
        this.author = author;
        this.scope = new Scope<>(globalScope);
    }

    public UserContext author() {
        return author;
    }

    public Scope<Value, FunctionDefinitionStatement> scope() {
        return scope;
    }

    public ExecutionContext copy() throws UnknownFunctionInScopeException, FunctionAlreadyDefinedInScopeException,
                                          UnknownVariableInScopeException, VariableAlreadyDefinedInScopeException,
                                          PMLFunctionNotDefinedException {
        return new ExecutionContext(this.author, this.scope.copy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExecutionContext that = (ExecutionContext) o;
        return Objects.equals(author, that.author) && Objects.equals(scope, that.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, scope);
    }
}
