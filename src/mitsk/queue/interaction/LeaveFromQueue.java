package mitsk.queue.interaction;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger64BE;
import mitsk.AbstractInteraction;
import mitsk.queue.object.Client;

public class LeaveFromQueue extends AbstractInteraction {
    private Client client;

    private ParameterHandle leaveFromQueueInteractionClassClientIdParameterHandle;

    private EncoderFactory encoderFactory;

    private InteractionClassHandle leaveFromQueueInteractionClassHandle;

    public LeaveFromQueue(RTIambassador rtiAmbassador, Client client) throws Exception {
        super(rtiAmbassador);

        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        this.client = client;
    }

    @Override
    public void sendInteraction() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        ParameterHandleValueMap parameters = rtiAmbassador.getParameterHandleValueMapFactory().create(1);

        HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE(client.getIdentificationNumber());

        parameters.put(leaveFromQueueInteractionClassClientIdParameterHandle, clientId.toByteArray());

        rtiAmbassador.sendInteraction(leaveFromQueueInteractionClassHandle, parameters, generateTag());
    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        leaveFromQueueInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.LeaveFromQueue");

        leaveFromQueueInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(leaveFromQueueInteractionClassHandle, "clientId");
    }
}
