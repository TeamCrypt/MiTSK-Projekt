package mitsk.waiters.interaction;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger64BE;
import mitsk.AbstractInteraction;
import mitsk.waiters.object.Client;
import mitsk.waiters.object.Waiter;

public class StartingClientService extends AbstractInteraction {
    private Client client;

    private InteractionClassHandle startingClientServiceInteractionClassHandle;

    private ParameterHandle startingClientServiceInteractionClassClientIdParameterHandle;

    private ParameterHandle startingClientServiceInteractionClassWaiterIdParameterHandle;

    private EncoderFactory encoderFactory;

    private Waiter waiter;

    public StartingClientService(RTIambassador rtiAmbassador, Client client, Waiter waiter) throws Exception {
        super(rtiAmbassador);

        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        this.client = client;

        this.waiter = waiter;
    }

    @Override
    public void sendInteraction() throws Exception {
        waiter.setBusy(client);

        RTIambassador rtiAmbassador = getRtiAmbassador();

        ParameterHandleValueMap parameters = rtiAmbassador.getParameterHandleValueMapFactory().create(2);

        { // clientId
            HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE(client.getIdentificationNumber());

            parameters.put(startingClientServiceInteractionClassClientIdParameterHandle, clientId.toByteArray());
        }

        { // waiterId
            HLAinteger64BE waiterId = encoderFactory.createHLAinteger64BE(waiter.getIdentificationNumber());

            parameters.put(startingClientServiceInteractionClassWaiterIdParameterHandle, waiterId.toByteArray());
        }

        rtiAmbassador.sendInteraction(startingClientServiceInteractionClassHandle, parameters, generateTag());
    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        startingClientServiceInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.StartingClientService");

        startingClientServiceInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(startingClientServiceInteractionClassHandle, "clientId");

        startingClientServiceInteractionClassWaiterIdParameterHandle = rtiAmbassador.getParameterHandle(startingClientServiceInteractionClassHandle, "waiterId");
    }
}
