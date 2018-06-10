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
import java.util.List;

public class Federate extends AbstractFederate {
    private static final int NUMBER_OF_TABLES = 3;

    private static final double A = 16.0;

    private static final double B = 32.0;

    private List<Table> tables;

    private List<Client> clientsReceived = new ArrayList<>();

    private int numberOfTables;

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

    InteractionClassHandle getLeaveFromQueueInteractionClassHandle() {
        return leaveFromQueueInteractionClassHandle;
    }

    ParameterHandle getLeaveFromQueueInteractionClassClientIdParameterHandle() {
        return leaveFromQueueInteractionClassClientIdParameterHandle;
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

    void addClientReceived(Long clientId) throws Exception {
        clientsReceived.add(new Client(getRTIAmbassador(), clientId));
    }

    private void clientTakesTable() {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        double freeAfter = randomDouble(A, B);

        List<Client> toRemove = new ArrayList<>();

        for (Client client : clientsReceived) {
            for (Table table : tables) {
                if (table.isFree()) {
                    try {
                        table.setOccupied(client, freeAfter);

                        new ClientTakesTable(rtiAmbassador, client, table).sendInteraction();

                        toRemove.add(client);

                        log("Client " + client.getIdentificationNumber() + " takes table " + table.getTableId());
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

        double federationTime = getFederateAmbassador().getFederateTime();

        for (Table table : tables) {
            if ((table.getFreeAt() <= federationTime) && !table.isFree()) {
                try {
                    ClientLeavesTable clientLeavesTable = new ClientLeavesTable(rtiAmbassador, table);

                    clientLeavesTable.sendInteraction();

                    table.setFree();
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
                    FreeTablesAvailable freeTablesAvailable = new FreeTablesAvailable(getRTIAmbassador());

                    freeTablesAvailable.sendInteraction();

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
    }
}
