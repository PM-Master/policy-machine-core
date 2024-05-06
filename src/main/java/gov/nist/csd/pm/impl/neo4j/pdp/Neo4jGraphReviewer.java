package gov.nist.csd.pm.impl.neo4j.pdp;

import gov.nist.csd.pm.impl.neo4j.pap.Neo4JGraphModification;
import gov.nist.csd.pm.impl.neo4j.pap.Neo4jConnection;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.GraphQuery;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.Uniqueness;

import java.util.ArrayList;
import java.util.List;

import static gov.nist.csd.pm.impl.neo4j.pap.Neo4JGraphModification.*;

public class Neo4jGraphReviewer implements GraphQuery {

    private final Neo4jConnection neo4j;
    private final Neo4JGraphModification neo4jGraphStore;

    public Neo4jGraphReviewer(GraphDatabaseService graph) {
        this.neo4j = new Neo4jConnection(graph);
        this.neo4jGraphStore = new Neo4JGraphModification(neo4j);
    }

    @Override
    public List<String> getAttributeContainers(String node) throws PMException {
        return neo4j.runTx(tx -> {
            Node neoNode = tx.findNode(NODE_LABEL, NAME_PROPERTY, node);
            Traverser traverser = tx.traversalDescription()
                                    .breadthFirst()
                                    .relationships(ASSIGNMENT_RELATIONSHIP_TYPE, Direction.OUTGOING)
                                    .uniqueness(Uniqueness.NONE)
                                    .traverse(neoNode);
            Iterable<Node> nodes = traverser.nodes();

            List<String> conts = new ArrayList<>();
            for (Node n : nodes) {
                if (n.hasLabel(OA_LABEL) || n.hasLabel(UA_LABEL)) {
                    conts.add(String.valueOf(n.getProperty(NAME_PROPERTY)));
                }
            }

            return conts;
        });
    }

    @Override
    public List<String> getPolicyClassContainers(String node) throws PMException {
        return neo4j.runTx(tx -> {
            Node neoNode = tx.findNode(NODE_LABEL, NAME_PROPERTY, node);
            Traverser traverser = tx.traversalDescription()
                                    .breadthFirst()
                                    .relationships(ASSIGNMENT_RELATIONSHIP_TYPE, Direction.OUTGOING)
                                    .uniqueness(Uniqueness.NONE)
                                    .traverse(neoNode);
            Iterable<Node> nodes = traverser.nodes();

            List<String> conts = new ArrayList<>();
            for (Node n : nodes) {
                if (n.hasLabel(PC_LABEL)) {
                    conts.add(String.valueOf(n.getProperty(NAME_PROPERTY)));
                }
            }

            return conts;
        });
    }

    @Override
    public boolean isContained(String subject, String container) throws PMException {
        return neo4j.runTx(tx -> {
            Node subjectNode = tx.findNode(NODE_LABEL, NAME_PROPERTY, subject);

            Traverser traverse = tx.traversalDescription()
                                   .breadthFirst()
                                   .relationships(ASSIGNMENT_RELATIONSHIP_TYPE, Direction.OUTGOING)
                                   .uniqueness(Uniqueness.NONE)
                                   .evaluator(path -> {
                                       Relationship last = path.lastRelationship();
                                       if (last == null) {
                                           return Evaluation.EXCLUDE_AND_CONTINUE;
                                       } else if (last.getEndNode().getProperty(NAME_PROPERTY).equals(container)) {
                                           return Evaluation.INCLUDE_AND_PRUNE;
                                       }

                                       return Evaluation.EXCLUDE_AND_CONTINUE;
                                   })
                                   .traverse(subjectNode);

            return traverse.iterator().hasNext();
        });
    }
}
