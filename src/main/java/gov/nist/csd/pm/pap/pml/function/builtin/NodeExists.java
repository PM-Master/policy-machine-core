package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.statement.operation.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.BoolValue;


public class NodeExists extends FunctionDefinitionStatement {

    private static final String NODE_ARG = "nodeName";

    public NodeExists() {
        super(new FunctionDefinitionStatement.Builder("nodeExists")
                      .returns(Type.bool())
                      .args(
                              new FormalArg(NODE_ARG, Type.string(), reqCap)
                      )
                      .executor((ctx, query) -> {
                          return new BoolValue(query.graph().nodeExists(ctx.scope().getVariable(NODE_ARG).getStringValue()));
                      })
                      .build()
        );
    }
}
