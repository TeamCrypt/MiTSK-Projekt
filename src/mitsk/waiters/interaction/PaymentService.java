package mitsk.waiters.interaction;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAfloat64BE;
import hla.rti1516e.encoding.HLAinteger64BE;
import mitsk.AbstractInteraction;
import mitsk.waiters.object.Client;

public class PaymentService extends AbstractInteraction {
    private Client client;

    private double billCost;

    private InteractionClassHandle paymentServiceInteractionClassHandle;

    private ParameterHandle paymentServiceInteractionClassClientIdParameterHandle;

    private ParameterHandle paymentServiceInteractionClassBillCostParameterHandle;

    private EncoderFactory encoderFactory;

    public PaymentService(RTIambassador rtiAmbassador, Client client, double billCost) throws Exception {
        super(rtiAmbassador);

        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        this.client = client;

        this.billCost = billCost;
    }

    @Override
    public void sendInteraction() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        ParameterHandleValueMap parameters = rtiAmbassador.getParameterHandleValueMapFactory().create(2);

        HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE(client.getIdentificationNumber());

        parameters.put(paymentServiceInteractionClassClientIdParameterHandle, clientId.toByteArray());

        HLAfloat64BE cost = encoderFactory.createHLAfloat64BE(billCost);

        parameters.put(paymentServiceInteractionClassBillCostParameterHandle, cost.toByteArray());

        rtiAmbassador.sendInteraction(paymentServiceInteractionClassHandle, parameters, generateTag());
    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        paymentServiceInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.PaymentService");

        paymentServiceInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(paymentServiceInteractionClassHandle, "clientId");

        paymentServiceInteractionClassBillCostParameterHandle = rtiAmbassador.getParameterHandle(paymentServiceInteractionClassHandle, "billCost");
    }
}
