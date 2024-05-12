package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.*;

public class Search extends FunctionDefinitionStatement {
    public Search() {
        super(new FunctionDefinitionStatement.Builder("search")
                      .returns(Type.array(Type.string()))
                      .args(
                              new FormalArgument("type", Type.string()),
                              new FormalArgument("properties", Type.map(Type.string(), Type.string()))
                      )
                      .executor((ctx, pap) -> {
                          NodeType nodeType = NodeType.toNodeType(ctx.scope().getVariable("type").getStringValue());

                          Map<Value, Value> propertiesValue = ctx.scope().getVariable("properties").getMapValue();

                          Map<String, String> properties = new HashMap<>();
                          for (Map.Entry<Value, Value> prop : propertiesValue.entrySet()) {
                              properties.put(prop.getKey().getStringValue(), prop.getValue().getStringValue());
                          }

                          Collection<String> search = pap.query().graph().search(nodeType, properties);

                          List<Value> ret = new ArrayList<>(search.size());
                          for (String s : search) {
                              ret.add(new StringValue(s));
                          }

                          return new ArrayValue(ret, Type.array(Type.string()));
                      })
                      .build()
        );
    }
}
