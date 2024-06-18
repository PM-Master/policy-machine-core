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

import java.util.Collection;
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
        Collection<String> aNodes = a.graph().search(NodeType.ANY, NO_PROPERTIES);
        Collection<String> bNodes = b.graph().search(NodeType.ANY, NO_PROPERTIES);
        assertTrue(aNodes.containsAll(bNodes));
        assertTrue(bNodes.containsAll(aNodes));

        for (String nodeName : aNodes) {
            Node aNode = a.graph().getNode(nodeName);
            Node bNode = b.graph().getNode(nodeName);
            assertEquals(aNode, bNode);

            Collection<String> aChildren = a.graph().getChildren(nodeName);
            Collection<String> aParents = a.graph().getParents(nodeName);

            Collection<String> bChildren = b.graph().getChildren(nodeName);
            Collection<String> bParents = b.graph().getParents(nodeName);

            assertTrue(aChildren.containsAll(bChildren), nodeName + ": " + aChildren + " != " + bChildren);
            assertTrue(bChildren.containsAll(aChildren), nodeName + ": " + bChildren + " != " + aChildren);
            assertTrue(aParents.containsAll(bParents), nodeName + ": " + aParents + " != " + bParents);
            assertTrue(bParents.containsAll(aParents), nodeName + ": " + bParents + " != " + aParents);

            Collection<Association> aSourceAssocs = a.graph().getAssociationsWithSource(nodeName);
            Collection<Association> aTargetAssocs = a.graph().getAssociationsWithTarget(nodeName);

            Collection<Association> bSourceAssocs = b.graph().getAssociationsWithSource(nodeName);
            Collection<Association> bTargetAssocs = b.graph().getAssociationsWithTarget(nodeName);

            assertTrue(aSourceAssocs.containsAll(bSourceAssocs), aSourceAssocs + " != " + bSourceAssocs);
            assertTrue(bSourceAssocs.containsAll(aSourceAssocs), bSourceAssocs + " != " + aSourceAssocs);
            assertTrue(aTargetAssocs.containsAll(bTargetAssocs), aTargetAssocs + " != " + bTargetAssocs);
            assertTrue(bTargetAssocs.containsAll(aTargetAssocs), bTargetAssocs + " != " + aTargetAssocs);
        }

        // check prohibitions
        Map<String, Collection<Prohibition>> aProhibitions = a.prohibitions().getAll();
        Map<String, Collection<Prohibition>> bProhibitions = b.prohibitions().getAll();

        assertTrue(aProhibitions.keySet().containsAll(bProhibitions.keySet()));
        assertTrue(aProhibitions.values().containsAll(bProhibitions.values()));

        // check obligations
        Collection<Obligation> aObligations = a.obligations().getAll();
        Collection<Obligation> bObligations = b.obligations().getAll();

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
