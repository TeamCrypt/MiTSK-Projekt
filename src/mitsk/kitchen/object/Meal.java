package mitsk.kitchen.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractMeal;

import java.util.Arrays;

public class Meal extends AbstractMeal {
    private Client client;

    private Long identificationNumber;

    public Meal(RTIambassador rtiAmbassador, Long mealId, Client client) throws Exception {
        super(rtiAmbassador);

        if (!Arrays.asList(getAllowedMealIds()).contains(mealId)) {
            throw new IllegalArgumentException("Unknown meal id " + mealId);
        }

        identificationNumber = mealId;

        this.client = client;
    }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    public Client getClient() {
        return client;
    }

    public Long getIdentificationNumber() {
        return identificationNumber;
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }
}
