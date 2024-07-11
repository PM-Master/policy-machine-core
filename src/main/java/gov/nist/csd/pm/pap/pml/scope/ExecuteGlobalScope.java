package gov.nist.csd.pm.pap.pml.scope;

import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.pml.PMLBuiltinFunctions;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.HashMap;
import java.util.Map;

import static gov.nist.csd.pm.pap.admin.AdminPolicyNode.*;
import static gov.nist.csd.pm.pap.admin.AdminPolicyNode.OBLIGATIONS_TARGET;

public class ExecuteGlobalScope extends GlobalScope<Value> {

    public ExecuteGlobalScope() {
        // buitin variables
        Map<String, Value> builtinConstants = new HashMap<>();
        for (String ar : AdminAccessRights.allAdminAccessRights()) {
            builtinConstants.put(ar, new StringValue(ar));
        }

        builtinConstants.put(ADMIN_POLICY.constantName(), new StringValue(ADMIN_POLICY.nodeName()));
        builtinConstants.put(POLICY_CLASS_TARGETS.constantName(), new StringValue(POLICY_CLASS_TARGETS.nodeName()));
        builtinConstants.put(ADMIN_POLICY_TARGET.constantName(), new StringValue(ADMIN_POLICY_TARGET.nodeName()));
        builtinConstants.put(PML_FUNCTIONS_TARGET.constantName(), new StringValue(PML_FUNCTIONS_TARGET.nodeName()));
        builtinConstants.put(PML_CONSTANTS_TARGET.constantName(), new StringValue(PML_CONSTANTS_TARGET.nodeName()));
        builtinConstants.put(PROHIBITIONS_TARGET.constantName(), new StringValue(PROHIBITIONS_TARGET.nodeName()));
        builtinConstants.put(OBLIGATIONS_TARGET.constantName(), new StringValue(OBLIGATIONS_TARGET.nodeName()));

        withConstants(builtinConstants);

        // add builtin functions
        Map<String, PMLFunction<?>> funcs = PMLBuiltinFunctions.builtinFunctions();
        for (Map.Entry<String, PMLFunction<?>> entry : funcs.entrySet()) {
            addFunction(entry.getKey(), entry.getValue());
        }
    }
}
