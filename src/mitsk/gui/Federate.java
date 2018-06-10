package mitsk.gui;

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

    private InteractionClassHandle newInQueueInteractionClassHandle;

    private ParameterHandle newInQueueInteractionClassClientIdParameterHandle;

    private InteractionClassHandle leaveFromQueueInteracionClassHandle;

    private ParameterHandle leaveFromQueueInteractionClassClientIdParameterHandle;

    private InteractionClassHandle clientImpatienceInteracionClassHandle;

    private ParameterHandle clientImpatienceInteractionClassClientIdParameterHandle;

    public Federate(String federationName) throws Exception {
        super(federationName);
    }

    InteractionClassHandle getNewInQueueInteractionClassHandle() {
        return newInQueueInteractionClassHandle;
    }

    ParameterHandle getNewInQueueInteractionClassClientIdParameterHandle() {
        return newInQueueInteractionClassClientIdParameterHandle;
    }

    InteractionClassHandle getLeaveFromQueueInteracionClassHandle() {
        return leaveFromQueueInteracionClassHandle;
    }

    ParameterHandle getLeaveFromQueueInteractionClassClientIdParameterHandle() {
        return leaveFromQueueInteractionClassClientIdParameterHandle;
    }

    InteractionClassHandle getClientImpatienceInteracionClassHandle() {
        return leaveFromQueueInteracionClassHandle;
    }

    ParameterHandle getClientImpatienceInteractionClassClientIdParameterHandle() {
        return clientImpatienceInteractionClassClientIdParameterHandle;
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
            (new File("foms/Statistics.xml")).toURI().toURL()
        };
    }

    @Override
    protected URL[] getJoinModules() throws MalformedURLException {
        return new URL[]{
            (new File("foms/Gui.xml")).toURI().toURL()
        };
    }

    public static void main(String[] args) {
        String federationName = args.length > 0 ? args[0] : "RestaurantFederation";

        try {
            new mitsk.gui.Federate(federationName).run();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
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
        // empty
    }

    @Override
    protected void publish() throws Exception {
        // empty
    }

    @Override
    protected void subscribe() throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        { // NewInQueue
            newInQueueInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewInQueue");

            rtiAmbassador.subscribeInteractionClass(newInQueueInteractionClassHandle);

            newInQueueInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(newInQueueInteractionClassHandle, "clientId");
        }

        { // LeaveFromQueue
            leaveFromQueueInteracionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.LeaveFromQueue");

            rtiAmbassador.subscribeInteractionClass(leaveFromQueueInteracionClassHandle);

            leaveFromQueueInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(leaveFromQueueInteracionClassHandle, "clientId");
        }

        { // ClientImpatience
            clientImpatienceInteracionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewInQueue.ClientImpatience");

            rtiAmbassador.subscribeInteractionClass(clientImpatienceInteracionClassHandle);

            newInQueueInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientImpatienceInteracionClassHandle, "clientId");
        }
    }
}