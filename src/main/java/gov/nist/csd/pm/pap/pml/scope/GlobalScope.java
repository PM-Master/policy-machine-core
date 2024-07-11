package gov.nist.csd.pm.pap.pml.scope;

import gov.nist.csd.pm.pap.pml.pattern.PMLPatternFunction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class GlobalScope<V> implements Serializable {

    private Map<String, V> constants;
    private Map<String, PMLFunction<?>> functions;
    private Map<String, PMLPatternFunction> patternFunctions;

    protected GlobalScope() {
        constants = new HashMap<>();
        functions = new HashMap<>();
        patternFunctions = buildPatternFunctions();
    }

    public GlobalScope(Map<String, V> constants, Map<String, PMLFunction<?>> functions) {
        this.constants = constants;
        this.functions = functions;
    }

    public GlobalScope<V> withFunctions(Map<String, PMLFunction<?>> functions) {
        this.functions = functions;
        return this;
    }

    public GlobalScope<V> withConstants(Map<String, V> constants) {
        this.constants = constants;
        return this;
    }

    public void addConstant(String key, V value) {
        this.constants.put(key, value);
    }

    public void addFunction(String name, PMLFunction<?> operation) {
        this.functions.put(name, operation);
    }

    public V getConstant(String varName) {
        return constants.get(varName);
    }

    public PMLFunction<?> getFunction(String funcName) {
        return functions.get(funcName);
    }

    public Map<String, PMLFunction<?>> getFunctions() {
        return functions;
    }

    public Map<String, V> getConstants() {
        return constants;
    }

    public void addFunctions(Map<String, PMLFunction<?>> funcs) {
        functions.putAll(funcs);
    }

    public void addConstants(Map<String, V> c) {
        constants.putAll(c);
    }

/* TODO -- do I still need this?
    private Map<String, PMLPatternFunction> buildPatternFunctions() {
        new

        return Map.of();
    }
*/


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GlobalScope<?> that = (GlobalScope<?>) o;
        return Objects.equals(constants, that.constants) && Objects.equals(functions, that.functions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(constants, functions);
    }
}
