package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.expression.reference.ReferenceByID;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.util.PolicyEquals;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.common.graph.node.NodeType.OA;
import static gov.nist.csd.pm.common.graph.node.NodeType.UA;
import static gov.nist.csd.pm.pap.pml.PMLUtil.buildArrayLiteral;
import static gov.nist.csd.pm.pap.pml.PMLUtil.buildMapLiteral;
import static org.junit.jupiter.api.Assertions.*;

class CreatePolicyModificationStatementTest {

    @Test
    void testSuccess() throws PMException {
        CreatePolicyStatement stmt = new CreatePolicyStatement(new StringLiteral("pc1"));
        MemoryPAP pap = new MemoryPAP();
        pap.modify().graph().createPolicyClass("pc2", new HashMap<>());
        pap.modify().graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc2"));
        pap.modify().graph().createUser("u2", new HashMap<>(), List.of("ua2"));
        ExecutionContext execCtx = new ExecutionContext(new UserContext("u2"), GlobalScope.forExecute(pap));

        stmt.execute(execCtx, pap);

        assertTrue(pap.query().graph().nodeExists("pc1"));
    }

    @Test
    void testToFormattedString() {
        CreatePolicyStatement stmt = new CreatePolicyStatement(
                new StringLiteral("pc1")
        );
        assertEquals(
                "create PC \"pc1\"",
                stmt.toFormattedString(0)
        );
        assertEquals(
                "    create PC \"pc1\"",
                stmt.toFormattedString(1)
        );
    }

    CreatePolicyStatement stmt = new CreatePolicyStatement(
            new StringLiteral("test"),
            buildMapLiteral("a", "b"),
            new ArrayList<>(List.of(
                    new CreatePolicyStatement.CreateOrAssignAttributeStatement(
                            new ReferenceByID("ua1"), UA, new StringLiteral("test"), buildMapLiteral("k", "v")),
                    new CreatePolicyStatement.CreateOrAssignAttributeStatement(
                            new StringLiteral("ua1-1"), UA, new ReferenceByID("ua1")),
                    new CreatePolicyStatement.CreateOrAssignAttributeStatement(
                            new StringLiteral("ua1-2"), UA, new ReferenceByID("ua1")),
                    new CreatePolicyStatement.CreateOrAssignAttributeStatement(
                            new StringLiteral("ua1-2-1"), UA, new StringLiteral("ua1-2"), buildMapLiteral("k", "v")),
                    new CreatePolicyStatement.CreateOrAssignAttributeStatement(
                            new StringLiteral("ua1-3"), UA, new ReferenceByID("ua1")),
                    new CreatePolicyStatement.CreateOrAssignAttributeStatement(
                            new StringLiteral("ua2"), UA, new StringLiteral("test"), buildMapLiteral("k", "v")),
                    new CreatePolicyStatement.CreateOrAssignAttributeStatement(
                            new StringLiteral("ua1-2-1"), UA, new StringLiteral("ua2"))
            )),
            new ArrayList<>(List.of(
                    new CreatePolicyStatement.CreateOrAssignAttributeStatement(
                            new StringLiteral("oa1"), OA, new StringLiteral("test"), buildMapLiteral("k", "v")),
                    new CreatePolicyStatement.CreateOrAssignAttributeStatement(
                            new StringLiteral("oa1-1"), OA, new StringLiteral("oa1")),
                    new CreatePolicyStatement.CreateOrAssignAttributeStatement(
                            new StringLiteral("oa1-2"), OA, new StringLiteral("oa1")),
                    new CreatePolicyStatement.CreateOrAssignAttributeStatement(
                            new StringLiteral("oa1-2-1"), OA, new StringLiteral("oa1-2"), buildMapLiteral("k", "v")),
                    new CreatePolicyStatement.CreateOrAssignAttributeStatement(
                            new StringLiteral("oa1-3"), OA, new StringLiteral("oa1")),
                    new CreatePolicyStatement.CreateOrAssignAttributeStatement(
                            new StringLiteral("oa2"), OA, new StringLiteral("test"), buildMapLiteral("k", "v")),
                    new CreatePolicyStatement.CreateOrAssignAttributeStatement(
                            new StringLiteral("oa1-2-1"), OA, new StringLiteral("oa2"))
            )),
            new ArrayList<>(List.of(
                    new AssociateStatement(
                            new StringLiteral("ua1"), new StringLiteral("oa1"), buildArrayLiteral("read", "write"))
            ))
    );

