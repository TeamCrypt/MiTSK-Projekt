package mitsk.statistics;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;
import mitsk.statistics.object.Client;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

public class Federate extends AbstractFederate {
    private static final int ITERATIONS = 20;

    private InteractionClassHandle clientImpatienceInteractionClassHandle;

    private ParameterHandle clientImpatienceInteractionClassClientIdParameterHandle;

    private InteractionClassHandle newInQueueInteractionClassHandle;

    private ParameterHandle newInQueueInteractionClassClientIdParameterHandle;

    private HashMap<Long, Client> clients = new HashMap<>();

    private List<Double> times = new ArrayList<>();

    public Federate(String federationName) throws Exception {
        super(federationName);
    }

    public void addClientToStatistics(Long clientId) throws Exception {
        Client client = new Client(getRTIAmbassador(), clientId, getFederateAmbassador().getFederateTime());

        clients.put(clientId, client);
    }

    @Override
    protected AbstractFederateAmbassador createAmbassador() {
        return new Ambassador(this);
    }

    public InteractionClassHandle getClientImpatienceInteractionClassHandle() {
        return clientImpatienceInteractionClassHandle;
    }

    public ParameterHandle getClientImpatienceInteractionClassClientIdParameterHandle() {
        return clientImpatienceInteractionClassClientIdParameterHandle;
    }

    @Override
    protected URL[] getFederationModules() throws MalformedURLException {
        return new URL[]{
            (new File("foms/Clients.xml")).toURI().toURL(),
            (new File("foms/Queue.xml")).toURI().toURL(),
            (new File("foms/Statistics.xml")).toURI().toURL()
        };
    }

    @Override
    protected URL[] getJoinModules() throws MalformedURLException {
        return new URL[]{
            (new File("foms/Statistics.xml")).toURI().toURL()
        };
    }

    public InteractionClassHandle getNewInQueueInteractionClassHandle() {
        return newInQueueInteractionClassHandle;
    }

    public ParameterHandle getNewInQueueInteractionClassClientIdParameterHandle() {
        return newInQueueInteractionClassClientIdParameterHandle;
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
        // empty
    }


    public void removeImpatientClientFromStatistics(Long clientId) {
        times.add(getFederateAmbassador().getFederateTime() - clients.remove(clientId).getFederateTime());
    }

    @Override
    public void run() throws Exception {
        super.run();

        for (int i = 0; i < ITERATIONS; i++) {
            advanceTime(1.0);

            log("Time Advanced to " + getFederateAmbassador().getFederateTime());
        }

        resignFederation();

        log("Estimate time in queue: " + getEstimateTime());
    }

    private double getEstimateTime() {
        double estimateTime = 0;
        int counter = 0;

        for (double time :
                times) {
            estimateTime += time;

            ++counter;
        }

        if (counter > 0) {
            return estimateTime / counter;
        }

        return estimateTime;
    }

    @Override
    protected void subscribe() throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        { // NewInQueue
            newInQueueInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewInQueue");

            rtiAmbassador.subscribeInteractionClass(newInQueueInteractionClassHandle);

            newInQueueInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(newInQueueInteractionClassHandle, "clientId");
        }

        { // ClientImpatience
            clientImpatienceInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.LeaveFromQueue.ClientImpatience");

            rtiAmbassador.subscribeInteractionClass(clientImpatienceInteractionClassHandle);

            clientImpatienceInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientImpatienceInteractionClassHandle, "clientId");
        }
    }
}
