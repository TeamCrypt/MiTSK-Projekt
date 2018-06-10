package mitsk.gui;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.FederateInternalError;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;

public class Ambassador extends AbstractFederateAmbassador {
    private EncoderFactory encoderFactory; // @TODO

    Ambassador(AbstractFederate federate) {
        super(federate);

        encoderFactory = getFederate().getEncoderFactory();
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters, byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime, OrderType receivedOrdering, MessageRetractionHandle retractionHandle, SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        super.receiveInteraction(interactionClass, theParameters, userSuppliedTag, sentOrdering, theTransport, theTime, receivedOrdering, retractionHandle, receiveInfo);

        Federate federate = (Federate) getFederate();

        if (interactionClass.equals(federate.getNewInQueueInteractionClassHandle())) {
            // @TODO
        } else if (interactionClass.equals(federate.getLeaveFromQueueInteractionClassHandle())) {
            // @TODO
        } else if ((interactionClass.equals(federate.getClientImpatienceInteractionClassHandle()))) {
            // @TODO
        }
    }
}
