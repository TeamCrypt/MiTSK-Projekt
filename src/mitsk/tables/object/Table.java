package mitsk.tables.object;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class Table extends AbstractObject {
    private Long tableId;

    private Client client = null;

    private static Long nextTableId = 0L;

    private ObjectClassHandle tableObjectClassHandle;

    private AttributeHandle isFreeAttributeClassHandle;

    private double freeAt;

    private boolean isNotified = false;

    public Table(RTIambassador rtiAmbassador) throws Exception {
        super(rtiAmbassador);

        tableId = nextTableId++;
    }

    public void setFree() {
        client = null;

        isNotified = false;
    }

    public void setOccupied(Client client, double freeAt) {
        this.client = client;

        this.freeAt = freeAt;
    }

    public boolean isFree() {
        return client == null;
    }

    public Long getTableId() {
        return tableId;
    }

    public Client getClient() {
        return client;
    }

    public double getFreeAt() {
        return freeAt;
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
