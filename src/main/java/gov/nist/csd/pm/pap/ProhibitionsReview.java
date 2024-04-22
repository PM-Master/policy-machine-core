package gov.nist.csd.pm.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.prohibition.Prohibition;

import java.util.List;

public interface ProhibitionsReview {

    List<Prohibition> getInheritedProhibitionsFor(String subject) throws PMException;
    List<Prohibition> getProhibitionsWithContainer(String container) throws PMException;

}
