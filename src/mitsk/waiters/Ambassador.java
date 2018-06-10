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

                federate.addNewOrderRequest(clientId.getValue());

                log("Received request for waiter for client with id " + clientId.getValue());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else if (interactionClass.equals(federate.getClientOrdersMealInteractionClassHandle())) {
            try {
                HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE();

                clientId.decode(theParameters.get(federate.getClientOrdersMealInteractionClassClientIdParameterHandle()));

                HLAinteger64BE mealId = encoderFactory.createHLAinteger64BE();

                mealId.decode(theParameters.get(federate.getClientOrdersMealInteractionClassMealIdParameterHandle()));

                federate.addMealToClientOrder(clientId.getValue(), mealId.getValue());

                log("Client with id " + clientId.getValue() + " ordered a meal with id " + mealId.getValue());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else if (interactionClass.equals(federate.getPreparedMealRequestInteractionClassHandle())) {
            try {
                HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE();

                clientId.decode(theParameters.get(federate.getPreparedMealRequestInteractionClassClientIdParameterHandle()));

                HLAinteger64BE mealId = encoderFactory.createHLAinteger64BE();

                mealId.decode(theParameters.get(federate.getPreparedMealRequestInteractionClassMealIdParameterHandle()));

                federate.addTakeMealRequest(clientId.getValue(), mealId.getValue());

                log("Meal with id " + mealId.getValue() + " ordered by client with id " + clientId.getValue() + " has been already prepared and is waiting in the kitchen");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}