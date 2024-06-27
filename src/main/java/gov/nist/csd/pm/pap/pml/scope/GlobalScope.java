package gov.nist.csd.pm.pap.pml.scope;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.pml.PMLBuiltinFunctions;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static gov.nist.csd.pm.pap.admin.AdminPolicyNode.*;
import static gov.nist.csd.pm.pap.admin.AdminPolicyNode.OBLIGATIONS_TARGET;

public abstract class GlobalScope<V, F> implements Serializable {

    private Map<String, V> builtinConstants;
    private Map<String, F> builtinFunctions;
    private Map<String, V> providedConstants;
    private Map<String, F> providedFunctions;

    protected GlobalScope() {
        builtinConstants = new HashMap<>();
        builtinFunctions = new HashMap<>();
        providedConstants = new HashMap<>();
        providedFunctions = new HashMap<>();
    }

    public GlobalScope(Map<String, V> builtinConstants, Map<String, F> builtinFunctions) {
        this.builtinConstants = builtinConstants;
        this.builtinFunctions = builtinFunctions;
    }

    public GlobalScope<V, F> withProvidedFunctions(Map<String, F> persistedFunctions) {
        this.providedFunctions = persistedFunctions;
        return this;
    }

    public GlobalScope<V, F> withProvidedFunction(String funcName, F f) {
        this.providedFunctions.put(funcName, f);
        return this;
    }

    public GlobalScope<V, F> withProvidedConstants(Map<String, V> persistedConstants) {
        this.providedConstants = persistedConstants;
        return this;
    }

    public GlobalScope<V, F> withProvidedConstant(String constName, V v) {
        this.providedConstants.put(constName, v);
        return this;
    }

    public V getConstant(String varName) {
        if (builtinConstants.containsKey(varName)) {
            return builtinConstants.get(varName);
        } else if (providedConstants.containsKey(varName)) {
            return providedConstants.get(varName);
        }

        return null;
    }

    public F getFunction(String funcName) {
        if (builtinFunctions.containsKey(funcName)) {
            return builtinFunctions.get(funcName);
        } else if (providedFunctions.containsKey(funcName)) {
            return providedFunctions.get(funcName);
        }

        return null;
    }

    public Map<String, V> getBuiltinConstants() {
        return builtinConstants;
    }

    public void setBuiltinConstants(Map<String, V> builtinConstants) {
        this.builtinConstants = builtinConstants;
    }

    public Map<String, F> getBuiltinFunctions() {
        return builtinFunctions;
    }

    public void setBuiltinFunctions(Map<String, F> builtinFunctions) {
        this.builtinFunctions = builtinFunctions;
    }

    public Map<String, V> getProvidedConstants() {
        return providedConstants;
    }

    public void setProvidedConstants(Map<String, V> providedConstants) {
        this.providedConstants = providedConstants;
    }

    public Map<String, F> getProvidedFunctions() {
        return providedFunctions;
    }

    public void setProvidedFunctions(Map<String, F> providedFunctions) {
        this.providedFunctions = providedFunctions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GlobalScope<?, ?> that = (GlobalScope<?, ?>) o;
        return Objects.equals(builtinConstants, that.builtinConstants) && Objects.equals(
                builtinFunctions,
                that.builtinFunctions
        ) && Objects.equals(providedConstants, that.providedConstants) && Objects.equals(
                providedFunctions,
                that.providedFunctions
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(builtinConstants, builtinFunctions, providedConstants, providedFunctions);
    }
}
