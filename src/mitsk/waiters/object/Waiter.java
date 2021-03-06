package mitsk.waiters.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class Waiter extends AbstractObject {
    private Long identificationNumber;

    private static Long nextWaiterId = 0L;

    private Client serves = null;

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

    public boolean isFree() {
        return serves == null;
    }

    public void setBusy(Client client) {
        serves = client;
    }

    public void setFree() {
        serves = null;
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }
}
