package mitsk.clients.interaction;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger64BE;
import mitsk.AbstractInteraction;
import mitsk.clients.object.Client;

public class ClientCallsWaiter extends AbstractInteraction {
    private Client client;

    private InteractionClassHandle clientCallsWaiterInteractionClassHandle;

    private ParameterHandle clientCallsWaiterInteractionClassClientIdParameterHandle;

    private EncoderFactory encoderFactory;

    public ClientCallsWaiter(RTIambassador rtiAmbassador, Client client) throws Exception {
        super(rtiAmbassador);

        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void sendInteraction() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        ParameterHandleValueMap parameters = rtiAmbassador.getParameterHandleValueMapFactory().create(1);

        { // clientId
            HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE(client.getIdentificationNumber());

            parameters.put(clientCallsWaiterInteractionClassClientIdParameterHandle, clientId.toByteArray());
        }

        rtiAmbassador.sendInteraction(clientCallsWaiterInteractionClassHandle, parameters, generateTag());

        client.waiterIsNotified();
    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        clientCallsWaiterInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientCallsWaiter");

        clientCallsWaiterInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientCallsWaiterInteractionClassHandle, "clientId");
    }
}
