package mitsk.waiters;

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

        if (interactionClass.equals(federate.getClientCallsWaiterInteractionClassHandle())) {
            try {
                Long clientIdentificationNumber;

                { // clientId
                    HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE();

                    clientId.decode(theParameters.get(federate.getClientCallsWaiterInteractionClassClientIdParameterHandle()));

                    clientIdentificationNumber = clientId.getValue();
                }

                federate.addClientsCall(clientIdentificationNumber);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else if (interactionClass.equals(federate.getClientOrdersMealInteractionClassHandle())) {
            try {
                Long clientIdentificationNumber;
                Long mealIdentificationNumber;
                Long waiterIdentificationNumber;

                { // clientId
                    HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE();

                    clientId.decode(theParameters.get(federate.getClientOrdersMealInteractionClassClientIdParameterHandle()));

                    clientIdentificationNumber = clientId.getValue();
                }

                { // mealId
                    HLAinteger64BE mealId = encoderFactory.createHLAinteger64BE();

                    mealId.decode(theParameters.get(federate.getClientOrdersMealInteractionClassMealIdParameterHandle()));

                    mealIdentificationNumber = mealId.getValue();
                }

                { // waiterId
                    HLAinteger64BE waiterId = encoderFactory.createHLAinteger64BE();

                    waiterId.decode(theParameters.get(federate.getClientOrdersMealInteractionClassWaiterIdParameterHandle()));

                    waiterIdentificationNumber = waiterId.getValue();
                }

                federate.orderMealForClient(clientIdentificationNumber, mealIdentificationNumber, waiterIdentificationNumber);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else if (interactionClass.equals(federate.getPreparedMealRequestInteractionClassHandle())) {
            try {
                Long clientIdentificationNumber;
                Long mealIdentificationNumber;

                { // clientId
                    HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE();

                    clientId.decode(theParameters.get(federate.getPreparedMealRequestInteractionClassClientIdParameterHandle()));

                    clientIdentificationNumber = clientId.getValue();
                }

                { // mealId
                    HLAinteger64BE mealId = encoderFactory.createHLAinteger64BE();

                    mealId.decode(theParameters.get(federate.getPreparedMealRequestInteractionClassMealIdParameterHandle()));

                    mealIdentificationNumber = mealId.getValue();
                }

                federate.takeMealRequest(clientIdentificationNumber, mealIdentificationNumber);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
//        } else if (interactionClass.equals(federate.getClientAsksForBillInteractionClassHandle())) {
//            try {
//                HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE();
//
//                clientId.decode(theParameters.get(federate.getClientAsksForBillInteractionClassClientIdParameterHandle()));
//
//                federate.addGiveBillRequest(clientId.getValue());
//
//                log("Received request for bill for client with id " + clientId.getValue());
//            } catch (Exception exception) {
//                exception.printStackTrace();
//            }
//        } else if (interactionClass.equals(federate.getEndingClientServiceInteractionClassHandle())) {
//            try {
//                HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE();
//
//                clientId.decode(theParameters.get(federate.getEndingClientServiceInteractionClassClientIdParameterHandle()));
//
//                federate.endClientService(clientId.getValue());
//
//                log("Received payment for bill from client with id " + clientId.getValue());
//            } catch (Exception exception) {
//                exception.printStackTrace();
//            }
        }
    }
}
