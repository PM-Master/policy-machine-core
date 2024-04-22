package gov.nist.csd.pm.impl.neo4j.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.Obligations;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.exception.*;
import org.apache.commons.lang3.SerializationUtils;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gov.nist.csd.pm.impl.neo4j.pap.Neo4JGraph.NAME_PROPERTY;

public class Neo4JObligations implements Obligations {

    public static final Label OBLIGATION_LABEL = Label.label("Obligation");

    public static final String BYTES_PROPERTY = "bytes";

    private Neo4jConnection neo4j;

    public Neo4JObligations(Neo4jConnection neo4j) {
        this.neo4j = neo4j;
    }

    @Override
    public void create(UserContext author, String name, Rule... rules)
            throws ObligationNameExistsException, NodeDoesNotExistException, PMBackendException {
        try {
            neo4j.runTx(tx -> {
                checkCreateInput(new Neo4JGraph(neo4j), author, name, rules);

                Node node = tx.createNode(OBLIGATION_LABEL);
                node.setProperty(NAME_PROPERTY, name);
                node.setProperty(
                        BYTES_PROPERTY,
                        SerializationUtils.serialize(new Obligation(author, name, Arrays.asList(rules)))
                );
            });
        } catch (ObligationNameExistsException | NodeDoesNotExistException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public void update(UserContext author, String name, Rule... rules)
            throws ObligationDoesNotExistException, ObligationRuleNameExistsException, NodeDoesNotExistException,
                   PMBackendException {
        try {
            neo4j.runTx(tx -> {
                checkUpdateInput(new Neo4JGraph(neo4j), author, name, rules);

                Node node = tx.findNode(OBLIGATION_LABEL, NAME_PROPERTY, name);
                if (node == null) {
                    create(author, name, rules);
                } else {
                    node.setProperty(NAME_PROPERTY, name);
                    node.setProperty(
                            BYTES_PROPERTY,
                            SerializationUtils.serialize(new Obligation(author, name, Arrays.asList(rules)))
                    );
                }
            });
        } catch (ObligationDoesNotExistException | ObligationRuleNameExistsException | NodeDoesNotExistException |
                PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public void delete(String name) throws PMBackendException {
        try {
            neo4j.runTx(tx -> {
                Node node = tx.findNode(OBLIGATION_LABEL, NAME_PROPERTY, name);
                if (node == null) {
                    return;
                }

                node.delete();
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public List<Obligation> getAll() throws PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                List<Obligation> obligations = new ArrayList<>();
                try(ResourceIterator<Node> nodes = tx.findNodes(OBLIGATION_LABEL)) {
                    while (nodes.hasNext()) {
                        Node next = nodes.next();
                        byte[] bytes = (byte[]) next.getProperty(BYTES_PROPERTY);
                        obligations.add(SerializationUtils.deserialize(bytes));
                    }
                }

                return obligations;
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public boolean exists(String name) throws PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                return tx.findNode(OBLIGATION_LABEL, NAME_PROPERTY, name) != null;
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public Obligation get(String name) throws ObligationDoesNotExistException, PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                Node node = tx.findNode(OBLIGATION_LABEL, NAME_PROPERTY, name);
                if (node == null) {
                    throw new ObligationDoesNotExistException(name);
                }

                byte[] bytes = (byte[]) node.getProperty(BYTES_PROPERTY);
                return SerializationUtils.deserialize(bytes);
            });
        } catch (ObligationDoesNotExistException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }
}
