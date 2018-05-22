package mitsk.clients.interaction;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger64BE;
import mitsk.AbstractInteraction;
import mitsk.clients.object.Client;

public class NewClient extends AbstractInteraction {
    private Client client;

    private ParameterHandle newClientInteractionClassClientIdParameterHandle;

    private EncoderFactory encoderFactory;

    private InteractionClassHandle newClientInteractionClassHandle;

    public NewClient(RTIambassador rtiAmbassador, Client client) throws Exception {
        super(rtiAmbassador);

        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        this.client = client;
    }

    @Override
    public void sendInteraction() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        ParameterHandleValueMap parameters = rtiAmbassador.getParameterHandleValueMapFactory().create(1);

        HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE(client.getIdentificationNumber());

        parameters.put(newClientInteractionClassClientIdParameterHandle, clientId.toByteArray());

        rtiAmbassador.sendInteraction(newClientInteractionClassHandle, parameters, generateTag());
    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        newClientInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewClient");

        newClientInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(newClientInteractionClassHandle, "clientId");
    }
}
