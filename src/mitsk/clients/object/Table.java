package mitsk.clients.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class Table extends AbstractObject {
    private Long identificationNumber;

    public Table(RTIambassador rtiAmbassador, Long identificationNumber) throws Exception {
        super(rtiAmbassador);
    }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    public Long getIdentificationNumber() {
        return identificationNumber;
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }
}
