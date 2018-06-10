package mitsk.kitchen;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;
import mitsk.kitchen.interaction.PreparedMealRequest;
import mitsk.kitchen.object.Client;
import mitsk.kitchen.object.Meal;
import mitsk.kitchen.object.MealRequest;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Federate extends AbstractFederate {
    private static final double A = 8.0;

    private static final double B = 32.0;

    private InteractionClassHandle newMealRequestInteractionClassHandle;

    private ParameterHandle newMealRequestInteractionClassClientIdParameterHandle;

    private ParameterHandle newMealRequestInteractionClassMealIdParameterHandle;

    private List<MealRequest> mealsRequests = new ArrayList<>();

    private List<Meal> preparedMeals = new ArrayList<>();

    private InteractionClassHandle takeFoodInteractionClassHandle;

    private ParameterHandle takeFoodInteractionClassClientIdParameterHandle;

    private ParameterHandle takeFoodInteractionClassMealIdParameterHandle;

    public Federate(String federationName) throws Exception {
        super(federationName);
    }

    void addMealRequest(Long clientId, Long mealId) {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        double federationTime = getFederateAmbassador().getFederateTime();

        try {
            mealsRequests.add(new MealRequest(rtiAmbassador, new Meal(rtiAmbassador, mealId, new Client(rtiAmbassador, clientId)), federationTime + randomDouble(A, B)));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected AbstractFederateAmbassador createAmbassador() throws Exception {
        return new Ambassador(this);
    }

    @Override
    protected URL[] getFederationModules() throws MalformedURLException {
        return new URL[]{
            (new File("foms/Clients.xml")).toURI().toURL(),
            (new File("foms/Gui.xml")).toURI().toURL(),
            (new File("foms/Kitchen.xml")).toURI().toURL(),
            (new File("foms/Queue.xml")).toURI().toURL(),
            (new File("foms/Statistics.xml")).toURI().toURL(),
            (new File("foms/Tables.xml")).toURI().toURL(),
            (new File("foms/Waiters.xml")).toURI().toURL()
        };
    }

    @Override
    protected URL[] getJoinModules() throws MalformedURLException {
        return new URL[]{
            (new File("foms/Kitchen.xml")).toURI().toURL()
        };
    }

    InteractionClassHandle getNewMealRequestInteractionClassHandle() {
        return newMealRequestInteractionClassHandle;
    }

    ParameterHandle getNewMealRequestInteractionClassClientIdParameterHandle() {
        return newMealRequestInteractionClassClientIdParameterHandle;
    }

    ParameterHandle getNewMealRequestInteractionClassMealIdParameterHandle() {
        return newMealRequestInteractionClassMealIdParameterHandle;
    }

    InteractionClassHandle getTakeFoodInteractionClassHandle() {
        return takeFoodInteractionClassHandle;
    }

    ParameterHandle getTakeFoodInteractionClassClientIdParameterHandle() {
        return takeFoodInteractionClassClientIdParameterHandle;
    }

    ParameterHandle getTakeFoodInteractionClassMealIdParameterHandle() {
        return takeFoodInteractionClassMealIdParameterHandle;
    }

    private void informAboutPreparedMeals() {
        List<MealRequest> preparedRequests = new ArrayList<>();

        RTIambassador rtiAmbassador = getRTIAmbassador();

        double federationTime = getFederateAmbassador().getFederateTime();

        for (MealRequest mealRequest : mealsRequests) {
            if (mealRequest.getReadyAt() <= federationTime) {
                try {
                    Meal meal = mealRequest.getMeal();

                    Client client = meal.getClient();

                    PreparedMealRequest preparedMealRequest = new PreparedMealRequest(rtiAmbassador, client, meal);

                    preparedMealRequest.sendInteraction();

                    preparedRequests.add(mealRequest);

                    preparedMeals.add(meal);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        if (preparedRequests.size() > 0) {
            mealsRequests.removeAll(preparedRequests);
        }
    }

    public static void main(String[] args) {
        String federationName = args.length > 0 ? args[0] : "RestaurantFederation";

        try {
            new Federate(federationName).run();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void publish() throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.PreparedMealRequest"));
    }

    boolean removePreparedFood(Long clientId, Long mealId) {
        Meal toRemove = null;

        for (Meal meal : preparedMeals) {
            if (meal.getIdentificationNumber().equals(mealId) && meal.getClient().getIdentificationNumber().equals(clientId)) {
                toRemove = meal;
            }
        }

        if (toRemove != null) {
            preparedMeals.remove(toRemove);
        }

        return toRemove != null;
    }

    @Override
    public void run() throws Exception {
        super.run();

        for (int i = 0; i < ITERATIONS; i++) {
            sendInteraction();

            advanceTime(1.0);

            log("Time Advanced to " + getFederateAmbassador().getFederateTime());
        }

        resignFederation();
    }

    private void sendInteraction() {
        informAboutPreparedMeals();
    }

    @Override
    protected void subscribe() throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        { // NewMealRequest
            newMealRequestInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewMealRequest");

            rtiAmbassador.subscribeInteractionClass(newMealRequestInteractionClassHandle);

            newMealRequestInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(newMealRequestInteractionClassHandle, "clientId");

            newMealRequestInteractionClassMealIdParameterHandle = rtiAmbassador.getParameterHandle(newMealRequestInteractionClassHandle, "mealId");
        }

        { // TakeFood
            takeFoodInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("TakeFood");

            rtiAmbassador.subscribeInteractionClass(takeFoodInteractionClassHandle);

            takeFoodInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(takeFoodInteractionClassHandle, "clientId");

            takeFoodInteractionClassMealIdParameterHandle = rtiAmbassador.getParameterHandle(takeFoodInteractionClassHandle, "mealId");
        }
    }
}
