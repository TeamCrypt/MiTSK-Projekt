package mitsk.queue;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;
import mitsk.queue.interaction.ClientImpatience;
import mitsk.queue.interaction.LeaveFromQueue;
import mitsk.queue.interaction.NewInQueue;
import mitsk.queue.object.Client;
import mitsk.tables.interaction.FreeTablesAvailable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Federate extends AbstractFederate {
    private static final double A = 1.0;

    private static final double B = 10.0;

    private static final int ITERATIONS = 20;

    private InteractionClassHandle newClientInteractionClassHandle;

    private ParameterHandle newClientInteractionClassClientIdParameterHandle;

    private InteractionClassHandle freeTablesAvailableInteractionClassHandle;

    private InteractionClassHandle leaveFromQueueInteractionClassHandle;

    private ParameterHandle leaveFromQueueInteractionClassClientIdParameterHandle;

    private List<Client> newInQueue = new ArrayList<>();

    private List<Client> queue = new ArrayList<>();

    private Random random = new Random();

    protected boolean permissionToSendClient = false;

    public Federate(String federationName) throws Exception {
        super(federationName);
    }

    private void addClientsToQueue() {
        List<Client> toRemove = new ArrayList<>();

        for (Client client : newInQueue) {
            try {
                NewInQueue newInQueue = new NewInQueue(getRTIAmbassador(), client);

                newInQueue.sendInteraction();

                toRemove.add(client);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        if (toRemove.size() > 0) {
            newInQueue.removeAll(toRemove);
        }
    }

    void addClientToQueue(Long clientId) throws Exception {
        Client client = new Client(getRTIAmbassador(), clientId, randomDouble(A, B));

        queue.add(client);

        newInQueue.add(client);
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
            (new File("foms/Statistics.xml")).toURI().toURL(),
            (new File("foms/Tables.xml")).toURI().toURL()
        };
    }

    @Override
    protected URL[] getJoinModules() throws MalformedURLException {
        return new URL[]{
            (new File("foms/Queue.xml")).toURI().toURL()
        };
    }

    InteractionClassHandle getNewClientInteractionClassHandle() {
        return newClientInteractionClassHandle;
    }

    ParameterHandle getNewClientInteractionClassClientIdParameterHandle() {
        return newClientInteractionClassClientIdParameterHandle;
    }

    InteractionClassHandle getFreeTablesAvailableInteractionClassHandle() {
        return freeTablesAvailableInteractionClassHandle;
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

        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewInQueue"));

        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.LeaveFromQueue.ClientImpatience"));

        leaveFromQueueInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.LeaveFromQueue");

        rtiAmbassador.publishInteractionClass(leaveFromQueueInteractionClassHandle);

        leaveFromQueueInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(leaveFromQueueInteractionClassHandle, "clientId");

    }

    private double randomDouble(double a, double b) { // Generates random double in range [a, b]
        double value = (random.nextDouble() * (b - a)) + a;

        return Math.round(value);
    }

    private void removeImpatientClients() {
        List<Client> toRemove = new ArrayList<>();

        for (Client client : queue) {
            if (client.getImpatience() <= getFederateAmbassador().getFederateTime()) {
                try {
                    ClientImpatience clientImpatience = new ClientImpatience(getRTIAmbassador(), client);

                    clientImpatience.sendInteraction();

                    toRemove.add(client);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        if (toRemove.size() > 0) {
            queue.removeAll(toRemove);
        }
    }

    private void leaveFromQueue() {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        if (permissionToSendClient && !queue.isEmpty()) {
            try {
                Client client = queue.remove(0);

                LeaveFromQueue leaveFromQueue = new LeaveFromQueue(rtiAmbassador, client);

                leaveFromQueue.sendInteraction();
            } catch (Exception e) {
                e.printStackTrace();
            }
            permissionToSendClient = false;
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

    private void sendInteraction() throws Exception {
        addClientsToQueue();

        removeImpatientClients();

        leaveFromQueue();
        }

    @Override
    protected void subscribe() throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        newClientInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewClient");

        rtiAmbassador.subscribeInteractionClass(newClientInteractionClassHandle);

        newClientInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(newClientInteractionClassHandle, "clientId");

        freeTablesAvailableInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.FreeTablesAvailable");

        rtiAmbassador.subscribeInteractionClass(freeTablesAvailableInteractionClassHandle);
    }
}
