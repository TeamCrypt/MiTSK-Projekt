package mitsk.kitchen.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractMeal;

public class Meal extends AbstractMeal {
    private Client client;

    public Meal(RTIambassador rtiAmbassador, Long mealId, Client client) throws Exception {
        super(rtiAmbassador, mealId);

        this.client = client;
    }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    public Client getClient() {
        return client;
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }
}
