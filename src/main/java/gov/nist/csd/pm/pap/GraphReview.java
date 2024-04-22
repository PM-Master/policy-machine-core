package gov.nist.csd.pm.pap;

import gov.nist.csd.pm.common.exception.PMException;

import java.util.List;

public interface GraphReview {

    List<String> getAttributeContainers(String node) throws PMException;
    List<String> getPolicyClassContainers(String node) throws PMException;
    boolean isContained(String subject, String container) throws PMException;

}
