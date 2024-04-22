package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.common.graph.relationships.Association;
import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.List;

public class GetAssociationsWithTarget extends FunctionDefinitionStatement {

    private static final Type returnType = Type.array(Type.map(Type.string(), Type.any()));

    public GetAssociationsWithTarget() {
        super(new FunctionDefinitionStatement.Builder("getAssociationsWithTarget")
                      .returns(returnType)
                      .args(
                              new FormalArgument("target", Type.string())
                      )
                      .executor((ctx, author) -> {
                          Value target = ctx.scope().getVariable("target");
                          List<Association> associations = author.graph().getAssociationsWithTarget(target.getStringValue());
                          List<Value> associationValues = new ArrayList<>(associations.size());
                          for (Association association : associations) {
                              associationValues.add(Value.fromObject(association));
                          }

                          return new ArrayValue(associationValues, returnType);
                      })
                      .build()
        );
    }

}
