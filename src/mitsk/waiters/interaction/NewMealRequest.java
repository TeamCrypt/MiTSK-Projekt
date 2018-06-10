package mitsk.waiters.interaction;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger64BE;
import mitsk.AbstractInteraction;
import mitsk.waiters.object.Client;
import mitsk.waiters.object.Meal;

public class NewMealRequest extends AbstractInteraction {
    private Client client;

    private Meal meal;

    private InteractionClassHandle newMealRequestInteractionClassHandle;

    private ParameterHandle newMealRequestInteractionClassClientIdParameterHandle;

    private ParameterHandle newMealRequestInteractionClassMealIdParameterHandle;

    private EncoderFactory encoderFactory;

    public NewMealRequest(RTIambassador rtiAmbassador, Client client, Meal meal) throws Exception {
        super(rtiAmbassador);

        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        this.client = client;

        this.meal = meal;
    }

    @Override
    public void sendInteraction() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        ParameterHandleValueMap parameters = rtiAmbassador.getParameterHandleValueMapFactory().create(2);

        HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE(client.getIdentificationNumber());

        parameters.put(newMealRequestInteractionClassClientIdParameterHandle, clientId.toByteArray());

        HLAinteger64BE mealId = encoderFactory.createHLAinteger64BE(meal.getIdentificationNumber());

        parameters.put(newMealRequestInteractionClassMealIdParameterHandle, mealId.toByteArray());

        rtiAmbassador.sendInteraction(newMealRequestInteractionClassHandle, parameters, generateTag());
    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        newMealRequestInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewMealRequest");

        newMealRequestInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(newMealRequestInteractionClassHandle, "clientId");

        newMealRequestInteractionClassMealIdParameterHandle = rtiAmbassador.getParameterHandle(newMealRequestInteractionClassHandle, "mealId");
    }
}
