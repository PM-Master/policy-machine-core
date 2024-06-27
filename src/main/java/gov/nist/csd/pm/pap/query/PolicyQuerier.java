package gov.nist.csd.pm.pap.query;

public abstract class PolicyQuerier implements PolicyQuery{
    @Override
    public abstract AccessQuerier access();

    @Override
    public abstract GraphQuerier graph();

    @Override
    public abstract ProhibitionsQuerier prohibitions();

    @Override
    public abstract ObligationsQuerier obligations();
}
