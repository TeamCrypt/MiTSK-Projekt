package mitsk.clients;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;
import mitsk.clients.interaction.ClientCallsWaiter;
import mitsk.clients.interaction.ClientOrdersMeal;
import mitsk.clients.interaction.ClientWantsToLeave;
import mitsk.clients.interaction.NewClient;
import mitsk.clients.object.Client;
import mitsk.clients.object.Meal;
import mitsk.clients.object.Table;
import mitsk.clients.object.Waiter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private InteractionClassHandle giveMealInteractionClassHandle;

    private ParameterHandle giveMealInteractionClassClientIdParameterHandle;

    private ParameterHandle giveMealInteractionClassMealIdParameterHandle;

    private InteractionClassHandle leaveFromQueueInteractionClassHandle;

    private ParameterHandle leaveFromQueueInteractionClassClientIdParameterHandle;

    private List<ClientOrdersMeal> mealsOrders = new ArrayList<>();

    private double nextClientAt = 0.0;

    private InteractionClassHandle startingClientServiceInteractionClassHandle;

    private ParameterHandle startingClientServiceInteractionClassClientIdParameterHandle;

    private ParameterHandle startingClientServiceInteractionClassWaiterIdParameterHandle;

    public Federate(String federationName) throws Exception {
        super(federationName);
    }

    private void callWaiter() {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        for (Client client : clients.values()) {
            if (client.isWaitingForWaiter()) {
                try {
                    new ClientCallsWaiter(rtiAmbassador, client).sendInteraction();

                    log("Client " + client.getIdentificationNumber() + " calls for Waiter");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @Override
    protected AbstractFederateAmbassador createAmbassador() {
        return new Ambassador(this);
    }

    private void createNewClient() {
        if (nextClientAt <= getFederateAmbassador().getFederateTime()) {
            try {
                RTIambassador rtiAmbassador = getRTIAmbassador();

                Client client = new Client(rtiAmbassador, (int) randomDouble(1.0, 2.0));

                new NewClient(rtiAmbassador, client).sendInteraction();

                clients.put(client.getIdentificationNumber(), client);

                log("New Client " + client.getIdentificationNumber() + " wants to eat " + client.getMealsToOrder() + " meals");

                nextClientAt += randomDouble(A, B);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    void clientTakesTable(Long clientIdentificationNumber, Long tableIdentificationNumber) {
        if (clients.containsKey(clientIdentificationNumber)) {
            try {
                RTIambassador rtiAmbassador = getRTIAmbassador();

                Client client = clients.get(clientIdentificationNumber);

                Table table = new Table(rtiAmbassador, tableIdentificationNumber);

                client.takeTable(table);

                log("Client " + client.getIdentificationNumber() + " takes Table " + table.getIdentificationNumber());
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

    InteractionClassHandle getGiveMealInteractionClassHandle() {
        return giveMealInteractionClassHandle;
    }

    ParameterHandle getGiveMealInteractionClassClientIdParameterHandle() {
        return giveMealInteractionClassClientIdParameterHandle;
    }

    ParameterHandle getGiveMealInteractionClassMealIdParameterHandle() {
        return giveMealInteractionClassMealIdParameterHandle;
    }

    InteractionClassHandle getLeaveFromQueueInteractionClassHandle() {
        return leaveFromQueueInteractionClassHandle;
    }

    ParameterHandle getLeaveFromQueueInteractionClassClientIdParameterHandle() {
        return leaveFromQueueInteractionClassClientIdParameterHandle;
    }

    InteractionClassHandle getStartingClientServiceInteractionClassHandle() {
        return startingClientServiceInteractionClassHandle;
    }

    ParameterHandle getStartingClientServiceInteractionClassClientIdParameterHandle() {
        return startingClientServiceInteractionClassClientIdParameterHandle;
    }

    ParameterHandle getStartingClientServiceInteractionClassWaiterIdParameterHandle() {
        return startingClientServiceInteractionClassWaiterIdParameterHandle;
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

    void giveMealToClient(Long clientIdentificationNumber, Long mealIdentificationNumber) {
        if (clients.containsKey(clientIdentificationNumber)) {
            try {
                Client client = clients.get(clientIdentificationNumber);

                Meal meal = new Meal(getRTIAmbassador(), mealIdentificationNumber);

                client.giveMeal(meal, getFederateAmbassador().getFederateTime() + randomDouble(A, B));

                log("Client " + client.getIdentificationNumber() + " gets " + meal.getName());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
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

    private void orderMeals() {
        List<ClientOrdersMeal> toRemove = new ArrayList<>();

        for (ClientOrdersMeal mealOrder : mealsOrders) {
            try {
                mealOrder.sendInteraction();

                toRemove.add(mealOrder);

                Client client = mealOrder.getClient();

                log("Client " + client.getIdentificationNumber() + " orders " + mealOrder.getMeal().getName() + " to Waiter " + mealOrder.getWaiter().getIdentificationNumber() + " and it is " + client.getMealsOrdered() + ". ordered meal");
            } catch (Exception exception) {
                exception.printStackTrace();

            }
        }

        if (toRemove.size() > 0) {
            mealsOrders.removeAll(toRemove);
        }
    }

    @Override
    protected void publish() throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();

//        rtiAmbassador.publishObjectClassAttributes(rtiAmbassador.getObjectClassHandle("HLAobjectRoot.Client"), rtiAmbassador.getAttributeHandleSetFactory().create()); // @TODO

        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewClient"));

        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientCallsWaiter"));

        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientOrdersMeal"));

        rtiAmbassador.publishInteractionClass(rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientWantsToLeave"));
    }

    private Meal randomMeal() throws Exception {
        Long[] allowedMeals = Meal.getAllowedMealIds();

        return new Meal(getRTIAmbassador(), allowedMeals[(int) randomDouble(0, allowedMeals.length - 1)]);
    }

    private void removeFreeClients() {
        double federateTime = getFederateAmbassador().getFederateTime();

        for (Client client : clients.values()) {
            if (client.isEating() && (client.getFreeAt() <= federateTime)) {
                client.letHimFree();

                if (!client.isWantToLeave()) {
                    log("Client " + client.getIdentificationNumber() + " wants to order next meal");
                }
            }
        }

        RTIambassador rtiAmbassador = getRTIAmbassador();

        for (Client client : clients.values()) {
            if (client.isWantToLeave()) {
                try {
                    new ClientWantsToLeave(rtiAmbassador, client).sendInteraction();

                    client.readyToLeave();

                    log("Client " + client.getIdentificationNumber() + " wants to leave");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
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

    private void sendInteraction() {
        createNewClient();

        callWaiter();

        orderMeals();

        removeFreeClients();
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

        { // StartingClientService
            startingClientServiceInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.StartingClientService");

            rtiAmbassador.subscribeInteractionClass(startingClientServiceInteractionClassHandle);

            startingClientServiceInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(startingClientServiceInteractionClassHandle, "clientId");

            startingClientServiceInteractionClassWaiterIdParameterHandle = rtiAmbassador.getParameterHandle(startingClientServiceInteractionClassHandle, "waiterId");
        }

        { // GiveMeal
            giveMealInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.GiveMeal");

            rtiAmbassador.subscribeInteractionClass(giveMealInteractionClassHandle);

            giveMealInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(giveMealInteractionClassHandle, "clientId");

            giveMealInteractionClassMealIdParameterHandle = rtiAmbassador.getParameterHandle(giveMealInteractionClassHandle, "mealId");
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

    void orderMeal(Long clientIdentificationNumber, Long waiterIdentificationNumber) {
        if (clients.containsKey(clientIdentificationNumber)) {
            Client client = clients.get(clientIdentificationNumber);

            if (client.isWaitingForService()) {
                try {
                    RTIambassador rtiAmbassador = getRTIAmbassador();

                    Waiter waiter = new Waiter(rtiAmbassador, waiterIdentificationNumber);

                    Meal meal = randomMeal();

                    mealsOrders.add(new ClientOrdersMeal(rtiAmbassador, client, waiter, meal));

                    client.mealOrdered();

                    log("Client " + client.getIdentificationNumber() + "  wants to order " + meal.getName() + " to Waiter " + waiter.getIdentificationNumber());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
