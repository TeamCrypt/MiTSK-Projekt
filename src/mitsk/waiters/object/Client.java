package mitsk.waiters.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class Client extends AbstractObject {
    private double bill = 0.0;

    private Long identificationNumber;

    public Client(RTIambassador rtiAmbassador, Long identificationNumber) throws Exception {
        super(rtiAmbassador);

        this.identificationNumber = identificationNumber;
    }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    public double getBill() {
        return bill;
    }

    public Long getIdentificationNumber() {
        return identificationNumber;
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }

    public void orders(Meal meal) {
        bill += meal.getCost();
    }
}
