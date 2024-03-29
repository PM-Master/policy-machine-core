package gov.nist.csd.pm.policy.pml.functions;

import gov.nist.csd.pm.policy.pml.model.expression.Type;
import gov.nist.csd.pm.policy.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.policy.pml.model.expression.Value;
import gov.nist.csd.pm.policy.pml.model.function.FormalArgument;
import gov.nist.csd.pm.policy.model.graph.nodes.NodeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Search extends FunctionDefinitionStatement {
    public Search() {
        super(
                name("search"),
                returns(Type.array(Type.string())),
                args(
                        new FormalArgument("type", Type.string()),
                        new FormalArgument("properties", Type.map(Type.string(), Type.string()))
                ),
                (ctx, author) -> {
                    NodeType nodeType = NodeType.toNodeType(ctx.scope().getValue("type").getStringValue());

                    Map<Value, Value> propertiesValue = ctx.scope().getValue("properties").getMapValue();

                    Map<String, String> properties = new HashMap<>();
                    for (Map.Entry<Value, Value> prop : propertiesValue.entrySet()) {
                        properties.put(prop.getKey().getStringValue(), prop.getValue().getStringValue());
                    }

                    List<String> search = author.graph().search(nodeType, properties);

                    List<Value> ret = new ArrayList<>(search.size());
                    for (String s : search) {
                        ret.add(new Value(s));
                    }

                    return new Value(ret);
                }
        );
    }
}
