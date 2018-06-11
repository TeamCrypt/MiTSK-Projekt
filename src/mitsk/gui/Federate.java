package mitsk.gui;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Federate extends AbstractFederate {
    private GUI gui = new GUI();

    private InteractionClassHandle clientImpatienceInteractionClassHandle;

    private InteractionClassHandle clientLeavesTableInteractionClassHandle;

    private InteractionClassHandle newClientInteractionClassHandle;

    private InteractionClassHandle newInQueueInteractionClassHandle;

    private InteractionClassHandle leaveFromQueueInteractionClassHandle;

    public Federate(String federationName) throws Exception {
        super(federationName);
    }

    InteractionClassHandle getClientImpatienceInteractionClassHandle() {
        return clientImpatienceInteractionClassHandle;
    }

    InteractionClassHandle getClientLeavesTableInteractionClassHandle() {
        return clientLeavesTableInteractionClassHandle;
    }

    InteractionClassHandle getLeaveFromQueueInteractionClassHandle() {
        return leaveFromQueueInteractionClassHandle;
    }

    InteractionClassHandle getNewClientInteractionClassHandle() {
        return newClientInteractionClassHandle;
    }

    InteractionClassHandle getNewInQueueInteractionClassHandle() {
        return newInQueueInteractionClassHandle;
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

    public GUI getGui() {
        return gui;
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
            new Federate(federationName).run();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void resignFederation() throws Exception {
        super.resignFederation();

        gui.setVisible(false);
        gui.dispose();
    }

    @Override
    public void run() throws Exception {
        gui.run();

        super.run();

        for (int i = 0; i < ITERATIONS; i++) {
            sendInteraction();

            advanceTime(1.0);

            log("Time Advanced to " + getFederateAmbassador().getFederateTime());

            TimeUnit.MILLISECONDS.sleep(500);
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

        { // NewClient
            newClientInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewClient");

            rtiAmbassador.subscribeInteractionClass(newClientInteractionClassHandle);
        }

        { // NewInQueue
            newInQueueInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewInQueue");

            rtiAmbassador.subscribeInteractionClass(newInQueueInteractionClassHandle);
        }

        { // LeaveFromQueue
            leaveFromQueueInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.LeaveFromQueue");

            rtiAmbassador.subscribeInteractionClass(leaveFromQueueInteractionClassHandle);
        }

        { // ClientImpatience
            clientImpatienceInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.LeaveFromQueue.ClientImpatience");

            rtiAmbassador.subscribeInteractionClass(clientImpatienceInteractionClassHandle);
        }

        { // ClientLeavesTable
            clientLeavesTableInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientLeavesTable");

            rtiAmbassador.subscribeInteractionClass(clientLeavesTableInteractionClassHandle);
        }
    }
}
