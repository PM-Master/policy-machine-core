package gov.nist.csd.pm.pap.pml.scope;

import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.type.Type;

import java.util.HashMap;
import java.util.Map;

import static gov.nist.csd.pm.pap.admin.AdminPolicyNode.*;
import static gov.nist.csd.pm.pap.admin.AdminPolicyNode.OBLIGATIONS_TARGET;
import static gov.nist.csd.pm.pap.pml.PMLBuiltinFunctions.builtinFunctions;

public class CompileGlobalScope extends GlobalScope<Variable> {

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
        withConstants(builtinConstants);

        Map<String, PMLFunction> funcs = builtinFunctions();
        for (Map.Entry<String, PMLFunction> func : funcs.entrySet()) {
            addFunction(func.getKey(), func.getValue());
        }
    }
}
