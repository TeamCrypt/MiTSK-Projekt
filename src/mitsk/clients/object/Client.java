package mitsk.clients.object;

import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class Client extends AbstractObject {
    private static Long nextIdentificationNumber = 0L;

    private ObjectClassHandle clientObjectClassHandle;

    private Long identificationNumber;

    private ObjectInstanceHandle objectInstanceHandle;

    private Table table;

    public Client(RTIambassador rtiAmbassador) throws Exception {
        super(rtiAmbassador);

        identificationNumber = nextIdentificationNumber++;

//        objectInstanceHandle = getRtiAmbassador().registerObjectInstance(clientObjectClassHandle); // @TODO
    }

    @Override
    public void destruct() throws Exception {
//        getRtiAmbassador().deleteObjectInstance(objectInstanceHandle, generateTag()); // @TODO
    }

    public Long getIdentificationNumber() {
        return identificationNumber;
    }

    public ObjectInstanceHandle getObjectInstanceHandle() {
        return objectInstanceHandle;
    }

    @Override
    protected void setHandles() throws Exception {
        clientObjectClassHandle = getRtiAmbassador().getObjectClassHandle("HLAobjectRoot.Client");
    }

    public void takeTable(Table table) {
        this.table = table;
    }
}
