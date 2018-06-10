package mitsk.gui;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractFederate;
import mitsk.AbstractFederateAmbassador;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Federate extends AbstractFederate {
    private InteractionClassHandle newInQueueInteractionClassHandle;

    private ParameterHandle newInQueueInteractionClassClientIdParameterHandle;

    private InteractionClassHandle leaveFromQueueInteracionClassHandle;

    private ParameterHandle leaveFromQueueInteractionClassClientIdParameterHandle;

    private InteractionClassHandle clientImpatienceInteracionClassHandle;

    private ParameterHandle clientImpatienceInteractionClassClientIdParameterHandle;

    public Federate(String federationName) throws Exception {
        super(federationName);
    }

    InteractionClassHandle getNewInQueueInteractionClassHandle() {
        return newInQueueInteractionClassHandle;
    }

    ParameterHandle getNewInQueueInteractionClassClientIdParameterHandle() {
        return newInQueueInteractionClassClientIdParameterHandle;
    }

    InteractionClassHandle getLeaveFromQueueInteracionClassHandle() {
        return leaveFromQueueInteracionClassHandle;
    }

    ParameterHandle getLeaveFromQueueInteractionClassClientIdParameterHandle() {
        return leaveFromQueueInteractionClassClientIdParameterHandle;
    }

    InteractionClassHandle getClientImpatienceInteracionClassHandle() {
        return leaveFromQueueInteracionClassHandle;
    }

    ParameterHandle getClientImpatienceInteractionClassClientIdParameterHandle() {
        return clientImpatienceInteractionClassClientIdParameterHandle;
    }

    @Override
    protected AbstractFederateAmbassador createAmbassador() throws Exception {
        return new Ambassador(this);
    }

    @Override
    protected URL[] getFederationModules() throws MalformedURLException {
        return new URL[]{
                (new File("foms/Clients.xml")).toURI().toURL(),
                (new File("foms/Kitchen.xml")).toURI().toURL(),
                (new File("foms/Queue.xml")).toURI().toURL(),
                (new File("foms/Statistics.xml")).toURI().toURL(),
                (new File("foms/Gui.xml")).toURI().toURL()
        };    }

    @Override
    protected URL[] getJoinModules() throws MalformedURLException {
        return new URL[]{
                (new File("foms/Gui.xml")).toURI().toURL()
        };    }

    @Override
    protected void publish() throws Exception {

    }

    @Override
    protected void subscribe() throws Exception {
        RTIambassador rtiAmbassador = getRTIAmbassador();

        { // NewInQueue
            newInQueueInteractionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewInQueue");

            rtiAmbassador.subscribeInteractionClass(newInQueueInteractionClassHandle);

            newInQueueInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(newInQueueInteractionClassHandle, "clientId");
        }

        { // LeaveFromQueue
            leaveFromQueueInteracionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.LeaveFromQueue");

            rtiAmbassador.subscribeInteractionClass(leaveFromQueueInteracionClassHandle);

            leaveFromQueueInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(leaveFromQueueInteracionClassHandle, "clientId");
        }

        { // ClientImpatience
            clientImpatienceInteracionClassHandle = rtiAmbassador.getInteractionClassHandle("HLAinteractionRoot.NewInQueue.ClientImpatience");

            rtiAmbassador.subscribeInteractionClass(clientImpatienceInteracionClassHandle);

            newInQueueInteractionClassClientIdParameterHandle = rtiAmbassador.getParameterHandle(clientImpatienceInteracionClassHandle, "clientId");
        }
    }
}
