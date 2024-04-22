package gov.nist.csd.pm.pap;

public interface PolicyReview {

    AccessReview access();
    GraphReview graph();
    ProhibitionsReview prohibitions();
    ObligationsReview obligations();

}
