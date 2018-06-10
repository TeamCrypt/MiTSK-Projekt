package mitsk.clients;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;
import mitsk.clients.interaction.NewClient;
import mitsk.clients.object.Client;
import mitsk.clients.object.Table;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class Federate extends AbstractFederate {
    private static final double A = 1.0;

    private static final double B = 8.0;

    private HashMap<Long, Client> clients = new HashMap<>();

    private InteractionClassHandle clientImpatienceInteractionClassHandle;

    private ParameterHandle clientImpatienceInteractionClassClientIdParameterHandle;

    private InteractionClassHandle clientLeavesTableInteractionClassHandle;

    private ParameterHandle clientLeavesTableInteractionClassClientIdParameterHandle;

    private InteractionClassHandle clientTakesTableInteractionClassHandle;

    private ParameterHandle clientTakesTableInteractionClassClientIdParameterHandle;

    private ParameterHandle clientTakesTableInteractionClassTableIdParameterHandle;

    private InteractionClassHandle leaveFromQueueInteractionClassHandle;

    private ParameterHandle leaveFromQueueInteractionClassClientIdParameterHandle;

    private double nextClientAt = 0.0;

    public Federate(String federationName) throws Exception {
        super(federationName);
    }

    @Override
    protected AbstractFederateAmbassador createAmbassador() {
        return new Ambassador(this);
    }

    private void createNewClient() {
        if (nextClientAt <= getFederateAmbassador().getFederateTime()) {
            try {
                RTIambassador rtiAmbassador = getRTIAmbassador();

                Client client = new Client(rtiAmbassador);

                new NewClient(rtiAmbassador, client).sendInteraction();

                clients.put(client.getIdentificationNumber(), client);

                log("New Client " + client.getIdentificationNumber());

                nextClientAt += randomDouble(A, B);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    void clientTakesTable(Long clientIdentificationNumber, Long tableIdentificationNumber) {
        if (clients.containsKey(clientIdentificationNumber)) {
            try {
                clients.get(clientIdentificationNumber).takeTable(new Table(getRTIAmbassador(), tableIdentificationNumber));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    InteractionClassHandle getClientImpatienceInteractionClassHandle() {
        return clientImpatienceInteractionClassHandle;
    }

    ParameterHandle getClientImpatienceInteractionClassClientIdParameterHandle() {
        return clientImpatienceInteractionClassClientIdParameterHandle;
    }

    ParameterHandle getClientLeavesTableInteractionClassClientIdParameterHandle() {
        return clientLeavesTableInteractionClassClientIdParameterHandle;
    }

    InteractionClassHandle getClientLeavesTableInteractionClassHandle() {
        return clientLeavesTableInteractionClassHandle;
    }

    InteractionClassHandle getClientTakesTableInteractionClassHandle() {
        return clientTakesTableInteractionClassHandle;
    }

    ParameterHandle getClientTakesTableInteractionClassClientIdParameterHandle() {
        return clientTakesTableInteractionClassClientIdParameterHandle;
    }

    ParameterHandle getClientTakesTableInteractionClassTableIdParameterHandle() {
        return clientTakesTableInteractionClassTableIdParameterHandle;
    }

    InteractionClassHandle getLeaveFromQueueInteractionClassHandle() {
        return leaveFromQueueInteractionClassHandle;
    }

    ParameterHandle getLeaveFromQueueInteractionClassClientIdParameterHandle() {
        return leaveFromQueueInteractionClassClientIdParameterHandle;
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
            (new File("foms/Clients.xml")).toURI().toURL()
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

//        rtiAmbassador.publishObjectClassAttributes(rtiAmbassador.getObjectClassHandle("HLAobjectRoot.Client"), rtiAmbassador.getAttributeHandleSetFactory().create()); // @TODO

        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewClient"));
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
        createNewClient();
    }

    @Override
    protected void subscribe() throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        { // ClientTakesTable
            clientTakesTableInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientTakesTable");

            rtiAmbassador.subscribeInteractionClass(clientTakesTableInteractionClassHandle);

            clientTakesTableInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientTakesTableInteractionClassHandle, "clientId");

            clientTakesTableInteractionClassTableIdParameterHandle = rtiAmbassador.getParameterHandle(clientTakesTableInteractionClassHandle, "tableId");
        }

        { // LeaveFromQueue
            leaveFromQueueInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.LeaveFromQueue");

            rtiAmbassador.subscribeInteractionClass(leaveFromQueueInteractionClassHandle);

            leaveFromQueueInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(leaveFromQueueInteractionClassHandle, "clientId");
        }

        { // ClientImpatience
            clientImpatienceInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.LeaveFromQueue.ClientImpatience");

            rtiAmbassador.subscribeInteractionClass(clientImpatienceInteractionClassHandle);

            clientImpatienceInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientImpatienceInteractionClassHandle, "clientId");
        }

        { // ClientLeavesTable
            clientLeavesTableInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientLeavesTable");

            rtiAmbassador.subscribeInteractionClass(clientLeavesTableInteractionClassHandle);

            clientLeavesTableInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientLeavesTableInteractionClassHandle, "clientId");
        }
    }

    void removeClient(Long clientIdentificationNumber) {
        if (clients.containsKey(clientIdentificationNumber)) {
            try {
                Client client = clients.remove(clientIdentificationNumber);

                client.destruct();

                log("Removed Client " + clientIdentificationNumber);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
