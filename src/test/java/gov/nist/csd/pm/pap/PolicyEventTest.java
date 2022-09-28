package gov.nist.csd.pm.pap;

import gov.nist.csd.pm.pap.memory.MemoryPAP;
import gov.nist.csd.pm.policy.model.access.AccessRightSet;
import gov.nist.csd.pm.policy.model.access.UserContext;
import gov.nist.csd.pm.policy.events.PolicyEvent;
import gov.nist.csd.pm.policy.exceptions.PMException;
import gov.nist.csd.pm.policy.model.obligation.Response;
import gov.nist.csd.pm.policy.model.obligation.Rule;
import gov.nist.csd.pm.policy.model.obligation.event.EventPattern;
import gov.nist.csd.pm.policy.model.obligation.event.EventSubject;
import gov.nist.csd.pm.policy.model.obligation.event.Performs;
import gov.nist.csd.pm.policy.model.prohibition.ContainerCondition;
import gov.nist.csd.pm.policy.model.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.policy.author.pal.model.expression.Literal;
import gov.nist.csd.pm.policy.author.pal.statement.CreatePolicyStatement;
import gov.nist.csd.pm.policy.author.pal.statement.Expression;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.SuperPolicy.SUPER_USER;
import static gov.nist.csd.pm.policy.model.graph.nodes.Properties.noprops;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PolicyEventTest {

    @Test
    void testEvents() throws PMException {
        PAP pap = new MemoryPAP();

        List<PolicyEvent> events = new ArrayList<>();
        pap.addEventListener(events::add, false);

        pap.graph().setResourceAccessRights(new AccessRightSet("read"));
        pap.graph().createPolicyClass("pc1", noprops());
        pap.graph().createObjectAttribute("oa1", noprops(), "pc1");
        pap.graph().createUserAttribute("ua1", noprops(), "pc1");
        pap.graph().createUserAttribute("ua2", noprops(), "pc1");
        pap.graph().createObject("o1", noprops(), "oa1");
        pap.graph().createUser("u1", noprops(), "ua1");
        pap.graph().createUser("u2", noprops(), "ua1");
        pap.graph().setNodeProperties("u1", Map.of("k", "v"));
        pap.graph().deleteNode("u1");
        pap.graph().assign("u2", "ua2");
        pap.graph().deassign("u2", "ua2");
        pap.graph().associate("ua1", "oa1", new AccessRightSet());
        pap.graph().dissociate("ua1", "oa1");
        pap.prohibitions().create("label", ProhibitionSubject.user("ua1"), new AccessRightSet("read"), false, new ContainerCondition("oa1", false));
        pap.prohibitions().update("label", ProhibitionSubject.user("ua2"), new AccessRightSet("read"), false, new ContainerCondition("oa1", false));
        pap.prohibitions().delete("label");
        pap.obligations().create(
                new UserContext(SUPER_USER),
                "label",
                new Rule(
                        "rule1",
                        new EventPattern(
                                EventSubject.anyUser(),
                                new Performs("test_event")
                        ),
                        new Response(
                                new UserContext(SUPER_USER),
                                new CreatePolicyStatement(new Expression(new Literal("test_pc")))
                        )
                )
        );
        pap.obligations().update(new UserContext(SUPER_USER),
                "label",
                new Rule(
                        "rule1",
                        new EventPattern(
                                EventSubject.anyUser(),
                                new Performs("test_event")
                        ),
                        new Response(
                                new UserContext(SUPER_USER),
                                new CreatePolicyStatement(new Expression(new Literal("test_pc2")))
                        )
                ));
        pap.obligations().delete("label");

        assertEquals(28, events.size());
    }

}
