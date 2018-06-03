package mitsk;

import hla.rti1516e.RTIambassador;

public abstract class AbstractMeal extends AbstractObject {
    protected AbstractMeal(RTIambassador rtiAmbassador) throws Exception {
        super(rtiAmbassador);
    }

    public final String[] getAllowedMeals() {
        return new String[] {
            "soup",
            "fries",
            "burger",
            "kebab",
            "salad"
        };
    }

    public final Long[] getAllowedMealIds() {
        int length = getAllowedMeals().length;

        Long[] mealIds = new Long[length];

        for (int i = 0; i < length; ++i) {
            mealIds[i] = (long) i;
        }

        return mealIds;
    }
}
