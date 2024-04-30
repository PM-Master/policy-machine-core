package gov.nist.csd.pm.impl.memory.dag;

import gov.nist.csd.pm.impl.memory.pdp.MemoryPolicyReviewer;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.dag.Direction;
import gov.nist.csd.pm.common.graph.dag.DepthFirstGraphWalker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DepthFirstGraphWalkerTest {

    static PAP pap;

    @BeforeAll
    static void setup() throws PMException {
        MemoryPolicyStore ps = new MemoryPolicyStore();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);

        pap = new PAP(ps, pr);
        pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
        pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

        pap.policy().graph().createObjectAttribute("oa1-1", new HashMap<>(), List.of("oa1"));
        pap.policy().graph().createObjectAttribute("oa1-1-1", new HashMap<>(), List.of("oa1-1"));
        pap.policy().graph().createObjectAttribute("oa1-1-2", new HashMap<>(), List.of("oa1-1"));
        pap.policy().graph().createObjectAttribute("oa1-1-3", new HashMap<>(), List.of("oa1-1"));

        pap.policy().graph().createObjectAttribute("oa1-2", new HashMap<>(), List.of("oa1"));
        pap.policy().graph().createObjectAttribute("oa1-2-1", new HashMap<>(), List.of("oa1-2"));
        pap.policy().graph().createObjectAttribute("oa1-2-2", new HashMap<>(), List.of("oa1-2"));
        pap.policy().graph().createObjectAttribute("oa1-2-3", new HashMap<>(), List.of("oa1-2"));
    }

    @Test
    void testWalk() throws PMException {
        List<String> visited = new ArrayList<>();
        DepthFirstGraphWalker bfs = new DepthFirstGraphWalker(pap.policy().graph())
                .withDirection(Direction.CHILDREN)
                .withVisitor((node) -> {
                    visited.add(node);
                });
        bfs.walk("pc1");
        List<String> expected = List.of(
                "oa1-1-1", "oa1-1-2", "oa1-1-3", "oa1-1", "oa1-2-1", "oa1-2-2", "oa1-2-3", "oa1-2", "oa1", "pc1"
        );

        assertEquals(expected, visited);
    }

    @Test
    void testAllPathsShortCircuit() throws PMException {
        List<String> visited = new ArrayList<>();
        DepthFirstGraphWalker dfs = new DepthFirstGraphWalker(pap.policy().graph())
                .withDirection(Direction.CHILDREN)
                .withVisitor(node -> {
                    visited.add(node);
                })
                .withAllPathShortCircuit(node -> node.equals("oa1-2-1"));

        dfs.walk("pc1");

        List<String> expected = List.of("oa1-1-1", "oa1-1-2", "oa1-1-3", "oa1-1", "oa1-2-1", "oa1-2", "oa1", "pc1");
        assertEquals(expected, visited);
    }

    @Test
    void testSinglePathShortCircuit() throws PMException {
        List<String> visited = new ArrayList<>();
        DepthFirstGraphWalker dfs = new DepthFirstGraphWalker(pap.policy().graph())
                .withDirection(Direction.CHILDREN)
                .withVisitor(node -> {
                    visited.add(node);
                })
                .withSinglePathShortCircuit(node -> node.equals("oa1-1"));

        dfs.walk("pc1");

        List<String> expected = List.of("oa1-1", "oa1-2-1", "oa1-2-2", "oa1-2-3", "oa1-2", "oa1", "pc1");
        assertEquals(expected, visited);
    }
}