package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.query.ProhibitionsQuery;
import gov.nist.csd.pm.pap.query.UserContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MemoryProhibitionsQuerierTest {

    private static ProhibitionsQuery querier;

    @BeforeAll
    static void setup() throws PMException {
        String pml = """
                set resource access rights ["read"]
                create pc "pc1" {
                    uas {
                        "ua1"
                            "ua2"
                                "ua3"
                    }
                    oas {
                        "oa1"
                        "oa2"
                    }
                }
                
                create u "u1" assign to ["ua3"]
                
                create prohibition "p1"
                deny UA "ua1"
                access rights ["read"]
                on intersection of ["oa1", "oa2"]
                
                create prohibition "p2"
                deny U "u1"
                access rights ["read"]
                on intersection of [!"oa1", "oa2"]
                """;
        PAP pap = new MemoryPAP();
        pap.deserialize(new UserContext("u1"), List.of(pml), new PMLDeserializer());

        querier = pap.query().prohibitions();
    }

    @Test
    void testGetInheritedProhibitionsFor() throws PMException {
        Collection<Prohibition> prohibitions = querier.getInheritedProhibitionsFor("u1");
        assertEquals(2, prohibitions.size());
    }

    @Test
    void testGetProhibitionsWithContainer() throws PMException {
        Collection<Prohibition> prohibitions = querier.getProhibitionsWithContainer("oa1");
        assertEquals(2, prohibitions.size());
    }

}