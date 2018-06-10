package mitsk.tables.interaction;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger64BE;
import mitsk.AbstractInteraction;
import mitsk.tables.object.Client;
import mitsk.tables.object.Table;

public class ClientTakesTable extends AbstractInteraction {
    private EncoderFactory encoderFactory;

    private Client client;

    private Table table;

    private InteractionClassHandle clientTakesTableInteractionClassHandle;

    private ParameterHandle clientTakesTableInteractionClassClientIdParameterHandle;

    private ParameterHandle clientTakesTableInteractionClassTableIdParameterHandle;

    public ClientTakesTable(RTIambassador rtiAmbassador, Client client, Table table) throws Exception {
        super(rtiAmbassador);

        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        this.client = client;

        this.table = table;
    }

    @Override
    public void sendInteraction() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        ParameterHandleValueMap parameters = rtiAmbassador.getParameterHandleValueMapFactory().create(1);

        { // clientId
            HLAinteger64BE clientId = encoderFactory.createHLAinteger64BE(client.getIdentificationNumber());

            parameters.put(clientTakesTableInteractionClassClientIdParameterHandle, clientId.toByteArray());
        }

        { // tableId
            HLAinteger64BE tableId = encoderFactory.createHLAinteger64BE(table.getTableId());

            parameters.put(clientTakesTableInteractionClassTableIdParameterHandle, tableId.toByteArray());
        }

        rtiAmbassador.sendInteraction(clientTakesTableInteractionClassHandle, parameters, generateTag());
    }

    @Override
    protected void setHandles() throws Exception {
        RTIambassador rtiAmbassador = getRtiAmbassador();

        clientTakesTableInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.ClientTakesTable");

        clientTakesTableInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientTakesTableInteractionClassHandle, "clientId");

        clientTakesTableInteractionClassTableIdParameterHandle = rtiAmbassador.getParameterHandle(clientTakesTableInteractionClassHandle, "tableId");
    }
}
