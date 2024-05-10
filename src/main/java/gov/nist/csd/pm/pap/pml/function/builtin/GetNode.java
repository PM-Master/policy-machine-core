package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;


public class GetNode extends FunctionDefinitionStatement {

    private static final String NODE_ARG = "nodeName";

    public GetNode() {
        super(new FunctionDefinitionStatement.Builder("getNode")
                      .returns(Type.map(Type.string(), Type.any()))
                      .args(
                              new FormalArgument(NODE_ARG, Type.string())
                      )
                      .executor((ctx, pap) -> {
                          Node node = pap.query().graph().getNode(ctx.scope().getVariable(NODE_ARG).getStringValue());

                          return Value.fromObject(node);
                      })
                      .build()
        );
    }
}
