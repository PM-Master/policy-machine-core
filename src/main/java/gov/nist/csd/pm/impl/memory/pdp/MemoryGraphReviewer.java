package gov.nist.csd.pm.impl.memory.pdp;

import gov.nist.csd.pm.common.graph.dag.DepthFirstGraphWalker;
import gov.nist.csd.pm.pap.Policy;
import gov.nist.csd.pm.pap.exception.NodeDoesNotExistException;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.dag.Direction;
import gov.nist.csd.pm.common.graph.nodes.Node;
import gov.nist.csd.pm.pap.GraphReview;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static gov.nist.csd.pm.common.graph.nodes.NodeType.*;

public class MemoryGraphReviewer implements GraphReview {

    private final Policy policy;

    public MemoryGraphReviewer(Policy policy) {
        this.policy = policy;
    }

    @Override
    public List<String> getAttributeContainers(String node) throws PMException {
        List<String> attrs = new ArrayList<>();

        new DepthFirstGraphWalker(policy.graph())
                .withDirection(Direction.PARENTS)
                .withVisitor((n) -> {
                    Node visitedNode;
                    visitedNode = policy.graph().getNode(n);
                    if (visitedNode.getType().equals(UA) ||
                            visitedNode.getType().equals(OA)) {
                        attrs.add(n);
                    }
                })
                .walk(node);

        return attrs;
    }

    @Override
    public List<String> getPolicyClassContainers(String node) throws PMException {
        List<String> attrs = new ArrayList<>();

        new DepthFirstGraphWalker(policy.graph())
                .withDirection(Direction.PARENTS)
                .withVisitor((n) -> {
                    Node visitedNode;
                    visitedNode = policy.graph().getNode(n);
                    if (visitedNode.getType().equals(PC)) {
                        attrs.add(n);
                    }
                })
                .walk(node);

        return attrs;
    }

    @Override
    public boolean isContained(String subject, String container) throws PMException {
        if (!policy.graph().nodeExists(subject)) {
            throw new NodeDoesNotExistException(subject);
        } else if (!policy.graph().nodeExists(container)){
            throw new NodeDoesNotExistException(container);
        }

        AtomicBoolean found = new AtomicBoolean(false);

        new DepthFirstGraphWalker(policy.graph())
                .withDirection(Direction.PARENTS)
                .withVisitor((n) -> {
                    if (n.equals(container)) {
                        found.set(true);
                    }
                })
                .walk(subject);

        return found.get();
    }

}
