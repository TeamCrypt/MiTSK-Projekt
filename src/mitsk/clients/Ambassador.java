package mitsk.clients;

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

        if (interactionClass.equals(federate.getClientTakesTableInteractionClassHandle())) {
            try {
                Long clientIdentificationNumber;

                Long tableIdentificationNumber;

                { // clientId
                    HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE();

                    clientId.decode(theParameters.get(federate.getClientTakesTableInteractionClassClientIdParameterHandle()));

                    clientIdentificationNumber = clientId.getValue();
                }

                { // tableId
                    HLAinteger64BE tableId = encoderFactory.createHLAinteger64BE();

                    tableId.decode(theParameters.get(federate.getClientTakesTableInteractionClassTableIdParameterHandle()));

                    tableIdentificationNumber = tableId.getValue();
                }

                federate.clientTakesTable(clientIdentificationNumber, tableIdentificationNumber);

                log("Client " + clientIdentificationNumber + " takes table " + tableIdentificationNumber);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
