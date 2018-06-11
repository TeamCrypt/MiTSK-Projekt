package mitsk.gui;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederateInternalError;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;

public class Ambassador extends AbstractFederateAmbassador {
    Ambassador(AbstractFederate federate) {
        super(federate);
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters, byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime, OrderType receivedOrdering, MessageRetractionHandle retractionHandle, SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        super.receiveInteraction(interactionClass, theParameters, userSuppliedTag, sentOrdering, theTransport, theTime, receivedOrdering, retractionHandle, receiveInfo);

        Federate federate = (Federate) getFederate();

        GUI gui = federate.getGui();

        if (interactionClass.equals(federate.getNewClientInteractionClassHandle())) {
            log("New Client");

            gui.addClient();
        } else if (interactionClass.equals(federate.getNewInQueueInteractionClassHandle())) {
            log("Client enters to Queue");

            gui.addToQueue();
        } else if (interactionClass.equals(federate.getLeaveFromQueueInteractionClassHandle())) {
            log("Client leaves from Queue");

            gui.removeFromQueue();
        } else if (interactionClass.equals(federate.getClientImpatienceInteractionClassHandle())) {
            log("Client are impatient");

            gui.addImpatientClient();
        } else if (interactionClass.equals(federate.getClientLeavesTableInteractionClassHandle())) {
            log("Client leaves Table");

            gui.removeClient();
        }
    }
}
