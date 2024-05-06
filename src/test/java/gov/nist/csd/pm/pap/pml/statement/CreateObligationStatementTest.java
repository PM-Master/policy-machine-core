package gov.nist.csd.pm.pap.pml.statement;

class CreateObligationStatementTest {

    /* TODO @Test
    void testSuccess() throws PMException {
        CreateObligationStatement stmt = new CreateObligationStatement(new StringLiteral("o1"), List.of(
                new CreateRuleStatement(
                        new StringLiteral("rule1"),
                        new CreateRuleStatement.SubjectClause(CreateRuleStatement.SubjectType.ANY_USER, null),
                        new CreateRuleStatement.OperationClause(buildArrayLiteral("e1", "e2")),
                        new CreateRuleStatement.OperandsClause(
                                buildArrayLiteral("oa1", "oa2"), CreateRuleStatement.TargetType.ANY_IN_UNION),
                        new CreateRuleStatement.ResponseBlock("evtCtx", List.of(
                                new CreatePolicyStatement(new StringLiteral("pc2"))
                        ))
                )
        ));

        MemoryPolicyStore store = new MemoryPolicyStore();
        store.graph().createPolicyClass("pc1", new HashMap<>());
        store.graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc1"));
        store.graph().createUser("u2", new HashMap<>(), List.of("ua2"));
        store.graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
        store.graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
        ExecutionContext execCtx = new ExecutionContext(new UserContext("u2"), GlobalScope.forExecute(store));

        stmt.execute(execCtx, store);

        assertTrue(store.obligations().exists("o1"));

        Obligation actual = store.obligations().get("o1");
        assertEquals(1, actual.getRules().size());
        assertEquals("u2", actual.getAuthor().getUser());
        Rule rule = actual.getRules().get(0);
        assertEquals("rule1", rule.getName());
        assertEquals(new EventPattern(
                new AnyUserSubject(),
                new Performs("e1", "e2"),
                new AnyInUnionTarget("oa1", "oa2")
        ), rule.getEventPattern());
    }

    @Test
    void testToFormattedString() {
        CreateObligationStatement stmt = new CreateObligationStatement(
                new StringLiteral("obl1"),
                List.of(
                        new CreateRuleStatement(
                                new StringLiteral("rule1"),
                                new CreateRuleStatement.SubjectClause(CreateRuleStatement.SubjectType.ANY_USER, null),
                                new CreateRuleStatement.OperationClause(buildArrayLiteral("e1", "e2")),
                                new CreateRuleStatement.OperandsClause(
                                        buildArrayLiteral("oa1", "oa2"), CreateRuleStatement.TargetType.ANY_IN_UNION),
                                new CreateRuleStatement.ResponseBlock("evtCtx", List.of(
                                        new CreatePolicyStatement(new StringLiteral("pc2"))
                                ))
                        ),
                        new CreateRuleStatement(
                                new StringLiteral("rule2"),
                                new CreateRuleStatement.SubjectClause(CreateRuleStatement.SubjectType.USERS, buildArrayLiteral("u1")),
                                new CreateRuleStatement.OperationClause(buildArrayLiteral("e3")),
                                new CreateRuleStatement.OperandsClause(
                                        buildArrayLiteral("oa1", "oa2"), CreateRuleStatement.TargetType.ANY_IN_UNION),
                                new CreateRuleStatement.ResponseBlock("evtCtx", List.of(
                                        new CreatePolicyStatement(new StringLiteral("pc3"))
                                ))
                        )
                )

        );
        assertEquals(
                """
                        create obligation "obl1" {
                            create rule "rule1"
                            when any user
                            performs ["e1", "e2"]
                            on union of ["oa1", "oa2"]
                            do (evtCtx) {        
                                create PC "pc2"
                            }
                            create rule "rule2"
                            when users ["u1"]
                            performs ["e3"]
                            on union of ["oa1", "oa2"]
                            do (evtCtx) {        
                                create PC "pc3"
                            }
                        }""",
                stmt.toFormattedString(0)
        );
        assertEquals(
                """
                            create obligation "obl1" {
                                create rule "rule1"
                                when any user
                                performs ["e1", "e2"]
                                on union of ["oa1", "oa2"]
                                do (evtCtx) {        
                                    create PC "pc2"
                                }
                                create rule "rule2"
                                when users ["u1"]
                                performs ["e3"]
                                on union of ["oa1", "oa2"]
                                do (evtCtx) {        
                                    create PC "pc3"
                                }
                            }""",
                stmt.toFormattedString(0)
        );
    }*/

}