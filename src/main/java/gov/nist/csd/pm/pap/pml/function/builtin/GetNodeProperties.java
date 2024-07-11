package gov.nist.csd.pm.pap.pml.function.builtin;

import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.statement.operation.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.MapValue;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.HashMap;
import java.util.Map;

public class GetNodeProperties extends FunctionDefinitionStatement {

    private static final Type returnType = Type.map(Type.string(), Type.string());


    public GetNodeProperties() {
        super(new FunctionDefinitionStatement.Builder("getNodeProperties")
                      .returns(returnType)
                      .args(
                              new FormalArg("nodeName", Type.string(), reqCap)
                      )
                      .executor((ctx, query) -> {
                          Node node = query.graph().getNode(ctx.scope().getVariable("nodeName").getStringValue());
                          Map<String, String> properties = node.getProperties();
                          Map<Value, Value> propertiesValues = new HashMap<>();
                          for (Map.Entry<String, String> prop : properties.entrySet()) {
                              propertiesValues.put(new StringValue(prop.getKey()), new StringValue(properties.get(prop.getValue())));
                          }

                          return new MapValue(propertiesValues, Type.string(), Type.string());
                      })
                      .build()
        );
    }

}
