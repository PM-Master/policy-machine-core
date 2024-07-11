package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.routine.Routine;

import java.util.Collection;
import java.util.List;

public class RoutinesModifier implements RoutinesModification{
    @Override
    public void create(Routine routine) throws PMException {

    }

    @Override
    public void delete(String name) throws PMException {

    }

    @Override
    public Routine get(String name) throws PMException {
        return null;
    }

    @Override
    public Collection<Routine> getAll() throws PMException {
        return List.of();
    }
}
