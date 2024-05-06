package gov.nist.csd.pm.impl.memory.pdp;

import gov.nist.csd.pm.common.graph.dag.DepthFirstGraphWalker;
import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.dag.Direction;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.query.ProhibitionsQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MemoryProhibitionsReviewer implements ProhibitionsQuery {

    private final PolicyModification policyModification;

    public MemoryProhibitionsReviewer(PolicyModification policyModification) {
        this.policyModification = policyModification;
    }

    @Override
    public List<Prohibition> getInheritedProhibitionsFor(String subject) throws PMException {
        List<Prohibition> pros = new ArrayList<>();

        new DepthFirstGraphWalker(policyModification.graph())
                .withVisitor((n) -> {
                    pros.addAll(policyModification.prohibitions().getWithSubject(n));
                })
                .withDirection(Direction.PARENTS)
                .walk(subject);

        return pros;
    }

    @Override
    public List<Prohibition> getProhibitionsWithContainer(String container) throws PMException {
        List<Prohibition> pros = new ArrayList<>();

        Map<String, List<Prohibition>> prohibitions = policyModification.prohibitions().getAll();
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
