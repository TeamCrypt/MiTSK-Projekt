package mitsk;

import hla.rti1516e.RTIambassador;

public abstract class AbstractInteraction extends RTIConnectable {
    protected AbstractInteraction(RTIambassador rtiAmbassador) throws Exception {
        super(rtiAmbassador);
    }

    public abstract void sendInteraction() throws Exception;
}
