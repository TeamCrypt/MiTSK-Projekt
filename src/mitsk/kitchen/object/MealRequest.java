package mitsk.kitchen.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class MealRequest extends AbstractObject {
    private Meal meal;

    private double readyAt;

    public MealRequest(RTIambassador rtiAmbassador, Meal meal, double readyAt) throws Exception {
        super(rtiAmbassador);

        this.meal = meal;

        this.readyAt = readyAt;
    }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    public Meal getMeal() {
        return meal;
    }

    public double getReadyAt() {
        return readyAt;
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }
}
