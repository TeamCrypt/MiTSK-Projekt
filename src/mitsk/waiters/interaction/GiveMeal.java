package mitsk.waiters.interaction;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger64BE;
import mitsk.AbstractInteraction;
import mitsk.waiters.object.Client;
import mitsk.waiters.object.Meal;

public class GiveMeal extends AbstractInteraction {
    private Client client;

    private Meal meal;

    private InteractionClassHandle giveMealInteractionClassHandle;

    private ParameterHandle giveMealClassClientIdParameterHandle;

    private ParameterHandle giveMealClassMealIdParameterHandle;

    private EncoderFactory encoderFactory;

    public GiveMeal(RTIambassador rtiAmbassador, Client client, Meal meal) throws Exception {
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

        parameters.put(giveMealClassClientIdParameterHandle, clientId.toByteArray());

        HLAinteger64BE mealId = encoderFactory.createHLAinteger64BE(meal.getIdentificationNumber());

        parameters.put(giveMealClassMealIdParameterHandle, mealId.toByteArray());

        rtiAmbassador.sendInteraction(giveMealInteractionClassHandle, parameters, generateTag());
    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        giveMealInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.GiveMeal");

        giveMealClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(giveMealInteractionClassHandle, "clientId");

        giveMealClassMealIdParameterHandle = rtiAmbassador.getParameterHandle(giveMealInteractionClassHandle, "mealId");
    }
}
