package gov.nist.csd.pm.impl.memory.pap.unmodifiable;

import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.obligation.EventPattern;

import java.util.Collections;
import java.util.List;

public class UnmodifiableObligation extends Obligation {

    public UnmodifiableObligation(UserContext author, String name,
                                  List<Rule> rules) {
        super(author, name, Collections.unmodifiableList(rules));
    }

    @Override
    public Obligation addRule(String name, EventPattern eventPattern, Response response) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteRule(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAuthor(UserContext userCtx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRules(List<Rule> rules) {
        throw new UnsupportedOperationException();
    }
}
