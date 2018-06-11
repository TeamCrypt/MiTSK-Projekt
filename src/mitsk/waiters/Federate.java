package mitsk.waiters;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;
import mitsk.waiters.interaction.GiveMeal;
import mitsk.waiters.interaction.NewMealRequest;
import mitsk.waiters.interaction.StartingClientService;
import mitsk.waiters.interaction.TakeFood;
import mitsk.waiters.object.Client;
import mitsk.waiters.object.Meal;
import mitsk.waiters.object.Waiter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Federate extends AbstractFederate {
    private static final int NUMBER_OF_WAITERS = 3;

    private HashMap<Long, Client> clients = new HashMap<>();

    private List<Client> clientsToServe = new ArrayList<>();

    private List<NewMealRequest> mealsRequests = new ArrayList<>();

    private List<GiveMeal> mealsToGive = new ArrayList<>();

    private int numberOfWaiters;

    private InteractionClassHandle clientCallsWaiterInteractionClassHandle;

    private ParameterHandle clientCallsWaiterInteractionClassClientIdParameterHandle;

    private InteractionClassHandle clientOrdersMealInteractionClassHandle;

    private ParameterHandle clientOrdersMealInteractionClassClientIdParameterHandle;

    private ParameterHandle clientOrdersMealInteractionClassMealIdParameterHandle;

    private ParameterHandle clientOrdersMealInteractionClassWaiterIdParameterHandle;

    private InteractionClassHandle preparedMealRequestInteractionClassHandle;

    private ParameterHandle preparedMealRequestInteractionClassClientIdParameterHandle;

    private ParameterHandle preparedMealRequestInteractionClassMealIdParameterHandle;

    private InteractionClassHandle clientAsksForBillInteractionClassHandle;

    private ParameterHandle clientAsksForBillInteractionClassClientIdParameterHandle;

    private InteractionClassHandle endingClientServiceInteractionClassHandle;

    private ParameterHandle endingClientServiceInteractionClassClientIdParameterHandle;

    private List<TakeFood> readyToTake = new ArrayList<>();

    private Map<Long, Waiter> waiters;

    public Federate(String federationName) throws Exception {
        this(federationName, NUMBER_OF_WAITERS);
    }

    public Federate(String federationName, int numberOfWaiters) throws Exception {
        super(federationName);

        if (numberOfWaiters <= 0) {
            throw new IllegalArgumentException("Number of waiters must be positive value");
        }

        this.numberOfWaiters = numberOfWaiters;

        createWaitersList();
    }

    void addClientsCall(Long clientIdentificationNumber) throws Exception {
        Client client = getClient(clientIdentificationNumber);

        clientsToServe.add(client);

        log("Client " + client.getIdentificationNumber() + " is waiting for free Waiter");
    }

    @Override
    protected AbstractFederateAmbassador createAmbassador() throws Exception {
        return new Ambassador(this);
    }

    private void createWaitersList() throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        waiters = new HashMap<>(numberOfWaiters);

        for (int i = 0; i < numberOfWaiters; ++i) {
            Waiter waiter = new Waiter(rtiAmbassador);
            waiters.put(waiter.getIdentificationNumber(), waiter);
        }
    }

    private Client getClient(Long clientIdentificationNumber) throws Exception {
        if (!clients.containsKey(clientIdentificationNumber)) {
            Client client = new Client(getRTIAmbassador(), clientIdentificationNumber);

            clients.put(clientIdentificationNumber, client);
        }

        return clients.get(clientIdentificationNumber);
    }

    InteractionClassHandle getClientCallsWaiterInteractionClassHandle() {
        return clientCallsWaiterInteractionClassHandle;
    }

    ParameterHandle getClientCallsWaiterInteractionClassClientIdParameterHandle() {
        return clientCallsWaiterInteractionClassClientIdParameterHandle;
    }

    InteractionClassHandle getClientOrdersMealInteractionClassHandle() {
        return clientOrdersMealInteractionClassHandle;
    }

    ParameterHandle getClientOrdersMealInteractionClassClientIdParameterHandle() {
        return clientOrdersMealInteractionClassClientIdParameterHandle;
    }

    ParameterHandle getClientOrdersMealInteractionClassMealIdParameterHandle() {
        return clientOrdersMealInteractionClassMealIdParameterHandle;
    }

    ParameterHandle getClientOrdersMealInteractionClassWaiterIdParameterHandle() {
        return clientOrdersMealInteractionClassWaiterIdParameterHandle;
    }

    InteractionClassHandle getPreparedMealRequestInteractionClassHandle() {
        return preparedMealRequestInteractionClassHandle;
    }

    ParameterHandle getPreparedMealRequestInteractionClassClientIdParameterHandle() {
        return preparedMealRequestInteractionClassClientIdParameterHandle;
    }

    ParameterHandle getPreparedMealRequestInteractionClassMealIdParameterHandle() {
        return preparedMealRequestInteractionClassMealIdParameterHandle;
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
            (new File("foms/Waiters.xml")).toURI().toURL()
        };
    }

    private void giveFood() {
        List<GiveMeal> toRemove = new ArrayList<>();

        for (GiveMeal giveMeal : mealsToGive) {
            try {
                giveMeal.sendInteraction();

                toRemove.add(giveMeal);

                log("Waiter " + giveMeal.getWaiter().getIdentificationNumber() + " gives " + giveMeal.getMeal().getName() + " to Client " + giveMeal.getClient().getIdentificationNumber());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        if (toRemove.size() > 0) {
            mealsToGive.removeAll(toRemove);
        }
    }

    public static void main(String[] args) {
        String federationName = args.length > 0 ? args[0] : "RestaurantFederation";

        try {
            new Federate(federationName).run();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    void orderMealForClient(Long clientIdentificationNumber, Long mealIdentificationNumber, Long waiterIdentificationNumber) throws Exception {
        if (waiters.containsKey(waiterIdentificationNumber)) {
            RTIambassador rtiAmbassador = getRTIAmbassador();

            Client client = getClient(clientIdentificationNumber);

            Meal meal = new Meal(rtiAmbassador, mealIdentificationNumber);

            mealsRequests.add(new NewMealRequest(rtiAmbassador, client, meal, waiters.get(waiterIdentificationNumber)));

            log("Client " + client.getIdentificationNumber() + " orders " + meal.getName() + " and his current bill " + client.getBill());
        }
    }

    private void orderMeals() {
        List<NewMealRequest> toRemove = new ArrayList<>();

        for (NewMealRequest mealRequest : mealsRequests) {
            try {
                mealRequest.sendInteraction();

                toRemove.add(mealRequest);

                log("Ordered " + mealRequest.getMeal().getName() + " for Client " + mealRequest.getClient().getIdentificationNumber() + " by Waiter " + mealRequest.getWaiter().getIdentificationNumber());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        if (toRemove.size() > 0) {
            mealsRequests.removeAll(toRemove);
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
        serveClients();

        orderMeals();

        takeFood();

        giveFood();

//        informAboutStartedClientServices();
//
//        informAboutNewMealRequests();
//
//        informAboutTakenFood();
//
//        informAboutGavenMeals();
//
//        informAboutStartedPaymentServices();
    }

    private void serveClients() {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        for (Waiter waiter : waiters.values()) {
            if (waiter.isFree() && (clientsToServe.size() > 0)) {
                try {
                    Client client = clientsToServe.remove(0); // pop

                    new StartingClientService(rtiAmbassador, client, waiter).sendInteraction();

                    log("Starting service for Client " + client.getIdentificationNumber() + " by Waiter " + waiter.getIdentificationNumber());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

    }

    @Override
    protected void subscribe() throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        { // ClientCallsWaiter
            clientCallsWaiterInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientCallsWaiter");

            rtiAmbassador.subscribeInteractionClass(clientCallsWaiterInteractionClassHandle);

            clientCallsWaiterInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientCallsWaiterInteractionClassHandle, "clientId");
        }

        { // ClientOrdersMeal
            clientOrdersMealInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientOrdersMeal");

            rtiAmbassador.subscribeInteractionClass(clientOrdersMealInteractionClassHandle);

            clientOrdersMealInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientOrdersMealInteractionClassHandle, "clientId");

            clientOrdersMealInteractionClassMealIdParameterHandle = rtiAmbassador.getParameterHandle(clientOrdersMealInteractionClassHandle, "mealId");

            clientOrdersMealInteractionClassWaiterIdParameterHandle = rtiAmbassador.getParameterHandle(clientOrdersMealInteractionClassHandle, "waiterId");
        }

        { // PreparedMealRequest
            preparedMealRequestInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.PreparedMealRequest");

            rtiAmbassador.subscribeInteractionClass(preparedMealRequestInteractionClassHandle);

            preparedMealRequestInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(preparedMealRequestInteractionClassHandle, "clientId");

            preparedMealRequestInteractionClassMealIdParameterHandle = rtiAmbassador.getParameterHandle(preparedMealRequestInteractionClassHandle, "mealId");
        }

        { // ClientAsksForBill
            clientAsksForBillInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientAsksForBill");

            rtiAmbassador.subscribeInteractionClass(clientAsksForBillInteractionClassHandle);

            clientAsksForBillInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientAsksForBillInteractionClassHandle, "clientId");
        }

        { // EndingClientService
            endingClientServiceInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.EndingClientService");

            rtiAmbassador.subscribeInteractionClass(endingClientServiceInteractionClassHandle);

            endingClientServiceInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(endingClientServiceInteractionClassHandle, "clientId");
        }
    }

    private void takeFood() {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        for (Waiter waiter : waiters.values()) {
            if (waiter.isFree() && (readyToTake.size() > 0)) {
                try {
                    TakeFood takeFood = readyToTake.remove(0); // pop

                    Client client = takeFood.getClient();

                    waiter.setBusy(client);

                    takeFood.sendInteraction();

                    Meal meal = takeFood.getMeal();

                    mealsToGive.add(new GiveMeal(rtiAmbassador, client, meal, waiter));

                    log("Take " + meal.getName() + " for Client " + client.getIdentificationNumber() + " by Waiter ");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    void takeMealRequest(Long clientIdentificationNumber, Long mealIdentificationNumber) throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        Client client = getClient(clientIdentificationNumber);

        Meal meal = new Meal(rtiAmbassador, mealIdentificationNumber);

        readyToTake.add(new TakeFood(rtiAmbassador, client, meal));

        log("Ready to take " + meal.getName() + " for Client " + client.getIdentificationNumber());
    }
}
