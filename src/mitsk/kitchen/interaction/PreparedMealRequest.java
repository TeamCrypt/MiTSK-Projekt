package mitsk.kitchen.interaction;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger64BE;
import mitsk.AbstractInteraction;
import mitsk.kitchen.object.Client;
import mitsk.kitchen.object.Meal;

public class PreparedMealRequest extends AbstractInteraction {
    private Client client;

    private EncoderFactory encoderFactory;

    private Meal meal;

    private InteractionClassHandle preparedMealRequestInteractionClassHandle;

    private ParameterHandle preparedMealRequestInteractionClassClientIdParameterHandle;

    private ParameterHandle preparedMealRequestInteractionClassMealIdParameterHandle;

    public PreparedMealRequest(RTIambassador rtiAmbassador, Client client, Meal meal) throws Exception {
        super(rtiAmbassador);

        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        this.client = client;

        this.meal = meal;
    }

    @Override
    public void sendInteraction() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        ParameterHandleValueMap parameters = rtiAmbassador.getParameterHandleValueMapFactory().create(1);

        { // clientId
            HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE(client.getIdentificationNumber());

            parameters.put(preparedMealRequestInteractionClassClientIdParameterHandle, clientId.toByteArray());
        }

        { // mealId
            HLAinteger64BE mealId = encoderFactory.createHLAinteger64BE(meal.getIdentificationNumber());

            parameters.put(preparedMealRequestInteractionClassMealIdParameterHandle, mealId.toByteArray());
        }

        rtiAmbassador.sendInteraction(preparedMealRequestInteractionClassHandle, parameters, generateTag());
    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        preparedMealRequestInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.PreparedMealRequest");

        preparedMealRequestInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(preparedMealRequestInteractionClassHandle, "clientId");

        preparedMealRequestInteractionClassMealIdParameterHandle = rtiAmbassador.getParameterHandle(preparedMealRequestInteractionClassHandle, "mealId");
    }
}
