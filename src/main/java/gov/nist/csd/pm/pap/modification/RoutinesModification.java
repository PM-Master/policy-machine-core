package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.routine.Routine;

import java.util.Collection;

public interface RoutinesModification {

    void create(Routine routine) throws PMException;
    void delete(String name) throws PMException;
    Routine get(String name) throws PMException;
    Collection<Routine> getAll() throws PMException;

}
