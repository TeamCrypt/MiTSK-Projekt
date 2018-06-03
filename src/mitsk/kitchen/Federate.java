package mitsk.kitchen;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;
import mitsk.kitchen.object.Client;
import mitsk.kitchen.object.Meal;
import mitsk.kitchen.object.MealRequest;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Federate extends AbstractFederate {
    private static final double A = 8.0;

    private static final double B = 32.0;

    private static final int ITERATIONS = 20;

    private InteractionClassHandle newMealRequestInteractionClassHandle;

    private ParameterHandle newMealRequestInteractionClassClientIdParameterHandle;

    private ParameterHandle newMealRequestInteractionClassMealIdParameterHandle;

    private List<MealRequest> mealsRequests = new ArrayList<>();

    private Random random = new Random();

    public Federate(String federationName) throws Exception {
        super(federationName);
    }

    void addMealRequest(Long clientId, Long mealId) {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        try {
            mealsRequests.add(new MealRequest(rtiAmbassador, new Client(rtiAmbassador, clientId), new Meal(rtiAmbassador, mealId), randomDouble(A, B)));
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
            (new File("foms/Kitchen.xml")).toURI().toURL(),
            (new File("foms/Queue.xml")).toURI().toURL(),
            (new File("foms/Statistics.xml")).toURI().toURL()
        };
    }

    @Override
    protected URL[] getJoinModules() throws MalformedURLException {
        return new URL[]{
            (new File("foms/Kitchen.xml")).toURI().toURL()
        };
    }

    public InteractionClassHandle getNewMealRequestInteractionClassHandle() {
        return newMealRequestInteractionClassHandle;
    }

    public ParameterHandle getNewMealRequestInteractionClassClientIdParameterHandle() {
        return newMealRequestInteractionClassClientIdParameterHandle;
    }

    public ParameterHandle getNewMealRequestInteractionClassMealIdParameterHandle() {
        return newMealRequestInteractionClassMealIdParameterHandle;
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
        // empty
    }

    private double randomDouble(double a, double b) { // Generates random double in range [a, b]
        double value = (random.nextDouble() * (b - a)) + a;

        return Math.round(value);
    }

    @Override
    public void run() throws Exception {
        super.run();

        for (int i = 0; i < ITERATIONS; i++) {
            advanceTime(1.0);

            log("Time Advanced to " + getFederateAmbassador().getFederateTime());
        }

        resignFederation();
    }

    @Override
    protected void subscribe() throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        { // NewMealRequest
            newMealRequestInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewMealRequest");

            rtiAmbassador.publishInteractionClass(newMealRequestInteractionClassHandle);

            newMealRequestInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(newMealRequestInteractionClassHandle, "clientId");

            newMealRequestInteractionClassMealIdParameterHandle = rtiAmbassador.getParameterHandle(newMealRequestInteractionClassHandle, "mealId");
        }
    }
}
