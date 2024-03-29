package gov.nist.csd.pm.policy.pml.functions;

import gov.nist.csd.pm.policy.pml.model.expression.Type;
import gov.nist.csd.pm.policy.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.policy.pml.model.expression.Value;
import gov.nist.csd.pm.policy.pml.model.function.FormalArgument;
import gov.nist.csd.pm.policy.model.graph.nodes.Node;

public class HasPropertyKey extends FunctionDefinitionStatement {

    public HasPropertyKey() {
        super(
                name("hasPropertyKey"),
                returns(Type.bool()),
                args(
                        new FormalArgument("nodeName", Type.string()),
                        new FormalArgument("key", Type.string())
                ),
                (ctx, author) -> {
                    String nodeName = ctx.scope().getValue("nodeName").getStringValue();
                    String key = ctx.scope().getValue("key").getStringValue();
                    Node node = author.graph().getNode(nodeName);
                    boolean hasPropertyKey = node.getProperties().containsKey(key);
                    return new Value(hasPropertyKey);
                }
        );
    }

}
