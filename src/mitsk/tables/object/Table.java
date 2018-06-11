package mitsk.tables.object;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class Table extends AbstractObject {
    private Long identificationNumber;

    private Client client = null;

    private static Long nextIdentificationNumber = 0L;

    private ObjectClassHandle tableObjectClassHandle;

    private AttributeHandle isFreeAttributeClassHandle;

    private boolean isNotified = false;

    public Table(RTIambassador rtiAmbassador) throws Exception {
        super(rtiAmbassador);

        identificationNumber = nextIdentificationNumber++;
    }

    public void setFree() {
        client = null;

        isNotified = false;
    }

    public void setOccupied(Client client) {
        this.client = client;
    }

    public boolean isFree() {
        return client == null;
    }

    public Long getIdentificationNumber() {
        return identificationNumber;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    @Override
    protected void setHandles() throws Exception {
        tableObjectClassHandle = getRtiAmbassador().getObjectClassHandle("HLAObjectRoot.Table");

//        isFreeAttributeClassHandle = getRtiAmbassador().getAttributeHandle(tableObjectClassHandle, "IsFree"); // @TODO hla.rti1516e.exceptions.NameNotFound: name: IsFree
    }

    public boolean isNotified() {
        return isNotified;
    }

    public void setNotified() {
        isNotified = true;
    }
}
