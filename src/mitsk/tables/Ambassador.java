package mitsk.tables;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger64BE;
import hla.rti1516e.exceptions.FederateInternalError;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;

public class Ambassador extends AbstractFederateAmbassador {
    private EncoderFactory encoderFactory;

    Ambassador(AbstractFederate federate) {
        super(federate);

        encoderFactory = getFederate().getEncoderFactory();
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters, byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime, OrderType receivedOrdering, MessageRetractionHandle retractionHandle, SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        super.receiveInteraction(interactionClass, theParameters, userSuppliedTag, sentOrdering, theTransport, theTime, receivedOrdering, retractionHandle, receiveInfo);

        Federate federate = (Federate) getFederate();

        if (interactionClass.equals(federate.getLeaveFromQueueInteractionClassHandle())) {
            try {
                Long clientIdentificationNumber;

                { // clientId
                    HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE();

                    clientId.decode(theParameters.get(federate.getLeaveFromQueueInteractionClassClientIdParameterHandle()));

                    clientIdentificationNumber = clientId.getValue();
                }

                federate.addClient(clientIdentificationNumber);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else if (interactionClass.equals(federate.getClientWantsToLeaveInteractionClassHandle())) {
            try {
                Long clientIdentificationNumber;

                { // clientId
                    HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE();

                    clientId.decode(theParameters.get(federate.getClientWantsToLeaveInteractionClassClientIdParameterHandle()));

                    clientIdentificationNumber = clientId.getValue();
                }

                log("wants to leave " + clientIdentificationNumber);

                federate.nextClientToLeave(clientIdentificationNumber);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
