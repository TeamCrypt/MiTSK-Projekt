package mitsk.queue.interaction;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.encoding.HLAinteger64BE;
import mitsk.queue.object.Client;

public class ClientImpatience extends LeaveFromQueue {
    private ParameterHandle clientImpatienceInteractionClassClientIdParameterHandle;

    private InteractionClassHandle clientImpatienceInteractionClassHandle;

    public ClientImpatience(RTIambassador rtiAmbassador, Client client) throws Exception {
        super(rtiAmbassador, client);
    }

    @Override
    public void sendInteraction() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        ParameterHandleValueMap parameters = rtiAmbassador.getParameterHandleValueMapFactory().create(1);

        HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE(client.getIdentificationNumber());

        parameters.put(clientImpatienceInteractionClassClientIdParameterHandle, clientId.toByteArray());

        rtiAmbassador.sendInteraction(clientImpatienceInteractionClassHandle, parameters, generateTag());
    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        clientImpatienceInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.LeaveFromQueue.ClientImpatience");

        clientImpatienceInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientImpatienceInteractionClassHandle, "clientId");
    }
}
