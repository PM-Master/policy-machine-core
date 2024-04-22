package gov.nist.csd.pm.impl.memory.pdp;

import gov.nist.csd.pm.common.graph.dag.DepthFirstGraphWalker;
import gov.nist.csd.pm.pap.Policy;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.dag.Direction;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.ProhibitionsReview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MemoryProhibitionsReviewer implements ProhibitionsReview {

    private final Policy policy;

    public MemoryProhibitionsReviewer(Policy policy) {
        this.policy = policy;
    }

    @Override
    public List<Prohibition> getInheritedProhibitionsFor(String subject) throws PMException {
        List<Prohibition> pros = new ArrayList<>();

        new DepthFirstGraphWalker(policy.graph())
                .withVisitor((n) -> {
                    pros.addAll(policy.prohibitions().getWithSubject(n));
                })
                .withDirection(Direction.PARENTS)
                .walk(subject);

        return pros;
    }

    @Override
    public List<Prohibition> getProhibitionsWithContainer(String container) throws PMException {
        List<Prohibition> pros = new ArrayList<>();

        Map<String, List<Prohibition>> prohibitions = policy.prohibitions().getAll();
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
