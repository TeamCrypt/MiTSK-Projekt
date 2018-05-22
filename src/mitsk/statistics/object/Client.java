package mitsk.statistics.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class Client extends AbstractObject {
    private Long identificationNumber;

    private double federateTime;

    public Client(RTIambassador rtiAmbassador, Long identificationNumber, double federateTime) throws Exception {
        super(rtiAmbassador);

        this.identificationNumber = identificationNumber;

        this.federateTime = federateTime;
    }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    public Long getIdentificationNumber() {
        return identificationNumber;
    }

    public double getFederateTime() {
        return federateTime;
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }
}
