package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.statement.operation.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GetAssociationsWithTarget extends FunctionDefinitionStatement {

    private static final Type returnType = Type.array(Type.map(Type.string(), Type.any()));

    public GetAssociationsWithTarget() {
        super(new FunctionDefinitionStatement.Builder("getAssociationsWithTarget")
                      .returns(returnType)
                      .args(
                              new FormalArg("target", Type.string(), reqCap)
                      )
                      .executor((ctx, query) -> {
                          Value target = ctx.scope().getVariable("target");
                          Collection<Association> associations = query.graph().getAssociationsWithTarget(target.getStringValue());
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
