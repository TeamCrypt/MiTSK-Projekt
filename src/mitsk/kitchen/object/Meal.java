package mitsk.kitchen.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractMeal;

import java.util.Arrays;

public class Meal extends AbstractMeal {
    public Meal(RTIambassador rtiAmbassador, Long mealId) throws Exception {
        super(rtiAmbassador);

        if (!Arrays.asList(getAllowedMealIds()).contains(mealId)) {
            throw new IllegalArgumentException("Unknown meal id " + mealId);
        }
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
