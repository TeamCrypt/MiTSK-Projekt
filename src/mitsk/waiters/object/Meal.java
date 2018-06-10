package mitsk.waiters.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractMeal;

import java.util.Arrays;

public class Meal extends AbstractMeal {
    private Long identificationNumber;

    public Meal(RTIambassador rtiAmbassador, Long mealId) throws Exception {
        super(rtiAmbassador);

        if (!Arrays.asList(getAllowedMealIds()).contains(mealId)) {
            throw new IllegalArgumentException("Unknown meal id " + mealId);
        }

        identificationNumber = mealId;
    }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    public Long getIdentificationNumber() {
        return identificationNumber;
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }
}
