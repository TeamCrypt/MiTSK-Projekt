package mitsk.tables.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class Client extends AbstractObject {
    private Long identificationNumber;

    private boolean wantsToLeave = false;

    public Client(RTIambassador rtiAmbassador, Long identificationNumber) throws Exception {
        super(rtiAmbassador);

        this.identificationNumber = identificationNumber;
    }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    public Long getIdentificationNumber() {
        return identificationNumber;
    }

    public boolean isWantsToLeave() {
        return wantsToLeave;
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }

    public void wantsToLeave() {
        wantsToLeave = true;
    }
}
