package mitsk.waiters.interaction;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger64BE;
import mitsk.AbstractInteraction;
import mitsk.waiters.object.Client;
import mitsk.waiters.object.Meal;

public class TakeFood extends AbstractInteraction {
    private Client client;

    private Meal meal;

    private InteractionClassHandle takeFoodInteractionClassHandle;

    private ParameterHandle takeFoodInteractionClassClientIdParameterHandle;

    private ParameterHandle takeFoodInteractionClassMealIdParameterHandle;

    private EncoderFactory encoderFactory;

    public TakeFood(RTIambassador rtiAmbassador, Client client, Meal meal) throws Exception {
        super(rtiAmbassador);

        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        this.client = client;

        this.meal = meal;
    }

    @Override
    public void sendInteraction() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        ParameterHandleValueMap parameters = rtiAmbassador.getParameterHandleValueMapFactory().create(1);

        HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE(client.getIdentificationNumber());

        parameters.put(takeFoodInteractionClassClientIdParameterHandle, clientId.toByteArray());

        HLAinteger64BE mealId = encoderFactory.createHLAinteger64BE(meal.getIdentificationNumber());

        parameters.put(takeFoodInteractionClassMealIdParameterHandle, mealId.toByteArray());

        rtiAmbassador.sendInteraction(takeFoodInteractionClassHandle, parameters, generateTag());
    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        takeFoodInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.TakeFood");

        takeFoodInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(takeFoodInteractionClassHandle, "clientId");

        takeFoodInteractionClassMealIdParameterHandle = rtiAmbassador.getParameterHandle(takeFoodInteractionClassHandle, "mealId");
    }
}
