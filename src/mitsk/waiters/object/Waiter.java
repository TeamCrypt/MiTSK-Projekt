package mitsk.waiters.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class Waiter extends AbstractObject {
    private Long identificationNumber;

    private static Long nextWaiterId = 0L;

    private boolean ifFree = true;

    public Waiter(RTIambassador rtiAmbassador) throws Exception {
        super(rtiAmbassador);

        identificationNumber = nextWaiterId++;
    }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    public Long getIdentificationNumber() {
        return identificationNumber;
    }

    public boolean ifFree() {
        return ifFree;
    }

    public void setOccupied() {
        ifFree = false;
    }

    public void setFree() {
        ifFree = true;
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }
}
