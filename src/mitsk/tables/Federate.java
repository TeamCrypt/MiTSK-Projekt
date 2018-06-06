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

    private List<Table> tables;

    private int numberOfTables = NUMBER_OF_TABLES;

    private InteractionClassHandle clientTakesTableInteractionClassHandle;

    private InteractionClassHandle clientLeavesTableInteractionClassHandle;

    private ParameterHandle clientTakesTableInteractionClassClientIdParameterHandle;

    private ParameterHandle clientTakesTableInteractionClassTableIdParameterHandle;

    private ParameterHandle clientLeavesTableInteractionClassClientIdParameterHandle;

    private ParameterHandle clientLeavesTableInteractionClassTableIdParameterHandle;

    public Federate(String federationName) throws Exception {
        super(federationName);

        tables = new ArrayList<>(numberOfTables);
    }

    public Federate(String federationName, int numberOfTables) throws Exception {
        this(federationName);

        if (numberOfTables < 0) {
            throw new IllegalArgumentException("Number of tables must be positive value");
        }

        this.numberOfTables = numberOfTables;
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

        rtiAmbassador.subscribeInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientTakesTable"));

        rtiAmbassador.subscribeInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientLeavesTable"));
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
