package gov.nist.csd.pm.common.graph.dag;

import gov.nist.csd.pm.common.exception.PMException;

public interface Propagator {

    /**
     * Propagate information from the node to the target node. This can be from a parent to a child if direction is
     *
     * @param src the node that holds the information already.
     * @param dst the node to propagate the information to.
     */
    void propagate(String src, String dst) throws PMException;
}
