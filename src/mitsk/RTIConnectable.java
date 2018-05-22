package mitsk;

import hla.rti1516e.RTIambassador;

public abstract class RTIConnectable {
    private RTIambassador rtiAmbassador;

    RTIConnectable(RTIambassador rtiAmbassador) throws Exception {
        this.rtiAmbassador = rtiAmbassador;

        setHandles();
    }

    protected byte[] generateTag() {
        return ("(timestamp) " + System.currentTimeMillis()).getBytes();
    }

    protected RTIambassador getRtiAmbassador() {
        return rtiAmbassador;
    }

    protected abstract void setHandles() throws Exception;
}
