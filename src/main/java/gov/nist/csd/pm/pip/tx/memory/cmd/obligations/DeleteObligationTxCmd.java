package gov.nist.csd.pm.pip.tx.memory.cmd.obligations;

import gov.nist.csd.pm.exceptions.PMException;
import gov.nist.csd.pm.pip.obligations.Obligations;
import gov.nist.csd.pm.pip.obligations.model.Obligation;
import gov.nist.csd.pm.pip.tx.memory.cmd.TxCmd;

public class DeleteObligationTxCmd implements TxCmd {

    private Obligations obligations;
    private String label;
    private Obligation obligation;

    public DeleteObligationTxCmd(Obligations obligations, String label) {
        this.obligations = obligations;
        this.label = label;
    }

    @Override
    public void commit() throws PMException {
        this.obligation = this.obligations.get(label);
        this.obligations.delete(label);
    }

    @Override
    public void rollback() throws PMException {
        this.obligations.add(this.obligation, this.obligation.isEnabled());
    }
}
