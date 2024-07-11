package gov.nist.csd.pm.pap.pml.function.builtin;

import gov.nist.csd.pm.pap.admin.AdminPolicy;
import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.statement.operation.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.StringValue;

public class PcTargetName extends FunctionDefinitionStatement {

    private static final String PC_ARG = "pc";

    public PcTargetName() {
        super(new FunctionDefinitionStatement.Builder("pcTargetName")
                .returns(Type.string())
                .args(
                        new FormalArg(PC_ARG, Type.string(), reqCap)
                )
                .executor((ctx, query) -> {
                    return new StringValue(AdminPolicy.policyClassTargetName(ctx.scope().getVariable(PC_ARG).getStringValue()));
                })
                .build()
        );
    }
}