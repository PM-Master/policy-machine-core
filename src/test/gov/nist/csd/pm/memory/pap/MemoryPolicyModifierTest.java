package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.modification.*;

import static org.junit.jupiter.api.Assertions.*;

class MemoryPolicyModifierTest extends PolicyModifierTest {

    @Override
    public PAP getPAP() throws PMException {
        return new MemoryPAP();
    }
}

class MemoryGraphModifierTest extends GraphModifierTest {

    @Override
    public PAP getPAP() throws PMException {
        return new MemoryPAP();
    }
}

class MemoryProhibitionsModifierTest extends ProhibitionsModifierTest {

    @Override
    public PAP getPAP() throws PMException {
        return new MemoryPAP();
    }
}

class MemoryObligationsModifierTest extends ObligationsModifierTest {

    @Override
    public PAP getPAP() throws PMException {
        return new MemoryPAP();
    }
}

class MemoryPMLModifierTest extends PMLModifierTest {

    @Override
    public PAP getPAP() throws PMException {
        return new MemoryPAP();
    }
}