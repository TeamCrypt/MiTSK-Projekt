package mitsk.tables.interaction;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger64BE;
import mitsk.AbstractInteraction;
import mitsk.tables.object.Table;

public class ClientLeavesTable extends AbstractInteraction {
    private EncoderFactory encoderFactory;

    private Table table;

    private InteractionClassHandle clientLeavesTableInteractionClassHandle;

    private ParameterHandle clientLeavesTableInteractionClassClientIdParameterHandle;

    private ParameterHandle clientLeavesTableInteractionClassTableIdParameterHandle;

    public ClientLeavesTable(RTIambassador rtiAmbassador, Table table) throws Exception {
        super(rtiAmbassador);

        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        this.table = table;
    }

    @Override
    public void sendInteraction() throws Exception {
        RTIambassador rtIambassador = getRtiAmbassador();

        ParameterHandleValueMap parameters = rtIambassador.getParameterHandleValueMapFactory().create(1);

        { // clientId
            HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE(table.getClient().getIdentificationNumber());

            parameters.put(clientLeavesTableInteractionClassClientIdParameterHandle, clientId.toByteArray());
        }

        { // tableId
            HLAinteger64BE tableId = encoderFactory.createHLAinteger64BE(table.getTableId());

            parameters.put(clientLeavesTableInteractionClassTableIdParameterHandle, tableId.toByteArray());
        }

        rtIambassador.sendInteraction(clientLeavesTableInteractionClassHandle, parameters, generateTag());
    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtIambassador = getRtiAmbassador();

        clientLeavesTableInteractionClassHandle = rtIambassador.getInteractionClassHandle("HLAinteractionRoot.ClientLeavesTable");

        clientLeavesTableInteractionClassClientIdParameterHandle = rtIambassador.getParameterHandle(clientLeavesTableInteractionClassHandle, "clientId");

        clientLeavesTableInteractionClassTableIdParameterHandle = rtIambassador.getParameterHandle(clientLeavesTableInteractionClassHandle, "tableId");
    }
}
