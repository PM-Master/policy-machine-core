package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.exception.ObligationDoesNotExistException;
import gov.nist.csd.pm.pap.query.UserContext;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class ObligationsModifierTest extends ModificationTest {


    /*Obligation obligation1 = new Obligation(
            new UserContext("u1"),
            "obl1",
            List.of(
                    new Rule(
                            "rule1",
                            new EventPattern(
                                    new AnyUserSubject(),
                                    new Performs("test_event")
                            ),
                            new Response("evtCtx", List.of(
                                    new CreatePolicyStatement(new StringLiteral("test_pc"))
                            ))
                    )
            )
    );

    Obligation obligation2 = new Obligation(
            new UserContext("u1"),
            "label2")
            .addRule(
                    new Rule(
                            "rule1",
                            new EventPattern(
                                    new AnyUserSubject(),
                                    new Performs("test_event")
                            ),
                            new Response("evtCtx", List.of(
                                    new CreatePolicyStatement(new StringLiteral("test_pc"))
                            ))
                    )
            ).addRule(
                    new Rule(
                            "rule2",
                            new EventPattern(
                                    new AnyUserSubject(),
                                    new Performs("test_event")
                            ),
                            new Response("evtCtx", List.of(
                                    new CreatePolicyStatement(new StringLiteral("test_pc"))
                            ))
                    )
            );


    @Nested
    class CreateObligation {

        @Test
        void testObligationNameExistsException() throws PMException {
            pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
            pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

            pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));

            assertThrows(ObligationNameExistsException.class, () -> pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new)));
        }

        @Test
        void testAuthorNodeDoestNotExistException() {
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.policy().obligations().create(new UserContext("u1"), obligation1.getName(),
                            obligation1.getRules().toArray(Rule[]::new)));
        }

        @Test
        void testEventSubjectNodeDoesNotExistException() throws PMException {
            pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
            pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.policy().obligations().create(
                            new UserContext("u1"),
                            "obl1",
                            new Rule(
                                    "rule1",
                                    new EventPattern(
                                            new UsersSubject("ua2"),
                                            Performs.events("test_event"),
                                            new AnyTarget()
                                    ),
                                    new Response("evtCtx", List.of())
                            )
                    ));
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.policy().obligations().create(
                            new UserContext("u1"),
                            "obl1",
                            new Rule(
                                    "rule1",
                                    new EventPattern(
                                            new UserAttributesSubject("ua3"),
                                            Performs.events("test_event"),
                                            new AnyTarget()
                                    ),
                                    new Response("evtCtx", List.of())
                            )
                    ));
        }

        @Test
        void testEventTargetNodeDoesNotExistException() throws PMException {
            pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
            pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.policy().obligations().create(
                            new UserContext("u1"),
                            "obl1",
                            new Rule(
                                    "rule1",
                                    new EventPattern(
                                            new UsersSubject("u1"),
                                            Performs.events("test_event"),
                                            new OnTargets("oa1")
                                    ),
                                    new Response("evtCtx", List.of())
                            )
                    ));
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.policy().obligations().create(
                            new UserContext("u1"),
                            "obl1",
                            new Rule(
                                    "rule1",
                                    new EventPattern(
                                            new UsersSubject("u1"),
                                            Performs.events("test_event"),
                                            new OnTargets("oa1")
                                    ),
                                    new Response("evtCtx", List.of())
                            )
                    ));
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.policy().obligations().create(
                            new UserContext("u1"),
                            "obl1",
                            new Rule(
                                    "rule1",
                                    new EventPattern(
                                            new UsersSubject("u1"),
                                            Performs.events("test_event"),
                                            new AnyInUnionTarget("oa1")
                                    ),
                                    new Response("evtCtx", List.of())
                            )
                    ));
        }

        @Test
        void testSuccess() throws PMException {
            pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
            pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

            pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));

            assertThrows(ObligationNameExistsException.class,
                    () -> pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName()));

            Obligation actual = pap.policy().obligations().get(obligation1.getName());
            assertEquals(obligation1, actual);
        }
    }

    @Nested
    class UpdateObligation {

        @Test
        void testObligationDoesNotExistException() {
            assertThrows(ObligationDoesNotExistException.class, () -> pap.policy().obligations().update(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new)));
        }

        @Test
        void testAuthorNodeDoesNotExistException() throws PMException {
            pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
            pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

            pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));

            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.policy().obligations().update(new UserContext("u2"), obligation1.getName(),
                            obligation1.getRules().toArray(Rule[]::new)));
        }

        @Test
        void testEventSubjectNodeDoesNotExistException() throws PMException {
            pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
            pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

            pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));

            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.policy().obligations().update(
                            new UserContext("u1"),
                            obligation1.getName(),
                            new Rule(
                                    "rule1",
                                    new EventPattern(
                                            new UsersSubject("ua2"),
                                            Performs.events("test_event"),
                                            new AnyTarget()
                                    ),
                                    new Response("evtCtx", List.of())
                            )
                    ));
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.policy().obligations().update(
                            new UserContext("u1"),
                            obligation1.getName(),
                            new Rule(
                                    "rule1",
                                    new EventPattern(
                                            new UserAttributesSubject("ua2"),
                                            Performs.events("test_event"),
                                            new AnyTarget()
                                    ),
                                    new Response("evtCtx", List.of())
                            )
                    ));
        }

        @Test
        void testEventTargetNodeDoesNotExistException() throws PMException {
            pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
            pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

            pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));

            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.policy().obligations().update(
                            new UserContext("u1"),
                            obligation1.getName(),
                            new Rule(
                                    "rule1",
                                    new EventPattern(
                                            new UsersSubject("u1"),
                                            Performs.events("test_event"),
                                            new OnTargets("oa1")
                                    ),
                                    new Response("evtCtx", List.of())
                            )
                    ));
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.policy().obligations().update(
                            new UserContext("u1"),
                            obligation1.getName(),
                            new Rule(
                                    "rule1",
                                    new EventPattern(
                                            new UsersSubject("u1"),
                                            Performs.events("test_event"),
                                            new OnTargets("oa1")
                                    ),
                                    new Response("evtCtx", List.of())
                            )
                    ));
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.policy().obligations().update(
                            new UserContext("u1"),
                            obligation1.getName(),
                            new Rule(
                                    "rule1",
                                    new EventPattern(
                                            new UsersSubject("u1"),
                                            Performs.events("test_event"),
                                            new AnyInUnionTarget("oa1")
                                    ),
                                    new Response("evtCtx", List.of())
                            )
                    ));
        }

        @Test
        void testSuccess() throws PMException {
            pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
            pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

            assertThrows(ObligationDoesNotExistException.class,
                    () -> pap.policy().obligations().update(new UserContext("u1"), obligation1.getName()));

            pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));

            pap.policy().obligations().update(new UserContext("u1"), obligation1.getName(),
                    obligation2.getRules().toArray(Rule[]::new));

            Obligation expected = new Obligation(obligation1);
            expected.setRules(obligation2.getRules());

            Obligation actual = pap.policy().obligations().get(obligation1.getName());
            assertEquals(expected, actual);
        }

    }

    @Nested
    class DeleteNode {

        @Test
        void testDeleteNonExistingObligationDoesNOtThrowExcpetion() {
            assertDoesNotThrow(() -> pap.policy().obligations().delete(obligation1.getName()));
        }

        @Test
        void testDeleteObligation() throws PMException {
            pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
            pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

            pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));
            pap.policy().obligations().create(obligation2.getAuthor(), obligation2.getName(), obligation2.getRules().toArray(Rule[]::new));

            pap.policy().obligations().delete(obligation1.getName());

            assertThrows(ObligationDoesNotExistException.class,
                    () -> pap.policy().obligations().get(obligation1.getName()));
        }
    }


    @Nested
    class GetAll {
        @Test
        void testGetObligations() throws PMException {
            pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
            pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

            pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));
            pap.policy().obligations().create(obligation2.getAuthor(), obligation2.getName(), obligation2.getRules().toArray(
                    Rule[]::new));

            List<Obligation> obligations = pap.policy().obligations().getAll();
            assertEquals(2, obligations.size());
            for (Obligation obligation : obligations) {
                if (obligation.getName().equals(obligation1.getName())) {
                    assertEquals(obligation1, obligation);
                } else {
                    assertEquals(obligation2, obligation);
                }
            }
        }
    }


    @Nested
    class Get {

        @Test
        void testObligationDoesNotExistException() {
            assertThrows(
                    ObligationDoesNotExistException.class,
                    () -> pap.policy().obligations().get(obligation1.getName()));
        }

        @Test
        void testGetObligation() throws PMException {
            pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
            pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

            pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));
            pap.policy().obligations().create(obligation2.getAuthor(), obligation2.getName(), obligation2.getRules().toArray(Rule[]::new));

            Obligation obligation = pap.policy().obligations().get(obligation1.getName());
            assertEquals(obligation1, obligation);
        }
    }*/
}