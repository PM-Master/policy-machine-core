package gov.nist.csd.pm.impl.neo4j.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.modification.ProhibitionsModification;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.exception.*;
import org.neo4j.graphdb.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.impl.neo4j.pap.Neo4JGraphModification.*;

public class Neo4JProhibitionsModification implements ProhibitionsModification {
    public static final Label PROHIBITION_LABEL = Label.label("Prohibition");
    public static final Label PROCESS_LABEL = Label.label("Process");

    public static final RelationshipType PROHIBITION_SUBJECT_REL_TYPE = RelationshipType.withName("prohibition_subject");
    public static final RelationshipType PROHIBITION_CONTAINER_REL_TYPE = RelationshipType.withName("prohibition_container");

    public static final String COMPLEMENT_PROPERTY = "complement";
    public static final String INTERSECTION_PROPERTY = "intersection";

    private Neo4jConnection neo4j;

    public Neo4JProhibitionsModification(Neo4jConnection neo4j) {
        this.neo4j = neo4j;
    }

    @Override
    public void create(String name, ProhibitionSubject subject, AccessRightSet accessRights, boolean intersection,
                       ContainerCondition... containerConditions)
            throws ProhibitionExistsException, ProhibitionSubjectDoesNotExistException,
            ProhibitionContainerDoesNotExistException, UnknownAccessRightException, PMBackendException {
        try {
            neo4j.runTx(tx -> {
                checkCreateInput(
                        new Neo4JGraphModification(neo4j),
                        name,
                        subject,
                        accessRights,
                        intersection,
                        containerConditions
                );

                // create prohibition node
                Node prohibitionNode = tx.createNode(PROHIBITION_LABEL);
                prohibitionNode.setProperty(NAME_PROPERTY, name);
                prohibitionNode.setProperty(ARSET_PROPERTY, accessRights.toArray(new String[]{}));

                doSubject(subject.getName(), prohibitionNode);

                prohibitionNode.setProperty(INTERSECTION_PROPERTY, intersection);

                doContainers(prohibitionNode, containerConditions);
            });
        } catch (ProhibitionExistsException | ProhibitionSubjectDoesNotExistException |
                ProhibitionContainerDoesNotExistException | UnknownAccessRightException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public void update(String name, ProhibitionSubject subject, AccessRightSet accessRights, boolean intersection,
                       ContainerCondition... containerConditions)
            throws ProhibitionDoesNotExistException, ProhibitionSubjectDoesNotExistException,
                   ProhibitionContainerDoesNotExistException, UnknownAccessRightException, PMBackendException {
        try {
            neo4j.runTx(tx -> {
                checkUpdateInput(new Neo4JGraphModification(neo4j), name, subject, accessRights, intersection, containerConditions);

                delete(name);

                try {
                    create(name, subject, accessRights, intersection, containerConditions);
                } catch (ProhibitionExistsException e) {
                    throw new PMBackendException(e);
                }
            });
        } catch (ProhibitionDoesNotExistException | ProhibitionSubjectDoesNotExistException |
                ProhibitionContainerDoesNotExistException | UnknownAccessRightException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }

    }

    @Override
    public void delete(String name) throws PMBackendException {
        try {
            neo4j.runTx(tx -> {
                if (!checkDeleteInput(name)) {
                    return;
                }

                Node node = tx.findNode(PROHIBITION_LABEL, NAME_PROPERTY, name);
                if (node == null) {
                    return;
                }

                Relationship subjectRel = node.getSingleRelationship(PROHIBITION_SUBJECT_REL_TYPE, Direction.INCOMING);
                Node subjectNode = subjectRel.getStartNode();
                // delete process node if this is the only prohibition it's assigned to
                if (subjectNode.hasLabel(PROCESS_LABEL)
                        && subjectNode.getRelationships().stream().count() == 1) {
                    subjectNode.delete();
                }

                // delete all rels
                for (Relationship relationship : node.getRelationships()) {
                    relationship.delete();
                }

                // delete prohibition node
                node.delete();
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public Map<String, List<Prohibition>> getAll() throws PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                Map<String, List<Prohibition>> all = new HashMap<>();
                try(ResourceIterator<Node> proNodes = tx.findNodes(PROHIBITION_LABEL)) {
                    while (proNodes.hasNext()) {
                        Node next = proNodes.next();
                        Prohibition p = getProhibitionFromNode(next);

                        List<Prohibition> subjPros = all.getOrDefault(p.getSubject().getName(), new ArrayList<>());
                        subjPros.add(p);
                        all.put(p.getSubject().getName(), subjPros);
                    }
                }

                return all;
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
                return tx.findNode(PROHIBITION_LABEL, NAME_PROPERTY, name) != null;
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public List<Prohibition> getWithSubject(String subject) throws PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                Node node = tx.findNode(NODE_LABEL, NAME_PROPERTY, subject);
                if (node == null) {
                    node = tx.findNode(PROCESS_LABEL, NAME_PROPERTY, subject);
                    if (node == null) {
                        return new ArrayList<>();
                    }
                }

                try(ResourceIterator<Relationship> proRels = node.getRelationships(Direction.OUTGOING, PROHIBITION_SUBJECT_REL_TYPE).iterator()) {
                    List<Prohibition> pros = new ArrayList<>();
                    while (proRels.hasNext()) {
                        pros.add(getProhibitionFromNode(proRels.next().getEndNode()));
                    }

                    return pros;
                }
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public Prohibition get(String name) throws ProhibitionDoesNotExistException, PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                Node node = tx.findNode(PROHIBITION_LABEL, NAME_PROPERTY, name);
                if (node == null) {
                    throw new ProhibitionDoesNotExistException(name);
                }

                return getProhibitionFromNode(node);
            });
        } catch (ProhibitionDoesNotExistException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    public static Prohibition getProhibitionFromNode(Node prohibitionNode) throws InvalidProhibitionSubjectException {
        String label = String.valueOf(prohibitionNode.getProperty(NAME_PROPERTY));

        // get subject
        Relationship subjectRel = prohibitionNode.getSingleRelationship(PROHIBITION_SUBJECT_REL_TYPE, Direction.INCOMING);
        Node subjectNode = subjectRel.getStartNode();
        String subject = String.valueOf(subjectNode.getProperty(NAME_PROPERTY));

        String subjectType = "PROCESS";
        if (subjectNode.hasLabel(UA_LABEL)) {
            subjectType = "USER_ATTRIBUTE";
        } else if (subjectNode.hasLabel(U_LABEL)) {
            subjectType = "USER";
        }

        AccessRightSet accessRights = new AccessRightSet((String[])prohibitionNode.getProperty(ARSET_PROPERTY));

        boolean intersection = (boolean)prohibitionNode.getProperty(INTERSECTION_PROPERTY);

        List<ContainerCondition> containerConditions = new ArrayList<>();
        try(ResourceIterator<Relationship> contRels = prohibitionNode.getRelationships(Direction.INCOMING, PROHIBITION_CONTAINER_REL_TYPE).iterator()) {
            while (contRels.hasNext()) {
                Relationship next = contRels.next();
                Node contNode = next.getStartNode();
                containerConditions.add(new ContainerCondition(
                        String.valueOf(contNode.getProperty(NAME_PROPERTY)),
                        Boolean.parseBoolean(String.valueOf(next.getProperty(COMPLEMENT_PROPERTY)))
                ));
            }
        }


        return new Prohibition(label, new ProhibitionSubject(subject, subjectType), accessRights, intersection, containerConditions);
    }

    private void doContainers(Node prohibitionNode, ContainerCondition ... containerConditions)
            throws PMBackendException {
        try {
            neo4j.runTx(tx -> {
                for (ContainerCondition cc : containerConditions) {
                    Node targetNode = tx.findNode(NODE_LABEL, NAME_PROPERTY, cc.getName());
                    targetNode.createRelationshipTo(prohibitionNode, PROHIBITION_CONTAINER_REL_TYPE)
                              .setProperty(COMPLEMENT_PROPERTY, cc.isComplement());
                }
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    private Node doSubject(String subject, Node prohibitionNode) throws PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                // look for a user or ua node with the subject name
                Node subjectNode = tx.findNode(U_LABEL, NAME_PROPERTY, subject);
                if (subjectNode == null) {
                    subjectNode = tx.findNode(UA_LABEL, NAME_PROPERTY, subject);
                }

                // if still null it's a process, add process subjectNode
                if (subjectNode == null) {
                    subjectNode = tx.findNode(PROCESS_LABEL, NAME_PROPERTY, subject);
                    if (subjectNode == null) {
                        subjectNode = tx.createNode(PROCESS_LABEL);
                        subjectNode.setProperty(NAME_PROPERTY, subject);
                    }
                }

                subjectNode.createRelationshipTo(prohibitionNode, PROHIBITION_SUBJECT_REL_TYPE);

                return subjectNode;
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }
}
