package mitsk.queue.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class Client extends AbstractObject {
    private Long identificationNumber;

    private double impatience;

    public Client(RTIambassador rtiAmbassador, Long identificationNumber, double impatience) throws Exception {
        super(rtiAmbassador);

        this.identificationNumber = identificationNumber;

        this.impatience = impatience;
    }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    public Long getIdentificationNumber() {
        return identificationNumber;
    }

    public double getImpatience() {
        return impatience;
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }
}
