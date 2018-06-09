package mitsk.tables;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;
import mitsk.tables.object.Client;
import mitsk.tables.object.Table;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Federate extends AbstractFederate {
    private static final int ITERATIONS = 20;

    private static final int NUMBER_OF_TABLES = 3;

    protected List<Table> tables;

    private int numberOfTables = NUMBER_OF_TABLES;

    private InteractionClassHandle clientTakesTableInteractionClassHandle;

    private InteractionClassHandle clientLeavesTableInteractionClassHandle;

    private ParameterHandle clientTakesTableInteractionClassClientIdParameterHandle;

    private ParameterHandle clientTakesTableInteractionClassTableIdParameterHandle;

    private ParameterHandle clientLeavesTableInteractionClassClientIdParameterHandle;

    private ParameterHandle clientLeavesTableInteractionClassTableIdParameterHandle;

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

    InteractionClassHandle getClientTakesTableInteractionClassHandle() {
        return clientTakesTableInteractionClassHandle;
    }

    ParameterHandle getClientTakesTableInteractionClassClientIdParameterHandle() {
        return clientTakesTableInteractionClassTableIdParameterHandle;
    }

    ParameterHandle getClientTakesTableInteractionClassTableIdParameterHandle() {
        return clientTakesTableInteractionClassTableIdParameterHandle;
    }

    InteractionClassHandle getClientLeavesTableInteractionClassHandle() {
        return clientLeavesTableInteractionClassHandle;
    }

    ParameterHandle getClientLeavesTableInteractionClassClientIdParameterHandle() {
        return clientLeavesTableInteractionClassClientIdParameterHandle;
    }

    ParameterHandle getClientLeavesTableInteractionClassTableIdParameterHandle() {
        return clientLeavesTableInteractionClassTableIdParameterHandle;
    }

    InteractionClassHandle getLeaveFromQueueInteractionClassHandle() {
        return  leaveFromQueueInteractionClassHandle;
    }

    ParameterHandle getLeaveFromQueueInteractionClassClientIdParameterHandle() {
        return getClientLeavesTableInteractionClassClientIdParameterHandle();
    }

    private void createTablesList() throws Exception {
        tables = new ArrayList<>(numberOfTables);

        for (int i = 0; i < numberOfTables; ++i) {
            tables.add(i, new Table(getRTIAmbassador()));
        }
    }

    void clientTakesTable(Table table, Client client) {
        table.setOccupied(client);
    }

    void clientLeavesTable(Table table) {
        table.setFree();
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

        // ClientTakesTable
        clientTakesTableInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientTakesTable");

        rtiAmbassador.publishInteractionClass(clientTakesTableInteractionClassHandle);

        clientTakesTableInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientTakesTableInteractionClassHandle, "clientId");

        clientTakesTableInteractionClassTableIdParameterHandle = rtiAmbassador.getParameterHandle(clientTakesTableInteractionClassHandle, "tableId");

        // ClientLeavesTable
        clientLeavesTableInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientLeavesTable");

        rtiAmbassador.publishInteractionClass(clientLeavesTableInteractionClassHandle);

        clientLeavesTableInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientTakesTableInteractionClassHandle, "clientId");

        clientLeavesTableInteractionClassTableIdParameterHandle = rtiAmbassador.getParameterHandle(clientTakesTableInteractionClassHandle, "tableId");

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

    }

    public void assignClientToEmptyTable(Long clientId) throws Exception {
        Client client = new Client(getRTIAmbassador(), clientId);

        for (Table table : tables) {
            if (table.isFree()) {
                table.setOccupied(client);
                return;
            }
        }
    }


    @Override
    protected void subscribe() throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        leaveFromQueueInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.LeaveFromQueue");

        rtiAmbassador.subscribeInteractionClass(leaveFromQueueInteractionClassHandle);

        leaveFromQueueInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(leaveFromQueueInteractionClassHandle, "clientId");

    }
}
