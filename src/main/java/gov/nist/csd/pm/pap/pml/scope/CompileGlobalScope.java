package gov.nist.csd.pm.pap.pml.scope;

import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.pml.PMLBuiltinFunctions;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.HashMap;
import java.util.Map;

import static gov.nist.csd.pm.pap.admin.AdminPolicyNode.*;
import static gov.nist.csd.pm.pap.admin.AdminPolicyNode.OBLIGATIONS_TARGET;

public class CompileGlobalScope extends GlobalScope<Variable, FunctionSignature> {

    public CompileGlobalScope() {
        // buitin variables
        Map<String, Variable> builtinConstants = new HashMap<>();
        for (String ar : AdminAccessRights.allAdminAccessRights()) {
            builtinConstants.put(ar, new Variable(ar, Type.string(), true));
        }

        // admin policy nodes constants
        builtinConstants.put(ADMIN_POLICY.constantName(), new Variable(ADMIN_POLICY.constantName(), Type.string(), true));
        builtinConstants.put(POLICY_CLASS_TARGETS.constantName(), new Variable(POLICY_CLASS_TARGETS.constantName(), Type.string(), true));
        builtinConstants.put(ADMIN_POLICY_TARGET.constantName(), new Variable(ADMIN_POLICY_TARGET.constantName(), Type.string(), true));
        builtinConstants.put(PML_FUNCTIONS_TARGET.constantName(), new Variable(PML_FUNCTIONS_TARGET.constantName(), Type.string(), true));
        builtinConstants.put(PML_CONSTANTS_TARGET.constantName(), new Variable(PML_CONSTANTS_TARGET.constantName(), Type.string(), true));
        builtinConstants.put(PROHIBITIONS_TARGET.constantName(), new Variable(PROHIBITIONS_TARGET.constantName(), Type.string(), true));
        builtinConstants.put(OBLIGATIONS_TARGET.constantName(), new Variable(OBLIGATIONS_TARGET.constantName(), Type.string(), true));

        // add built in functions
        Map<String, FunctionSignature> builtinFunctions = new HashMap<>();
        Map<String, FunctionDefinitionStatement> functions = PMLBuiltinFunctions.builtinFunctions();
        for (Map.Entry<String, FunctionDefinitionStatement> e : functions.entrySet()) {
            builtinFunctions.put(e.getKey(), e.getValue().getSignature());
        }

        setBuiltinConstants(builtinConstants);
        setBuiltinFunctions(builtinFunctions);
    }
}
