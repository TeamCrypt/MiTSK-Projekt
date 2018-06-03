package mitsk.kitchen;

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

        if (interactionClass.equals(federate.getNewMealRequestInteractionClassHandle())) {
            try {
                HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE();

                clientId.decode(theParameters.get(federate.getNewMealRequestInteractionClassClientIdParameterHandle()));

                HLAinteger64BE mealId = encoderFactory.createHLAinteger64BE();

                mealId.decode(theParameters.get(federate.getNewMealRequestInteractionClassMealIdParameterHandle()));

                federate.addMealRequest(clientId.getValue(), mealId.getValue());

                log("Received request for meal with id " + mealId.getValue() + " for client with id " + clientId.getValue());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else if (interactionClass.equals(federate.getTakeFoodInteractionClassHandle())) {
            try {
                HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE();

                clientId.decode(theParameters.get(federate.getTakeFoodInteractionClassClientIdParameterHandle()));

                HLAinteger64BE mealId = encoderFactory.createHLAinteger64BE();

                mealId.decode(theParameters.get(federate.getTakeFoodInteractionClassMealIdParameterHandle()));

                if (federate.removePreparedFood(clientId.getValue(), mealId.getValue())) {
                    log("Food with id " + mealId.getValue() + " for client " + clientId.getValue() + " was taken");
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
