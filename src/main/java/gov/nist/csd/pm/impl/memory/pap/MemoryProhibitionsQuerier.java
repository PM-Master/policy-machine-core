package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.dag.DepthFirstGraphWalker;
import gov.nist.csd.pm.common.graph.dag.Direction;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.exception.ProhibitionDoesNotExistException;
import gov.nist.csd.pm.pap.query.ProhibitionsQuerier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryProhibitionsQuerier extends ProhibitionsQuerier {

    private MemoryPolicy memoryPolicy;
    private MemoryGraphQuerier graph;

    public MemoryProhibitionsQuerier(MemoryPolicy memoryPolicy, MemoryGraphQuerier graph) {
        this.memoryPolicy = memoryPolicy;
        this.graph = graph;
    }

    @Override
    public Map<String, List<Prohibition>> getAll() throws PMException {
        Map<String, List<Prohibition>> retProhibitions = new HashMap<>();
        for (String subject : memoryPolicy.prohibitions.keySet()) {
            retProhibitions.put(subject, memoryPolicy.prohibitions.get(subject));
        }

        return retProhibitions;
    }

    @Override
    public boolean exists(String name) throws PMException {
        for (Map.Entry<String, List<Prohibition>> e : memoryPolicy.prohibitions.entrySet()) {
            for (Prohibition p : e.getValue()) {
                if (p.getName().equals(name)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<Prohibition> getWithSubject(String subject) throws PMException {
        List<Prohibition> subjectPros = memoryPolicy.prohibitions.get(subject);
        if (subjectPros == null) {
            return new ArrayList<>();
        }

        return subjectPros;
    }

    @Override
    public Prohibition getInternal(String name) throws PMException {
        for (String subject : memoryPolicy.prohibitions.keySet()) {
            List<Prohibition> subjectPros = memoryPolicy.prohibitions.get(subject);
            for (Prohibition p : subjectPros) {
                if (p.getName().equals(name)) {
                    return p;
                }
            }
        }

        throw new ProhibitionDoesNotExistException(name);
    }

    @Override
    public List<Prohibition> getInheritedProhibitionsFor(String subject) throws PMException {
        List<Prohibition> pros = new ArrayList<>();

        new DepthFirstGraphWalker(graph)
                .withVisitor((n) -> {
                    pros.addAll(getWithSubject(n));
                })
                .withDirection(Direction.PARENTS)
                .walk(subject);

        return pros;
    }

    @Override
    public List<Prohibition> getProhibitionsWithContainer(String container) throws PMException {
        List<Prohibition> pros = new ArrayList<>();

        Map<String, List<Prohibition>> prohibitions = getAll();
        for (String subject : prohibitions.keySet()) {
            List<Prohibition> subjectProhibitions = prohibitions.get(subject);
            for (Prohibition prohibition : subjectProhibitions) {
                for (ContainerCondition cc : prohibition.getContainers()) {
                    if (cc.getName().equals(container)) {
                        pros.add(prohibition);
                    }
                }
            }
        }

        return pros;
    }
}
