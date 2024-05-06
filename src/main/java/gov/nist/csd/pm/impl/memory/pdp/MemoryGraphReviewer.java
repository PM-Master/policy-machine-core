package gov.nist.csd.pm.impl.memory.pdp;

import gov.nist.csd.pm.common.graph.dag.DepthFirstGraphWalker;
import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.pap.exception.NodeDoesNotExistException;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.dag.Direction;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.pap.query.GraphQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static gov.nist.csd.pm.common.graph.node.NodeType.*;

public class MemoryGraphReviewer implements GraphQuery {

    private final PolicyModification policyModification;

    public MemoryGraphReviewer(PolicyModification policyModification) {
        this.policyModification = policyModification;
    }

    @Override
    public List<String> getAttributeContainers(String node) throws PMException {
        List<String> attrs = new ArrayList<>();

        new DepthFirstGraphWalker(policyModification.graph())
                .withDirection(Direction.PARENTS)
                .withVisitor((n) -> {
                    Node visitedNode;
                    visitedNode = policyModification.graph().getNode(n);
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

        new DepthFirstGraphWalker(policyModification.graph())
                .withDirection(Direction.PARENTS)
                .withVisitor((n) -> {
                    Node visitedNode;
                    visitedNode = policyModification.graph().getNode(n);
                    if (visitedNode.getType().equals(PC)) {
                        attrs.add(n);
                    }
                })
                .walk(node);

        return attrs;
    }

    @Override
    public boolean isContained(String subject, String container) throws PMException {
        if (!policyModification.graph().nodeExists(subject)) {
            throw new NodeDoesNotExistException(subject);
        } else if (!policyModification.graph().nodeExists(container)){
            throw new NodeDoesNotExistException(container);
        }

        AtomicBoolean found = new AtomicBoolean(false);

        new DepthFirstGraphWalker(policyModification.graph())
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
