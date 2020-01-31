package gov.nist.csd.pm.pip.graph.dag.searcher;

import gov.nist.csd.pm.exceptions.PMException;
import gov.nist.csd.pm.pip.graph.Graph;
import gov.nist.csd.pm.pip.graph.dag.propagator.Propagator;
import gov.nist.csd.pm.pip.graph.dag.visitor.Visitor;
import gov.nist.csd.pm.pip.graph.model.nodes.Node;

import java.util.HashSet;

public class DepthFirstSearcher implements Searcher{

    private Graph graph;
    private HashSet<Node> visited;

    public DepthFirstSearcher(Graph graph) {
        this.graph = graph;
        this.visited = new HashSet<>();
    }

    @Override
    public void traverse(Node start, Propagator propagator, Visitor visitor) throws PMException {
        if(visited.contains(start)) {
            return;
        }

        // mark the node as visited
        visited.add(start);

        for(Long parent : graph.getParents(start.getID())) {
            Node parentNode = graph.getNode(parent);

            // traverse from the parent node
            traverse(parentNode, propagator, visitor);

            // propagate from the parent to the start node
            propagator.propagate(parentNode, start);
        }

        // after processing the parents, visit the start node
        visitor.visit(start);
    }
}
