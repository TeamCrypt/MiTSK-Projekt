package mitsk.waiters.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class ClientService extends AbstractObject {
    private Client client;

    private Waiter waiter;

    private Meal meal = null;

    private boolean ifDone = false;

    public ClientService(RTIambassador rtiAmbassador, Client client, Waiter waiter) throws Exception {
        super(rtiAmbassador);

        this.client = client;
        this.waiter = waiter;
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

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public Meal getMeal() {
        return meal;
    }

    public boolean ifDone() {
        return ifDone;
    }

    public void finishService() {
        ifDone = true;
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }
}
