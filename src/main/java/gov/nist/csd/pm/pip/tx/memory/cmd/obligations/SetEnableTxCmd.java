package gov.nist.csd.pm.pip.tx.memory.cmd.obligations;

import gov.nist.csd.pm.exceptions.PMException;
import gov.nist.csd.pm.pip.obligations.Obligations;
import gov.nist.csd.pm.pip.tx.memory.cmd.TxCmd;

public class SetEnableTxCmd implements TxCmd {

    private Obligations obligations;
    private String label;
    private boolean enabled;
    private boolean oldEnabled;

    public SetEnableTxCmd(Obligations obligations, String label, boolean enabled) {
        this.obligations = obligations;
        this.label = label;
        this.enabled = enabled;
    }

    @Override
    public void commit() throws PMException {
        this.oldEnabled = obligations.get(label).isEnabled();
        this.obligations.setEnable(label, enabled);
    }

    @Override
    public void rollback() throws PMException {
        this.obligations.setEnable(label, oldEnabled);
    }
}
