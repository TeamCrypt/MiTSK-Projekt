package mitsk.clients.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractMeal;

public class Meal extends AbstractMeal {
    public Meal(RTIambassador rtiAmbassador, Long mealId) throws Exception {
        super(rtiAmbassador, mealId);
    }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }
}
