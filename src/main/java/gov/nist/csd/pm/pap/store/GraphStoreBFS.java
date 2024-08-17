package gov.nist.csd.pm.pap.store;

import gov.nist.csd.pm.pap.exception.PMException;
import gov.nist.csd.pm.pap.graph.dag.BreadthFirstGraphWalker;
import gov.nist.csd.pm.pap.graph.dag.Direction;

import java.util.Collection;

public class GraphStoreBFS extends BreadthFirstGraphWalker {

    private GraphStore graphStore;

    public GraphStoreBFS(GraphStore graphStore) {
        super(null);
        this.graphStore = graphStore;
    }

    @Override
    protected Collection<String> getNextLevel(String node) throws PMException {
        if (getDirection() == Direction.DESCENDANTS) {
            return graphStore.getAdjacentDescendants(node);
        } else {
            return graphStore.getAdjacentAscendants(node);
        }
    }
}