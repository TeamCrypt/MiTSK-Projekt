package mitsk.clients;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;
import mitsk.clients.interaction.NewClient;
import mitsk.clients.object.Client;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

public class Federate extends AbstractFederate {
    private static final double A = 1.0;

    private static final double B = 10.0;

    private static final int ITERATIONS = 20;

    private HashMap<Long, Client> clients = new HashMap<>();

    private Random random = new Random();

    public Federate(String federationName) throws Exception {
        super(federationName);
    }

    @Override
    protected AbstractFederateAmbassador createAmbassador() {
        return new Ambassador(this);
    }

    private void createNewClient() {
        try {
            RTIambassador rtiAmbassador = getRTIAmbassador();

            Client client = new Client(rtiAmbassador);

            new NewClient(rtiAmbassador, client).sendInteraction();

            clients.put(client.getIdentificationNumber(), client);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected URL[] getFederationModules() throws MalformedURLException {
        return new URL[]{
            (new File("foms/Clients.xml")).toURI().toURL(),
            (new File("foms/Waiters.xml")).toURI().toURL(),
            (new File("foms/Kitchen.xml")).toURI().toURL(),
            (new File("foms/Queue.xml")).toURI().toURL(),
            (new File("foms/Statistics.xml")).toURI().toURL()
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

    private double randomDouble(double a, double b) { // Generates random double in range [a, b]
        double value = (random.nextDouble() * (b - a)) + a;

        return Math.round(value);
    }

    @Override
    public void run() throws Exception {
        super.run();

        for (int i = 0; i < ITERATIONS; i++) {
            sendInteraction();

            advanceTime(randomDouble(A, B));

            log("Time Advanced to " + getFederateAmbassador().getFederateTime());
        }

        resignFederation();
    }

    private void sendInteraction() {
        createNewClient();
    }

    @Override
    protected void subscribe() throws Exception {
        // empty
    }
}
