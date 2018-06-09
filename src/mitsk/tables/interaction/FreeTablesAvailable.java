package mitsk.tables.interaction;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger64BE;
import mitsk.AbstractInteraction;
import mitsk.tables.object.Client;
import mitsk.tables.object.Table;

public class FreeTablesAvailable extends AbstractInteraction {
    private EncoderFactory encoderFactory;

    private InteractionClassHandle freeTableAvailableInteractionClassHandle;

    public FreeTablesAvailable(RTIambassador rtiAmbassador) throws Exception {
        super(rtiAmbassador);
    }

    @Override
    public void sendInteraction() {

    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        ParameterHandleValueMap parameters = rtiAmbassador.getParameterHandleValueMapFactory().create(0);

        freeTableAvailableInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.FreeTablesAvailable");

        rtiAmbassador.sendInteraction(freeTableAvailableInteractionClassHandle, parameters, generateTag());
    }
}
