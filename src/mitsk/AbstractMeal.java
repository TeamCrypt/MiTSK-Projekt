package mitsk;

import hla.rti1516e.RTIambassador;

import java.util.Arrays;

public abstract class AbstractMeal extends AbstractObject {
    private Long identificationNumber;

    protected AbstractMeal(RTIambassador rtiAmbassador, Long identificationNumber) throws Exception {
        super(rtiAmbassador);

        if (!Arrays.asList(getAllowedMealIds()).contains(identificationNumber)) {
            throw new IllegalArgumentException("Unknown meal id " + identificationNumber);
        }

        this.identificationNumber = identificationNumber;
    }

    public static String[] getAllowedMeals() {
        return new String[] {
            "soup",
            "fries",
            "burger",
            "kebab",
            "salad"
        };
    }

    public static double[] getAllowedMealsCosts() {
        return new double[] {
            10.0,
            12.5,
            5.5,
            8.0,
            11.0
        };
    }

    public static Long[] getAllowedMealIds() {
        int length = getAllowedMeals().length;

        Long[] mealIds = new Long[length];

        for (int i = 0; i < length; ++i) {
            mealIds[i] = (long) i;
        }

        return mealIds;
    }

    public Long getIdentificationNumber() {
        return identificationNumber;
    }

    public String getName() {
        return getAllowedMeals()[Math.toIntExact(identificationNumber)];
    }

    public double getCost() {
        return getAllowedMealsCosts()[Math.toIntExact(getIdentificationNumber())];
    }
}
