package mitsk.waiters;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;
import mitsk.waiters.interaction.StartingClientService;
import mitsk.waiters.object.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Federate extends AbstractFederate {
    private static final int ITERATIONS = 20;

    private static final int NUMBER_OF_WAITERS = 3;

    private int numberOfWaiters = NUMBER_OF_WAITERS;

    protected List<Waiter> waiters;

    private InteractionClassHandle clientCallsWaiterInteractionClassHandle;

    private ParameterHandle clientCallsWaiterInteractionClassClientIdParameterHandle;

    private InteractionClassHandle clientOrdersMealInteractionClassHandle;

    private ParameterHandle clientOrdersMealInteractionClassClientIdParameterHandle;

    private ParameterHandle clientOrdersMealInteractionClassMealIdParameterHandle;

    private InteractionClassHandle preparedMealRequestInteractionClassHandle;

    private ParameterHandle preparedMealRequestInteractionClassClientIdParameterHandle;

    private ParameterHandle preparedMealRequestInteractionClassMealIdParameterHandle;

    private InteractionClassHandle clientAsksForBillInteractionClassHandle;

    private ParameterHandle clientAsksForBillInteractionClassClientIdParameterHandle;

    private InteractionClassHandle endingClientServiceInteractionClassHandle;

    private ParameterHandle endingClientServiceInteractionClassClientIdParameterHandle;

    private List<WaiterRequest> waiterRequests = new ArrayList<>();

    private List<ClientService> clientsOrders = new ArrayList<>();

    public Federate(String federationName) throws Exception {
        this(federationName, NUMBER_OF_WAITERS);
    }

    public Federate(String federationName, int numberOfWaiters) throws Exception {
        super(federationName);

        if (numberOfWaiters <= 0) {
            throw new IllegalArgumentException("Number of waiters must be positive value");
        }

        this.numberOfWaiters = numberOfWaiters;

        waiters = new ArrayList<>(numberOfWaiters);

        for (int i = 0; i < numberOfWaiters; ++i) {
            waiters.add(new Waiter(getRTIAmbassador()));
        }
    }

    public InteractionClassHandle getClientCallsWaiterInteractionClassHandle() {
        return clientCallsWaiterInteractionClassHandle;
    }

    public ParameterHandle getClientCallsWaiterInteractionClassClientIdParameterHandle() {
        return clientCallsWaiterInteractionClassClientIdParameterHandle;
    }

    public InteractionClassHandle getClientOrdersMealInteractionClassHandle() {
        return clientOrdersMealInteractionClassHandle;
    }

    public ParameterHandle getClientOrdersMealInteractionClassClientIdParameterHandle() {
        return clientOrdersMealInteractionClassClientIdParameterHandle;
    }

    public ParameterHandle getClientOrdersMealInteractionClassMealIdParameterHandle() {
        return clientOrdersMealInteractionClassMealIdParameterHandle;
    }

    public InteractionClassHandle getPreparedMealRequestInteractionClassHandle() {
        return preparedMealRequestInteractionClassHandle;
    }

    public ParameterHandle getPreparedMealRequestInteractionClassClientIdParameterHandle() {
        return preparedMealRequestInteractionClassClientIdParameterHandle;
    }

    public ParameterHandle getPreparedMealRequestInteractionClassMealIdParameterHandle() {
        return preparedMealRequestInteractionClassMealIdParameterHandle;
    }

    public InteractionClassHandle getClientAsksForBillInteractionClassHandle() {
        return clientAsksForBillInteractionClassHandle;
    }

    public ParameterHandle getClientAsksForBillInteractionClassClientIdParameterHandle() {
        return clientAsksForBillInteractionClassClientIdParameterHandle;
    }

    public InteractionClassHandle getEndingClientServiceInteractionClassHandle() {
        return endingClientServiceInteractionClassHandle;
    }

    public ParameterHandle getEndingClientServiceInteractionClassClientIdParameterHandle() {
        return endingClientServiceInteractionClassClientIdParameterHandle;
    }

    protected void addWaiterRequest(Long clientId) {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        try {
            waiterRequests.add(new WaiterRequest(rtiAmbassador, new Client(rtiAmbassador, clientId)));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private boolean ifIsFreeWaiter() {
        for (Waiter waiter : waiters) {
            if(waiter.ifFree()) {
                return true;
            }
        }

        return false;
    }

    private Waiter getFirstFreeWaiter() {
        for (Waiter waiter : waiters) {
            if(waiter.ifFree()) {
                return waiter;
            }
        }

        return null;
    }

    protected void informAboutStartedClientServices() {
        List<WaiterRequest> consideredWaiterRequests = new ArrayList<>();

        RTIambassador rtiAmbassador = getRTIAmbassador();

        for (WaiterRequest waiterRequest : waiterRequests) {
            if(ifIsFreeWaiter()) {
                try {
                    Client client = waiterRequest.getClient();

                    Waiter waiter = getFirstFreeWaiter();

                    waiter.setOccupied();

                    ClientService clientOrder = new ClientService(rtiAmbassador, client, waiter);

                    clientsOrders.add(clientOrder);

                    StartingClientService startingClientService = new StartingClientService(rtiAmbassador, client);

                    startingClientService.sendInteraction();

                    consideredWaiterRequests.add(waiterRequest);

                    log("Started client service for client with id " + client.getIdentificationNumber());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (consideredWaiterRequests.size() > 0) {
            waiterRequests.removeAll(consideredWaiterRequests);
        }
    }

    private ClientService findOrder(Long clientId) {
        for (ClientService clientService : clientsOrders) {
            if((!clientService.ifDone()) && (clientService.getClient().getIdentificationNumber() == clientId)) {
                return clientService;
            }
        }

        return null;
    }

    protected void addMealToOrder(Long clientId, Long mealId) {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        ClientService clientService = findOrder(clientId);

        try {
            Meal meal = new Meal(rtiAmbassador, mealId);

            clientService.setMeal(meal);
        } catch (Exception e) {
            e.printStackTrace();
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
            (new File("foms/Waiters.xml")).toURI().toURL(),
            (new File("foms/Kitchen.xml")).toURI().toURL(),
            (new File("foms/Queue.xml")).toURI().toURL(),
            (new File("foms/Statistics.xml")).toURI().toURL(),
        };
    }

    @Override
    protected URL[] getJoinModules() throws MalformedURLException {
        return new URL[]{
            (new File("foms/Waiters.xml")).toURI().toURL()
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

        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.StartingClientService"));
        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewMealRequest"));
        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.TakeFood"));
        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.GiveMeal"));
        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.PaymentService"));
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
        informAboutStartedClientServices();
    }

    @Override
    protected void subscribe() throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        // ClientCallsWaiter
        clientCallsWaiterInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientCallsWaiter");

        rtiAmbassador.subscribeInteractionClass(clientCallsWaiterInteractionClassHandle);

        clientCallsWaiterInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientCallsWaiterInteractionClassHandle, "clientId");

        // ClientOrdersMeal
        clientOrdersMealInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientOrdersMeal");

        rtiAmbassador.subscribeInteractionClass(clientOrdersMealInteractionClassHandle);

        clientOrdersMealInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientOrdersMealInteractionClassHandle, "clientId");

        clientOrdersMealInteractionClassMealIdParameterHandle = rtiAmbassador.getParameterHandle(clientOrdersMealInteractionClassHandle, "mealId");

        // PreparedMealRequest
        preparedMealRequestInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.PreparedMealRequest");

        rtiAmbassador.subscribeInteractionClass(preparedMealRequestInteractionClassHandle);

        preparedMealRequestInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(preparedMealRequestInteractionClassHandle, "clientId");

        preparedMealRequestInteractionClassMealIdParameterHandle = rtiAmbassador.getParameterHandle(preparedMealRequestInteractionClassHandle, "mealId");

        // ClientAsksForBill
        clientAsksForBillInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientAsksForBill");

        rtiAmbassador.subscribeInteractionClass(clientAsksForBillInteractionClassHandle);

        clientAsksForBillInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientAsksForBillInteractionClassHandle, "clientId");

        // EndingClientService
        endingClientServiceInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.EndingClientService");

        rtiAmbassador.subscribeInteractionClass(endingClientServiceInteractionClassHandle);

        endingClientServiceInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(endingClientServiceInteractionClassHandle, "clientId");
    }
}