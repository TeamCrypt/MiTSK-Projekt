package mitsk.kitchen;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Federate extends AbstractFederate {
    private static final int ITERATIONS = 20;

    private InteractionClassHandle newMealRequestInteractionClassHandle;

    private ParameterHandle newMealRequestInteractionClassClientIdParameterHandle;

    private ParameterHandle newMealRequestInteractionClassMealIdParameterHandle;

    public Federate(String federationName) throws Exception {
        super(federationName);
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
