package mitsk.waiters.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class Bill extends AbstractObject {
    private Client client;

    private Waiter waiter;

    private double billCost;

    private boolean ifPayed = false;

    public Bill(RTIambassador rtiAmbassador, Client client, Waiter waiter, double billCost) throws Exception {
        super(rtiAmbassador);

        this.client = client;

        this.waiter = waiter;

        this.billCost = billCost;
    }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    public Client getClient() {
        return client;
    }

    public Waiter getWaiter() {
        return waiter;
    }

    public double getBillCost() {
        return billCost;
    }

    public boolean ifPayed() {
        return ifPayed;
    }

    public void payBill() {
        ifPayed = true;
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }
}
