package mitsk.tables;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;
import mitsk.tables.interaction.ClientLeavesTable;
import mitsk.tables.interaction.ClientTakesTable;
import mitsk.tables.interaction.FreeTablesAvailable;
import mitsk.tables.object.Client;
import mitsk.tables.object.Table;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Federate extends AbstractFederate {
    private static final int NUMBER_OF_TABLES = 3;

    private static final double A = 16.0;

    private static final double B = 32.0;

    private List<Table> tables;

    private Map<Long, Client> clients = new HashMap<>();

    private List<Client> clientsReceived = new ArrayList<>();

    private int numberOfTables;

    private InteractionClassHandle clientWantsToLeaveInteractionClassHandle;

    private ParameterHandle clientWantsToLeaveInteractionClassClientIdParameterHandle;

    private InteractionClassHandle leaveFromQueueInteractionClassHandle;

    private ParameterHandle leaveFromQueueInteractionClassClientIdParameterHandle;

    public Federate(String federationName) throws Exception {
        this(federationName, NUMBER_OF_TABLES);
    }

    public Federate(String federationName, int numberOfTables) throws Exception {
        super(federationName);

        if (numberOfTables < 0) {
            throw new IllegalArgumentException("Number of tables must be positive value");
        }

        this.numberOfTables = numberOfTables;

        createTablesList();
    }

    void addClient(Long clientIdentificationNumber) throws Exception {
        Client client = new Client(getRTIAmbassador(), clientIdentificationNumber);

        clients.put(client.getIdentificationNumber(), client);

        clientsReceived.add(client);

        log("Client " + client.getIdentificationNumber() + " enters Restaurant");
    }

    private void createTablesList() throws Exception {
        tables = new ArrayList<>(numberOfTables);

        for (int i = 0; i < numberOfTables; ++i) {
            tables.add(i, new Table(getRTIAmbassador()));
        }
    }

    @Override
    protected AbstractFederateAmbassador createAmbassador() throws Exception {
        return new Ambassador(this);
    }

    InteractionClassHandle getClientWantsToLeaveInteractionClassHandle() {
        return clientWantsToLeaveInteractionClassHandle;
    }

    ParameterHandle getClientWantsToLeaveInteractionClassClientIdParameterHandle() {
        return clientWantsToLeaveInteractionClassClientIdParameterHandle;
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
            (new File("foms/Tables.xml")).toURI().toURL()
        };
    }

    InteractionClassHandle getLeaveFromQueueInteractionClassHandle() {
        return leaveFromQueueInteractionClassHandle;
    }

    ParameterHandle getLeaveFromQueueInteractionClassClientIdParameterHandle() {
        return leaveFromQueueInteractionClassClientIdParameterHandle;
    }

    public static void main(String[] args) {
        String federationName = args.length > 0 ? args[0] : "RestaurantFederation";

        try {
            new Federate(federationName).run();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    void nextClientToLeave(Long clientIdentificationNumber) {
        if (clients.containsKey(clientIdentificationNumber)) {
            Client client = clients.get(clientIdentificationNumber);

            client.wantsToLeave();

            log("Client " + client.getIdentificationNumber() + " wants to leave");
        }
    }

    @Override
    protected void publish() throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientTakesTable"));

        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientLeavesTable"));

        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.FreeTablesAvailable"));
    }

    @Override
    protected void resignFederation() throws Exception {
        super.resignFederation();

        for (Table table : tables) {
            table.destruct();
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

    public void sendInteraction() {
        clientLeavesTable();

        freeTablesAvailable();

        clientTakesTable();
    }

    private void clientTakesTable() {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        List<Client> toRemove = new ArrayList<>();

        for (Client client : clientsReceived) {
            for (Table table : tables) {
                if (table.isFree()) {
                    try {
                        table.setOccupied(client);

                        new ClientTakesTable(rtiAmbassador, client, table).sendInteraction();

                        toRemove.add(client);

                        log("Client " + client.getIdentificationNumber() + " takes Table " + table.getIdentificationNumber());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    break;
                }
            }
        }

        if (toRemove.size() > 0) {
            clientsReceived.removeAll(toRemove);
        }
    }

    private void clientLeavesTable() {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        for (Table table : tables) {
            if (!table.isFree() && table.getClient().isWantsToLeave()) {
                try {
                    Client client = table.getClient();

                    ClientLeavesTable clientLeavesTable = new ClientLeavesTable(rtiAmbassador, table);

                    clientLeavesTable.sendInteraction();

                    table.setFree();

                    clients.remove(client.getIdentificationNumber());

                    log("Client " + client.getIdentificationNumber() + " leaves Table " + table.getIdentificationNumber());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private void freeTablesAvailable() {
        for (Table table : tables) {
            if (table.isFree() && !table.isNotified()) {
                try {
                    new FreeTablesAvailable(getRTIAmbassador()).sendInteraction();

                    table.setNotified();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void subscribe() throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        { // LeaveFromQueue
            leaveFromQueueInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.LeaveFromQueue");

            rtiAmbassador.subscribeInteractionClass(leaveFromQueueInteractionClassHandle);

            leaveFromQueueInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(leaveFromQueueInteractionClassHandle, "clientId");
        }

        { // ClientImpatience
            rtiAmbassador.subscribeInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.LeaveFromQueue.ClientImpatience")); // Otherwise RTI throws exception
        }

        { // ClientWantsToLeave
            clientWantsToLeaveInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientWantsToLeave");

            rtiAmbassador.subscribeInteractionClass(clientWantsToLeaveInteractionClassHandle);

            clientWantsToLeaveInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientWantsToLeaveInteractionClassHandle, "clientId");
        }
    }
}
