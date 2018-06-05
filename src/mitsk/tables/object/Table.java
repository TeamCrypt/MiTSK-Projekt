package mitsk.tables.object;

import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class Table extends AbstractObject {
    private boolean isFree;

    private Long tableId;

    private Client client;

    private static Long nextTableId = 0L;


    Table(RTIambassador rtiAmbassador) throws Exception {
        super(rtiAmbassador);

        this.isFree = true;

        this.client = null;

        this.tableId = nextTableId;

        nextTableId++;
    }

    public void setFree() {
        this.isFree = true;

        this.client = null;
    }

    public void setOccupied(Client client) {
        this.isFree = false;

        this.client = client;
    }

    public boolean isFree() {
        return this.isFree;
    }

    public Long getTableId() { return this.tableId; }

    public Client getClient() { return this.client; }

    @Override
    public void destruct() throws Exception {
        // empty
    }

    @Override
    protected void setHandles() throws Exception {
        // empty
    }
}
