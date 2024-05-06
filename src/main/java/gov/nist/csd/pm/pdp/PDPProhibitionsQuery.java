package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.pdp.adjudicator.AdjudicatorProhibitionsQuery;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.query.ProhibitionsQuery;

import java.util.List;

public class PDPProhibitionsQuery implements ProhibitionsQuery {

    private final AdjudicatorProhibitionsQuery adjudicator;
    private final ProhibitionsQuery prohibitionsQuery;

    public PDPProhibitionsQuery(AdjudicatorProhibitionsQuery adjudicator, ProhibitionsQuery prohibitionsQuery) {
        this.adjudicator = adjudicator;
        this.prohibitionsQuery = prohibitionsQuery;
    }

    @Override
    public List<Prohibition> getInheritedProhibitionsFor(String subject) throws PMException {
        adjudicator.getInheritedProhibitionsFor(subject);

        return prohibitionsQuery.getInheritedProhibitionsFor(subject);
    }

    @Override
    public List<Prohibition> getProhibitionsWithContainer(String container) throws PMException {
        adjudicator.getProhibitionsWithContainer(container);

        return prohibitionsQuery.getProhibitionsWithContainer(container);
    }
}
