package mitsk.waiters;

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

    private InteractionClassHandle clientCallsWaiterInteractionClassHandle;

    private ParameterHandle clientCallsWaiterInteractionClassClientIdParameterHandle;

    private InteractionClassHandle clientOrdersMealInteractionClassHandle;

    private ParameterHandle clientOrdersMealInteractionClassClientIdParameterHandle;

    private ParameterHandle clientOrdersMealInteractionClassMealIdParameterHandle;

    private InteractionClassHandle preparedMealRequestInteractionClassHandle;

    private ParameterHandle preparedMealRequestInteractionClassClientIdParameterHandle;

    private ParameterHandle preparedMealRequestInteractionClassMealIdParameterHandle;

    private InteractionClassHandle clientAsksForBillInteractionClassHandle;

    private ParameterHandle clientAsksForBillInteractionClassClientIdParameterHandle;

    private InteractionClassHandle endingClientServiceInteractionClassHandle;

    private ParameterHandle endingClientServiceInteractionClassClientIdParameterHandle;

    public Federate(String federationName) throws Exception {
        super(federationName);
    }

    public InteractionClassHandle getClientCallsWaiterInteractionClassHandle() {
        return clientCallsWaiterInteractionClassHandle;
    }

    public ParameterHandle getClientCallsWaiterInteractionClassClientIdParameterHandle() {
        return clientCallsWaiterInteractionClassClientIdParameterHandle;
    }

    public InteractionClassHandle getClientOrdersMealInteractionClassHandle() {
        return clientOrdersMealInteractionClassHandle;
    }

    public ParameterHandle getClientOrdersMealInteractionClassClientIdParameterHandle() {
        return clientOrdersMealInteractionClassClientIdParameterHandle;
    }

    public ParameterHandle getClientOrdersMealInteractionClassMealIdParameterHandle() {
        return clientOrdersMealInteractionClassMealIdParameterHandle;
    }

    public InteractionClassHandle getPreparedMealRequestInteractionClassHandle() {
        return preparedMealRequestInteractionClassHandle;
    }

    public ParameterHandle getPreparedMealRequestInteractionClassClientIdParameterHandle() {
        return preparedMealRequestInteractionClassClientIdParameterHandle;
    }

    public ParameterHandle getPreparedMealRequestInteractionClassMealIdParameterHandle() {
        return preparedMealRequestInteractionClassMealIdParameterHandle;
    }

    public InteractionClassHandle getClientAsksForBillInteractionClassHandle() {
        return clientAsksForBillInteractionClassHandle;
    }

    public ParameterHandle getClientAsksForBillInteractionClassClientIdParameterHandle() {
        return clientAsksForBillInteractionClassClientIdParameterHandle;
    }

    public InteractionClassHandle getEndingClientServiceInteractionClassHandle() {
        return endingClientServiceInteractionClassHandle;
    }

    public ParameterHandle getEndingClientServiceInteractionClassClientIdParameterHandle() {
        return endingClientServiceInteractionClassClientIdParameterHandle;
    }

    @Override
    protected AbstractFederateAmbassador createAmbassador() throws Exception {
        return new Ambassador(this);
    }

    @Override
    protected URL[] getFederationModules() throws MalformedURLException {
        return new URL[]{
            (new File("foms/Clients.xml")).toURI().toURL(),
            (new File("foms/Waiters.xml")).toURI().toURL(),
            (new File("foms/Kitchen.xml")).toURI().toURL(),
            (new File("foms/Queue.xml")).toURI().toURL(),
            (new File("foms/Statistics.xml")).toURI().toURL(),
        };
    }

    @Override
    protected URL[] getJoinModules() throws MalformedURLException {
        return new URL[]{
            (new File("foms/Waiters.xml")).toURI().toURL()
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
        RTIambassador rtiAmbassador = getRTIAmbassador();

        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.StartingClientService"));
        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewMealRequest"));
        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.TakeFood"));
        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.GiveMeal"));
        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.PaymentService"));
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

    public void sendInteraction() {}

    @Override
    protected void subscribe() throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();
    }
}