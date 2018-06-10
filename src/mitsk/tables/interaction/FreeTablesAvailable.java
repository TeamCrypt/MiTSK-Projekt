package mitsk.tables.interaction;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractInteraction;

public class FreeTablesAvailable extends AbstractInteraction {
    private InteractionClassHandle freeTableAvailableInteractionClassHandle;

    public FreeTablesAvailable(RTIambassador rtiAmbassador) throws Exception {
        super(rtiAmbassador);
    }

    @Override
    public void sendInteraction() {
        // @TODO
    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        ParameterHandleValueMap parameters = rtiAmbassador.getParameterHandleValueMapFactory().create(0);

        freeTableAvailableInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.FreeTablesAvailable");

        rtiAmbassador.sendInteraction(freeTableAvailableInteractionClassHandle, parameters, generateTag());
    }
}
