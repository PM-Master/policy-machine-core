package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.pap.query.ObligationsQuery;
import gov.nist.csd.pm.pap.query.UserContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class MemoryObligationsQuerierTest {

    private static ObligationsQuery querier;

    @BeforeAll
    static void setup() throws PMException {
        String pml = """
                create pc "pc1" {
                    uas {
                        "ua1"
                    }
                    oas {
                        "oa1"
                            "oa1-1"
                            "oa1-2"
                        "oa2"
                    }
                }
                
                create u "u1" assign to ["ua1"]
                create u "u2" assign to ["ua1"]
                
                create obligation "o1" {
                    create rule "r1"
                    when subject => pEquals("u1")
                    performs op => pEquals("assign_to")
                    do(ctx){
                        create policy class "test"
                    }
                }
                
                create obligation "o2" {
                    create rule "r2"
                    when (subject) => pAny()
                    performs op => pEquals("assign_to")
                    do(ctx){
                        create policy class "test"
                    }
                }
                """;
        PAP pap = new MemoryPAP();
        pap.deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        pml = """
              create obligation "o3" {
                    create rule "r3"
                    when (subject) => pAny()
                    performs op => pEquals("assign_to")
                    on 
                        opnd1 => pAny()
                        opnd2 => pEquals("oa2")
                    do(ctx) {
                        create policy class "test"
                    }
              }
              
              create obligation "o4" {
                    create rule "r4"
                    when subject => pEquals("u2")
                    performs op => pEquals("assign")
                    on 
                        opnd1 => pAny()
                        opnd2 => pEquals("oa1")
                    do(ctx) {}
              }
              """;
        pap.executePML(new UserContext("u2"), pml);

        querier = pap.query().obligations();
    }

    @Test
    void TestGetObligationsWithAuthor() throws PMException {
        Collection<Obligation> u1 = querier.getObligationsWithAuthor(new UserContext("u1"));
        assertEquals(2, u1.size());
        Collection<Obligation> u2 = querier.getObligationsWithAuthor(new UserContext("u2"));
        assertEquals(2, u2.size());
    }
}