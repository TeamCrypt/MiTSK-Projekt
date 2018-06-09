package mitsk.waiters;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger64BE;
import hla.rti1516e.exceptions.FederateInternalError;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;

public class Ambassador extends AbstractFederateAmbassador {
    private EncoderFactory encoderFactory;

    protected Ambassador(AbstractFederate federate) {
        super(federate);

        encoderFactory = getFederate().getEncoderFactory();
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters, byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime, OrderType receivedOrdering, MessageRetractionHandle retractionHandle, SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        super.receiveInteraction(interactionClass, theParameters, userSuppliedTag, sentOrdering, theTransport, theTime, receivedOrdering, retractionHandle, receiveInfo);

        Federate federate = (Federate) getFederate();

        if (interactionClass.equals(federate.getClientCallsWaiterInteractionClassHandle())) {
            try {
                HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE();

                clientId.decode(theParameters.get(federate.getClientCallsWaiterInteractionClassClientIdParameterHandle()));

                federate.addWaiterRequest(clientId.getValue());

                log("Received request for waiter for client with id " + clientId.getValue());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}