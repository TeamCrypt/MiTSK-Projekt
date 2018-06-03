package mitsk.kitchen.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class MealRequest extends AbstractObject {
    private Client client;

    private Meal meal;

    private double preparationTime;

    public MealRequest(RTIambassador rtiAmbassador, Client client, Meal meal, double preparationTime) throws Exception {
        super(rtiAmbassador);

        this.client = client;

        this.meal = meal;

        this.preparationTime = preparationTime;
    }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    public Client getClient() {
        return client;
    }

    public Meal getMeal() {
        return meal;
    }

    public double getPreparationTime() {
        return preparationTime;
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }
}