    @Test
    void testHierarchy() throws PMException {
        MemoryPAP pap = new MemoryPAP();

        ExecutionContext execCtx = new ExecutionContext(new UserContext("u2"), GlobalScope.forExecute(pap));
        execCtx.scope().addVariable("ua1", new StringValue("ua1"));

        pap.modify().graph().setResourceAccessRights(new AccessRightSet("read", "write"));
        stmt.execute(execCtx, pap);

        MemoryPAP expected = new MemoryPAP();
        expected.modify().graph().setResourceAccessRights(new AccessRightSet("read", "write"));
        expected.modify().graph().createPolicyClass("test", Map.of("a", "b"));
        expected.modify().graph().createUserAttribute("ua1", Map.of("k", "v"), List.of("test"));
        expected.modify().graph().createUserAttribute("ua1-1", new HashMap<>(), List.of("ua1"));
        expected.modify().graph().createUserAttribute("ua1-2", new HashMap<>(), List.of("ua1"));
        expected.modify().graph().createUserAttribute("ua1-2-1", Map.of("k", "v"), List.of("ua1-2"));
        expected.modify().graph().createUserAttribute("ua1-3", new HashMap<>(), List.of("ua1"));
        expected.modify().graph().createUserAttribute("ua2", Map.of("k", "v"), List.of("test"));
        expected.modify().graph().assign("ua1-2-1", "ua2");

        expected.modify().graph().createObjectAttribute("oa1", Map.of("k", "v"), List.of("test"));
        expected.modify().graph().createObjectAttribute("oa1-1", new HashMap<>(), List.of("oa1"));
        expected.modify().graph().createObjectAttribute("oa1-2", new HashMap<>(), List.of("oa1"));
        expected.modify().graph().createObjectAttribute("oa1-2-1", Map.of("k", "v"), List.of("oa1-2"));
        expected.modify().graph().createObjectAttribute("oa1-3", new HashMap<>(), List.of("oa1"));
        expected.modify().graph().createObjectAttribute("oa2", Map.of("k", "v"), List.of("test"));
        expected.modify().graph().assign("oa1-2-1", "oa2");

        expected.modify().graph().associate("ua1", "oa1", new AccessRightSet("read", "write"));

        PolicyEquals.assertPolicyEquals(expected.query(), pap.query());

        String s = stmt.toString();
        pap = new MemoryPAP();
        pap.modify().graph().setResourceAccessRights(new AccessRightSet("read", "write"));
        pap.modify().pml().createConstant("ua1", new StringValue("ua1"));
        PMLExecutor.compileAndExecutePML(pap, new UserContext("u2"), s);
        pap.modify().pml().deleteConstant("ua1");

        PolicyEquals.assertPolicyEquals(expected.query(), pap.query());
    }

    @Test
    void testFormattedString() {
        String s = stmt.toFormattedString(0);
        assertEquals("""
                             create PC "test" with properties {"a": "b"} {
                                 user attributes {
                                     ua1 {"k": "v"}
                                         "ua1-1"
                                         "ua1-2"
                                             "ua1-2-1" {"k": "v"}
                                         "ua1-3"
                                     "ua2" {"k": "v"}
                                         "ua1-2-1"
                                 }
                                 object attributes {
                                     "oa1" {"k": "v"}
                                         "oa1-1"
                                         "oa1-2"
                                             "oa1-2-1" {"k": "v"}
                                         "oa1-3"
                                     "oa2" {"k": "v"}
                                         "oa1-2-1"
                                 }
                                 associations {
                                     "ua1" and "oa1" with ["read", "write"]
                                 }
                             }""", s);
    }

    @Test
    void testFormattedStringWithIndent() {
        String s = stmt.toFormattedString(2);
        assertEquals("""
                             create PC "test" with properties {"a": "b"} {
                                 user attributes {
                                     ua1 {"k": "v"}
                                         "ua1-1"
                                         "ua1-2"
                                             "ua1-2-1" {"k": "v"}
                                         "ua1-3"
                                     "ua2" {"k": "v"}
                                         "ua1-2-1"
                                 }
                                 object attributes {
                                     "oa1" {"k": "v"}
                                         "oa1-1"
                                         "oa1-2"
                                             "oa1-2-1" {"k": "v"}
                                         "oa1-3"
                                     "oa2" {"k": "v"}
                                         "oa1-2-1"
                                 }
                                 associations {
                                     "ua1" and "oa1" with ["read", "write"]
                                 }
                             }
                     """.stripTrailing(), s);
    }

    @Test
    void testFormattedStringWithNoHierarchy() {
        CreatePolicyStatement s = new CreatePolicyStatement(new StringLiteral("a"));
        assertEquals("create PC \"a\"", s.toFormattedString(0));

        s = new CreatePolicyStatement(new StringLiteral("a"), buildMapLiteral("a", "b"));
        assertEquals("create PC \"a\" with properties {\"a\": \"b\"}", s.toFormattedString(0));

        s = new CreatePolicyStatement(new StringLiteral("a"));
        assertEquals("    create PC \"a\"", s.toFormattedString(1));

        s = new CreatePolicyStatement(new StringLiteral("a"), buildMapLiteral("a", "b"));
        assertEquals("    create PC \"a\" with properties {\"a\": \"b\"}", s.toFormattedString(1));
    }
}