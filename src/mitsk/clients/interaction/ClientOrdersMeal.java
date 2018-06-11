package mitsk.clients.interaction;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger64BE;
import mitsk.AbstractInteraction;
import mitsk.clients.object.Client;
import mitsk.clients.object.Meal;
import mitsk.clients.object.Waiter;

public class ClientOrdersMeal extends AbstractInteraction {
    private Client client;

    private InteractionClassHandle clientOrdersMealInteractionClassHandle;

    private ParameterHandle clientOrdersMealInteractionClassClientIdParameterHandle;

    private ParameterHandle clientOrdersMealInteractionClassMealIdParameterHandle;

    private ParameterHandle clientOrdersMealInteractionClassWaiterIdParameterHandle;

    private EncoderFactory encoderFactory;

    private Meal meal;

    private Waiter waiter;

    public ClientOrdersMeal(RTIambassador rtiAmbassador, Client client, Waiter waiter, Meal meal) throws Exception {
        super(rtiAmbassador);

        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        this.client = client;

        this.meal = meal;

        this.waiter = waiter;
    }

    public Client getClient() {
        return client;
    }

    public Meal getMeal() {
        return meal;
    }

    public Waiter getWaiter() {
        return waiter;
    }

    @Override
    public void sendInteraction() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        ParameterHandleValueMap parameters = rtiAmbassador.getParameterHandleValueMapFactory().create(3);

        { // clientId
            HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE(client.getIdentificationNumber());

            parameters.put(clientOrdersMealInteractionClassClientIdParameterHandle, clientId.toByteArray());
        }

        { // mealId
            HLAinteger64BE mealId = encoderFactory.createHLAinteger64BE(meal.getIdentificationNumber());

            parameters.put(clientOrdersMealInteractionClassMealIdParameterHandle, mealId.toByteArray());
        }

        { // waiterId
            HLAinteger64BE waiterId = encoderFactory.createHLAinteger64BE(waiter.getIdentificationNumber());

            parameters.put(clientOrdersMealInteractionClassWaiterIdParameterHandle, waiterId.toByteArray());
        }

        rtiAmbassador.sendInteraction(clientOrdersMealInteractionClassHandle, parameters, generateTag());
    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        clientOrdersMealInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientOrdersMeal");

        clientOrdersMealInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientOrdersMealInteractionClassHandle, "clientId");

        clientOrdersMealInteractionClassMealIdParameterHandle = rtiAmbassador.getParameterHandle(clientOrdersMealInteractionClassHandle, "mealId");

        clientOrdersMealInteractionClassWaiterIdParameterHandle = rtiAmbassador.getParameterHandle(clientOrdersMealInteractionClassHandle, "waiterId");
    }
}
