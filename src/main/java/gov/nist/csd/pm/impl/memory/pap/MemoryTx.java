package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.tx.Transactional;

import java.util.Objects;

public class MemoryTx implements Transactional {
    private boolean active;
    private int counter;
    private MemoryPolicyModifier modifier;

    public MemoryTx() {
        active = false;
        counter = 0;
        modifier = null;
    }

    public MemoryTx(boolean active, int counter, MemoryPolicyModifier modifier) {
        this.active = active;
        this.counter = counter;
        this.modifier = modifier;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public MemoryPolicyModifier getModifier() {
        return modifier;
    }

    public void setModifier(MemoryPolicyModifier modifier) {
        this.modifier = modifier;
    }

    @Override
    public void beginTx() {
        active = true;
        counter++;
    }

    @Override
    public void commit() {
        counter--;
        active = counter != 0;
    }

    @Override
    public void rollback() {
        counter = 0;
        active = false;
    }

    @Override
    public boolean equals(java.lang.Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (MemoryTx) obj;
        return this.active == that.active &&
                this.counter == that.counter &&
                Objects.equals(this.modifier, that.modifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(active, counter, modifier);
    }

    @Override
    public String toString() {
        return "MemoryTx[" +
                "active=" + active + ", " +
                "counter=" + counter + ", " +
                "policyStore=" + modifier + ']';
    }
}
