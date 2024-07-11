package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.statement.operation.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.ProhibitionValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GetProhibitionsFor extends FunctionDefinitionStatement {

    private static final Type returnType = Type.array(Type.map(Type.string(), Type.any()));


    public GetProhibitionsFor() {
        super(new FunctionDefinitionStatement.Builder("getProhibitionsFor")
                .returns(returnType)
                .args(
                        new FormalArg("subject", Type.string(), reqCap)
                )
                .executor((ctx, query) -> {
                            String subject = ctx.scope().getVariable("subject").getStringValue();
                            Collection<Prohibition> prohibitions = query.prohibitions().getWithSubject(subject);
                            List<Value> prohibitionValues = new ArrayList<>(prohibitions.size());
                            for (Prohibition prohibition : prohibitions) {
                                prohibitionValues.add(new ProhibitionValue(prohibition).getValue());
                            }

                            return new ArrayValue(prohibitionValues, returnType);
                        }
                )
                .build()
        );
    }

}
