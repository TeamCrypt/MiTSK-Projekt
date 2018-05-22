package mitsk;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.time.HLAfloat64Time;

public class AbstractFederateAmbassador extends NullFederateAmbassador {
    private AbstractFederate federate;

    private double federateLookahead = 1.0;

    private double federateTime = 0.0;

    private boolean isAnnounced = false;

    private boolean isAdvancing = false;

    private boolean isConstrained = false;

    private boolean isReadyToRun = false;

    private boolean isRegulating = false;

    protected AbstractFederateAmbassador(AbstractFederate federate) {
        this.federate = federate;
    }

    protected AbstractFederate getFederate() {
        return federate;
    }

    public double getFederateLookahead() {
        return federateLookahead;
    }

    public double getFederateTime() {
        return federateTime;
    }

    public boolean getIsAnnounced() {
        return isAnnounced;
    }

    public boolean getIsAdvancing() {
        return isAdvancing;
    }

    public boolean getIsConstrained() {
        return isConstrained;
    }

    public boolean getIsReadyToRun() {
        return isReadyToRun;
    }

    public boolean getIsRegulating() {
        return isRegulating;
    }

    public final String getName() {
        return getClass().getName();
    }

    protected void log(String message) {
        System.out.println(getName() + ": " + message);
    }

    public AbstractFederateAmbassador setIsAdvancing(boolean isAdvancing) {
        this.isAdvancing = isAdvancing;

        return this;
    }

    @Override
    public void announceSynchronizationPoint(String label, byte[] tag) {
        log("Synchronization point announced: " + label);
        if (label.equals(AbstractFederate.READY_TO_RUN)) {
            isAnnounced = true;
        }
    }

    @Override
    public void federationSynchronized(String label, FederateHandleSet failed) {
        log("Federation Synchronized: " + label);
        if (label.equals(AbstractFederate.READY_TO_RUN)) {
            isReadyToRun = true;
        }
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters, byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        receiveInteraction(interactionClass, theParameters, userSuppliedTag, sentOrdering, theTransport, null, sentOrdering, receiveInfo);
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters, byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime, OrderType receivedOrdering, SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        receiveInteraction(interactionClass, theParameters, userSuppliedTag, sentOrdering, theTransport, theTime, receivedOrdering, null, receiveInfo);
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters, byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime, OrderType receivedOrdering, MessageRetractionHandle retractionHandle, SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        StringBuilder builder = new StringBuilder("Interaction Received:");

        // print the handle
        builder.append(" handle=").append(interactionClass);

        // print the tag
        builder.append(", tag=").append(new String(userSuppliedTag));
        // print the time (if we have it) we'll get null if we are just receiving
        // a forwarded call from the other reflect callback above
        if (theTime != null) {
            builder.append(", time=").append(((HLAfloat64Time) theTime).getValue());
        }

        // print the parameer information
        builder.append(", parameterCount=").append(theParameters.size());
        builder.append("\n");
        for (ParameterHandle parameter : theParameters.keySet()) {
            // print the parameter handle
            builder.append("\tparamHandle=");
            builder.append(parameter);
            // print the parameter value
            builder.append(", paramValue=");
            builder.append(theParameters.get(parameter).length);
            builder.append(" bytes");
        }

        log(builder.toString());
    }

    @Override
    public void synchronizationPointRegistrationFailed(String label,
                                                       SynchronizationPointFailureReason reason) {
        log("Failed to register sync point: " + label + ", reason=" + reason);
    }

    @Override
    public void synchronizationPointRegistrationSucceeded(String label) {
        log("Successfully registered sync point: " + label);
    }

    @Override
    public void timeAdvanceGrant(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.isAdvancing = false;
    }

    @Override
    public void timeConstrainedEnabled(LogicalTime time) {
        federateTime = ((HLAfloat64Time) time).getValue();
        isConstrained = true;
    }

    @Override
    public void timeRegulationEnabled(LogicalTime time) {
        federateTime = ((HLAfloat64Time) time).getValue();
        isRegulating = true;
    }
}
