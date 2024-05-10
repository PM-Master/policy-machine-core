package gov.nist.csd.pm.util;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.query.PolicyQuery;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.common.graph.node.Properties.NO_PROPERTIES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PolicyEquals {

    public static void assertPolicyEquals(PolicyQuery a, PolicyQuery b) throws PMException {
        // check nodes
        // assignments
        // associations
        List<String> aNodes = a.graph().search(NodeType.ANY, NO_PROPERTIES);
        List<String> bNodes = b.graph().search(NodeType.ANY, NO_PROPERTIES);
        assertTrue(aNodes.containsAll(bNodes));
        assertTrue(bNodes.containsAll(aNodes));

        for (String nodeName : aNodes) {
            Node aNode = a.graph().getNode(nodeName);
            Node bNode = b.graph().getNode(nodeName);
            assertEquals(aNode, bNode);

            List<String> aChildren = a.graph().getChildren(nodeName);
            List<String> aParents = a.graph().getParents(nodeName);

            List<String> bChildren = b.graph().getChildren(nodeName);
            List<String> bParents = b.graph().getParents(nodeName);

            assertTrue(aChildren.containsAll(bChildren));
            assertTrue(bChildren.containsAll(aChildren));
            assertTrue(aParents.containsAll(bParents));
            assertTrue(bParents.containsAll(aParents));

            List<Association> aSourceAssocs = a.graph().getAssociationsWithSource(nodeName);
            List<Association> aTargetAssocs = a.graph().getAssociationsWithTarget(nodeName);

            List<Association> bSourceAssocs = b.graph().getAssociationsWithSource(nodeName);
            List<Association> bTargetAssocs = b.graph().getAssociationsWithTarget(nodeName);

            assertTrue(aSourceAssocs.containsAll(bSourceAssocs));
            assertTrue(bSourceAssocs.containsAll(aSourceAssocs));
            assertTrue(aTargetAssocs.containsAll(bTargetAssocs));
            assertTrue(bTargetAssocs.containsAll(aTargetAssocs));
        }

        // check prohibitions
        Map<String, List<Prohibition>> aProhibitions = a.prohibitions().getAll();
        Map<String, List<Prohibition>> bProhibitions = b.prohibitions().getAll();

        assertTrue(aProhibitions.keySet().containsAll(bProhibitions.keySet()));
        assertTrue(aProhibitions.values().containsAll(bProhibitions.values()));

        // check obligations
        List<Obligation> aObligations = a.obligations().getAll();
        List<Obligation> bObligations = b.obligations().getAll();

        assertTrue(aObligations.containsAll(bObligations));
        assertTrue(bObligations.containsAll(aObligations));

        // check user defined pml
        Map<String, Value> aConstants = a.pml().getConstants();
        Map<String, Value> bConstants = b.pml().getConstants();
        assertEquals(aConstants, bConstants);

        Map<String, FunctionDefinitionStatement> aFunctions = a.pml().getFunctions();
        Map<String, FunctionDefinitionStatement> bFunctions = b.pml().getFunctions();
        assertEquals(aFunctions, bFunctions);
    }

}
