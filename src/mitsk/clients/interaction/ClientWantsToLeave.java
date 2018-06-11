package mitsk.clients.interaction;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger64BE;
import mitsk.AbstractInteraction;
import mitsk.clients.object.Client;

public class ClientWantsToLeave extends AbstractInteraction {
    private Client client;

    private InteractionClassHandle clientWantsToLeaveInteractionClassHandle;

    private ParameterHandle clientWantsToLeaveInteractionClassClientIdParameterHandle;

    private EncoderFactory encoderFactory;

    public ClientWantsToLeave(RTIambassador rtiAmbassador, Client client) throws Exception {
        super(rtiAmbassador);

        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        this.client = client;
    }

    @Override
    public void sendInteraction() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        ParameterHandleValueMap parameters = rtiAmbassador.getParameterHandleValueMapFactory().create(1);

        { // clientId
            HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE(client.getIdentificationNumber());

            parameters.put(clientWantsToLeaveInteractionClassClientIdParameterHandle, clientId.toByteArray());
        }

        rtiAmbassador.sendInteraction(clientWantsToLeaveInteractionClassHandle, parameters, generateTag());
    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        clientWantsToLeaveInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientWantsToLeave");

        clientWantsToLeaveInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientWantsToLeaveInteractionClassHandle, "clientId");
    }
}
