package mitsk.waiters.interaction;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger64BE;
import mitsk.AbstractInteraction;
import mitsk.waiters.object.Client;
import mitsk.waiters.object.Meal;
import mitsk.waiters.object.Waiter;

public class GiveMeal extends AbstractInteraction {
    private Client client;

    private Meal meal;

    private InteractionClassHandle giveMealInteractionClassHandle;

    private ParameterHandle giveMealClassClientIdParameterHandle;

    private ParameterHandle giveMealClassMealIdParameterHandle;

    private EncoderFactory encoderFactory;

    private Waiter waiter;

    public GiveMeal(RTIambassador rtiAmbassador, Client client, Meal meal, Waiter waiter) throws Exception {
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

        ParameterHandleValueMap parameters = rtiAmbassador.getParameterHandleValueMapFactory().create(2);

        { // clientId
            HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE(client.getIdentificationNumber());

            parameters.put(giveMealClassClientIdParameterHandle, clientId.toByteArray());
        }

        { // mealId
            HLAinteger64BE mealId = encoderFactory.createHLAinteger64BE(meal.getIdentificationNumber());

            parameters.put(giveMealClassMealIdParameterHandle, mealId.toByteArray());
        }

        rtiAmbassador.sendInteraction(giveMealInteractionClassHandle, parameters, generateTag());

        waiter.setFree();
    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        giveMealInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.GiveMeal");

        giveMealClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(giveMealInteractionClassHandle, "clientId");

        giveMealClassMealIdParameterHandle = rtiAmbassador.getParameterHandle(giveMealInteractionClassHandle, "mealId");
    }
}
