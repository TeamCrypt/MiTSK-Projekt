package mitsk.tables.object;

import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class Table extends AbstractObject {
    private Long tableId;

    private Client client = null;

    private static Long nextTableId = 0L;

    private ObjectClassHandle tableObjectClassHandle;



    Table(RTIambassador rtiAmbassador) throws Exception {
        super(rtiAmbassador);

        this.tableId = nextTableId;

        nextTableId++;
    }

    public void setFree() {
        this.client = null;
    }

    public void setOccupied(Client client) {
        this.client = client;
    }

    public boolean isFree() {
        return this.client == null;
    }

    public Long getTableId() { return this.tableId; }

    public Client getClient() { return this.client; }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    @Override
    protected void setHandles() throws Exception {
        tableObjectClassHandle = getRtiAmbassador().getObjectClassHandle("HLAObjectRoot.Table");
    }
}
