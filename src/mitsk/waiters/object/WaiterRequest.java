package mitsk.waiters.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class WaiterRequest extends AbstractObject {
    private Client client;

    public WaiterRequest(RTIambassador rtiAmbassador, Client client) throws Exception {
        super(rtiAmbassador);

        this.client = client;
    }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    public Client getClient() {
        return client;
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }
}
