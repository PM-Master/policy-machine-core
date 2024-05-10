package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.op.pattern.ReferencedPolicyEntities;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.PatternValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class PMLPattern extends Pattern {

    public abstract static class Simple extends PMLPattern {
        public Simple(String varName, List<Value> argValues, FunctionSignature functionSignature) throws PMException {
            super(varName, argValues, functionSignature);
        }

        @Override
        public ReferencedPolicyEntities getReferencedPolicyEntities() {
            ReferencedPolicyEntities entities = new ReferencedPolicyEntities(false);
            entities.addEntities(getEntityFromValue(getArgValues().get(0)));

            return entities;
        }
    }
    public abstract static class Aggregatge extends PMLPattern {
        private final List<PatternValue> patternValues;

        public Aggregatge(String varName, List<Value> argValues, FunctionSignature functionSignature) throws PMException {
            super(varName, argValues, functionSignature);

            Value argValue = argValues.getFirst();
            if (!argValue.getType().equals(Type.array(Type.pattern()))) {
                throw new PMException("aggregate pattern functions expect an array of patterns");
            }

            List<Value> arrayValue = argValue.getArrayValue();
            List<PatternValue> patternValues = new ArrayList<>();
            for (Value v : arrayValue) {
                if (!(v instanceof PatternValue patternValue)) {
                    throw new PMException("expected a Pattern, received " + argValue.getType());
                }

                patternValues.add(patternValue);
            }

            this.patternValues = patternValues;
        }

        public List<PatternValue> getPatternValues() {
            return patternValues;
        }

        @Override
        public ReferencedPolicyEntities getReferencedPolicyEntities() {
            return new ReferencedPolicyEntities(false);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            Aggregatge that = (Aggregatge) o;
            return Objects.equals(patternValues, that.patternValues);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), patternValues);
        }
    }

    private final String varName;
    private final List<Value> argValues;

    public PMLPattern(String varName, List<Value> argValues, FunctionSignature functionSignature) throws PMException {
        this.varName = varName;
        this.argValues = argValues;

        int expected = functionSignature.getArgs().size();
        int actual = argValues.size();
        if (expected != actual) {
            throw new PMException("expected " + expected + " args, received " + actual);
        }
    }

    @Override
    public abstract boolean matches(Object value, PAP pap) throws PMException;

    @Override
    public abstract ReferencedPolicyEntities getReferencedPolicyEntities();

    public String getVarName() {
        return varName;
    }

    public List<Value> getArgValues() {
        return argValues;
    }

    public List<String> getEntityFromValue(Value value) {
        if (value.getType().isString()) {
            return Collections.singletonList(value.getStringValue());
        }

        // only option is an array
        List<Value> arrayValue = value.getArrayValue();
        List<String> entities = new ArrayList<>();
        for (Value v : arrayValue) {
            entities.addAll(getEntityFromValue(v));
        }

        return entities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PMLPattern that = (PMLPattern) o;
        return Objects.equals(varName, that.varName) && Objects.equals(argValues, that.argValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(varName, argValues);
    }
}
