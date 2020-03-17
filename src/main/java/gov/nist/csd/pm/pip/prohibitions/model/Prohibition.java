package gov.nist.csd.pm.pip.prohibitions.model;

import gov.nist.csd.pm.exceptions.PMException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Object representing a Prohibition.
 */
public class Prohibition {

    private String      name;
    private Subject     subject;
    private List<Node>  nodes;
    private Set<String> operations;
    private boolean     intersection;

    public Prohibition(String name, Subject subject) {
        this.name = name;
        this.subject = subject;
        this.nodes = new ArrayList<>();
    }

    public Prohibition() {
        this.nodes = new ArrayList<>();
    }

    public Prohibition(String name, Subject subject, List<Node> nodes, Set<String> operations, boolean intersection) {
        if (subject == null) {
            throw new IllegalArgumentException("Prohibition subject cannot be null");
        }
        this.subject = subject;
        if (nodes == null) {
            this.nodes = new ArrayList<>();
        }
        else {
            this.nodes = nodes;
        }
        this.name = name;
        this.operations = operations;
        this.intersection = intersection;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void removeNode(String name) {
        for (Node n : nodes) {
            if (n.getName().equals(name)) {
                nodes.remove(n);
                return;
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getOperations() {
        return operations;
    }

    public void setOperations(Set<String> operations) {
        this.operations = operations;
    }

    public boolean isIntersection() {
        return intersection;
    }

    public void setIntersection(boolean intersection) {
        this.intersection = intersection;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Prohibition)) {
            return false;
        }

        Prohibition p = (Prohibition) o;
        return this.getName().equals(p.getName());
    }

    public int hashCode() {
        return Objects.hash(name);
    }

    public static class Subject {
        String subject;
        Type subjectType;

        /**
         * Prohibition Subject constructor.  The name cannot be empty and the type cannot be null.
         *
         * @param subject
         * @param subjectType
         */
        public Subject(String subject, Type subjectType) {
            if (subject == null || subject.isEmpty()) {
                throw new IllegalArgumentException("a prohibition subject cannot have an ID of 0");
            }
            else if (subjectType == null) {
                throw new IllegalArgumentException("a prohibition subject cannot have a null type");
            }
            this.subject = subject;
            this.subjectType = subjectType;
        }

        public String getSubject() {
            return subject;
        }

        public Type getSubjectType() {
            return subjectType;
        }

        public enum Type {
            USER_ATTRIBUTE,
            USER,
            PROCESS;

            /**
             * Given a string, return the corresponding Type.  If the string is null, an IllegalArgumentException will
             * be thrown, and if the string does not match any of (USER, USER_ATTRIBUTE, PROCESS) a PMProhbitionExceptino will
             * be thrown because the provided string is not a valid subject type.
             *
             * @param subjectType the string to convert to a Type.
             * @return the SUbjectType tht corresponds to the given string.
             * @throws IllegalArgumentException if the given string is null.
             * @throws PMException              if the given string is not a valid subject.
             */
            public static Type toType(String subjectType) throws PMException {
                if (subjectType == null) {
                    throw new IllegalArgumentException("null is an invalid Prohibition subject type");
                }
                switch (subjectType.toUpperCase()) {
                    case "USER_ATTRIBUTE":
                        return USER_ATTRIBUTE;
                    case "USER":
                        return USER;
                    case "PROCESS":
                        return PROCESS;
                    default:
                        throw new PMException(String.format("%s is an invalid Prohibition subject type", subjectType));
                }
            }
        }
    }

    public static class Node {
        String    name;
        boolean complement;

        public Node(String name, boolean complement) {
            this.name = name;
            this.complement = complement;
        }

        public String getName() {
            return name;
        }

        public boolean isComplement() {
            return complement;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Node)) {
                return false;
            }

            Node node = (Node) o;
            return this.getName().equals(node.getName());
        }

        public int hashCode() {
            return name.hashCode();
        }
    }
}
