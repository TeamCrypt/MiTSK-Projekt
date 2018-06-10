package mitsk.waiters.object;

import hla.rti1516e.RTIambassador;

public class TakeMealRequest extends WaiterRequest {
    private Meal meal;

    public TakeMealRequest(RTIambassador rtiAmbassador, Client client, Meal meal) throws Exception {
        super(rtiAmbassador, client);

        this.meal = meal;
    }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    public Meal getMeal() {
        return meal;
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }
}
