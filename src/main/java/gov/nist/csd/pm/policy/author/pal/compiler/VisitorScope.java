package gov.nist.csd.pm.policy.author.pal.compiler;

import gov.nist.csd.pm.policy.author.pal.compiler.error.ErrorLog;
import gov.nist.csd.pm.policy.author.pal.model.expression.Type;
import gov.nist.csd.pm.policy.author.pal.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.policy.model.access.AccessRightSet;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.*;

public class VisitorScope {

    // local functions and variables
    private final Map<String, FunctionDefinitionStatement> functions;
    private final Map<String, Variable> variables;
    private AccessRightSet resourceAccessRights;
    private final ErrorLog errorLog;


    public VisitorScope(ErrorLog errorLog) {
        this.functions = new HashMap<>();
        this.variables = new HashMap<>();
        this.resourceAccessRights = new AccessRightSet();
        this.errorLog = errorLog;
    }

    public Map<String, FunctionDefinitionStatement> getFunctions() {
        return functions;
    }

    public Map<String, Variable> getVariables() {
        return variables;
    }

    public void addFunction(ParserRuleContext ctx, FunctionDefinitionStatement functionDefinitionStmt) {
        if (this.functions.containsKey(functionDefinitionStmt.getFunctionName())) {
            this.errorLog.addError(
                    ctx,
                    "a function with the name " + functionDefinitionStmt.getFunctionName() + " already exists"
            );
            return;
        }
        this.functions.put(functionDefinitionStmt.getFunctionName(), functionDefinitionStmt);
    }

    public void addFunction(FunctionDefinitionStatement functionDefinitionStmt) {
        if (this.functions.containsKey(functionDefinitionStmt.getFunctionName())) {
            this.errorLog.addError(
                    -1, -1, -1,
                    "a builtin function with the name " + functionDefinitionStmt.getFunctionName() + " already exists"
            );
            return;
        }
        this.functions.put(functionDefinitionStmt.getFunctionName(), functionDefinitionStmt);
    }

    public void addVariable(String varName, Type type, boolean isConst) {
        this.variables.put(varName, new Variable(varName, type, isConst, false));
    }

    public void setResourceAccessRights(AccessRightSet resourceAccessRights) {
        this.resourceAccessRights = resourceAccessRights;
    }

    public boolean areResourceAccessRightsSet() {
        for (String varName : variables.keySet()) {
            Variable variable = variables.get(varName);
            if (variable.isAccessRight()) {
                return true;
            }
        }

        return false;
    }

    public AccessRightSet getResourceAccessRights() {
        AccessRightSet accessRightSet = new AccessRightSet();
        for (String varName : variables.keySet()) {
            Variable variable = variables.get(varName);
            if (variable.isAccessRight()) {
                accessRightSet.add(variable.name());
            }
        }

        return accessRightSet;
    }

    public FunctionDefinitionStatement getFunction(String name) {
        return functions.get(name);
    }

    public Variable getVariable(String name) {
        return variables.get(name);
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }

    /**
     * Update any existing variables to the values set in the passed VisitorScope.
     * Ignore variables that don't already exist in this.
     * @param visitorScope the VisitorScope to update this with
     */
    public void updateVariables(VisitorScope visitorScope) {
        for (String varName : visitorScope.variables.keySet()) {
            if (!this.variables.containsKey(varName)) {
                continue;
            }

            this.variables.put(varName, visitorScope.getVariable(varName));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitorScope scope = (VisitorScope) o;
        return Objects.equals(functions, scope.functions) && Objects.equals(variables, scope.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functions, variables);
    }
}
