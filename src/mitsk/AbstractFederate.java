package mitsk;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class AbstractFederate {
    public static final String READY_TO_RUN = "ReadyToRun";

    protected static final int ITERATIONS = 60;

    private String federationName;

    private RTIambassador rtiAmbassador;

    private AbstractFederateAmbassador federateAmbassador;

    private EncoderFactory encoderFactory;

    private HLAfloat64TimeFactory timeFactory;

    public AbstractFederate(String federationName) throws Exception {
        this.federationName = federationName;

        prepareRTI();
    }

    protected void advanceTime(double timeStep) throws RTIexception {
        federateAmbassador.setIsAdvancing(true);

        HLAfloat64Time time = timeFactory.makeTime(federateAmbassador.getFederateTime() + timeStep);
        rtiAmbassador.timeAdvanceRequest(time);

        while (federateAmbassador.getIsAdvancing()) {
            rtiAmbassador.evokeMultipleCallbacks(0.1, 0.2);
        }
    }

    protected abstract AbstractFederateAmbassador createAmbassador() throws Exception;

    private void createFederation() throws Exception {
        log("Create federation");
        rtiAmbassador.createFederationExecution(federationName, getFederationModules());
    }

    private void enableTimePolicy() throws Exception {
        log("Enabling time policy");

        HLAfloat64Interval lookahead = timeFactory.makeInterval(federateAmbassador.getFederateLookahead());

        rtiAmbassador.enableTimeRegulation(lookahead);

        while (!federateAmbassador.getIsRegulating()) {
            rtiAmbassador.evokeMultipleCallbacks(0.1, 0.2);
        }

        rtiAmbassador.enableTimeConstrained();

        while (!federateAmbassador.getIsConstrained()) {
            rtiAmbassador.evokeMultipleCallbacks(0.1, 0.2);
        }

        log("Time policy enabled");
    }

    protected byte[] generateTag() {
        return ("(timestamp) " + System.currentTimeMillis()).getBytes();
    }

    public EncoderFactory getEncoderFactory() {
        return encoderFactory;
    }

    protected final AbstractFederateAmbassador getFederateAmbassador() {
        return federateAmbassador;
    }

    protected abstract URL[] getFederationModules() throws MalformedURLException;

    protected abstract URL[] getJoinModules() throws MalformedURLException;

    public final String getName() {
        return getClass().getName();
    }

    protected final RTIambassador getRTIAmbassador() {
        return rtiAmbassador;
    }

    private String getType() {
        return getName() + "Type";
    }

    private void joinFederation() throws Exception {
        log("Joining federation");
        rtiAmbassador.joinFederationExecution(getName(), getType(), federationName, getJoinModules());
        log("Joined federation as " + getName());
    }

    protected final void log(String message) {
        System.out.println(getName() + ": " + message);
    }

    private void prepareRTI() throws Exception {
        log("Getting RTI factory");
        RtiFactory rtiFactory = RtiFactoryFactory.getRtiFactory();

        log("Creating RTI ambassador");
        rtiAmbassador = rtiFactory.getRtiAmbassador();

        log("Getting encoder factory");
        encoderFactory = rtiFactory.getEncoderFactory();

        log("Getting time factory");
        timeFactory = (HLAfloat64TimeFactory) rtiAmbassador.getTimeFactory();

        log("Creating federate ambassador");
        federateAmbassador = createAmbassador();

        log("Connecting with RTI ambassador");
        rtiAmbassador.connect(federateAmbassador, CallbackModel.HLA_EVOKED);

        try {
            createFederation();
        } catch (FederationExecutionAlreadyExists exception) {
            log("Didn't create federation, it already existed");
        }

        joinFederation();

        enableTimePolicy();

        publish();

        subscribe();
    }

    protected abstract void publish() throws Exception;

    protected void resignFederation() throws Exception {
        rtiAmbassador.resignFederationExecution(ResignAction.DELETE_OBJECTS);
        log("Resigned from federation");

        destroyFederation();
    }

    private void destroyFederation() throws Exception {
        log("Destroying federation");

        try {
            rtiAmbassador.destroyFederationExecution(federationName);
            log("Destroyed federation");
        } catch (FederationExecutionDoesNotExist exception) {
            log("No need to destroy federation, it doesn't exist");
        } catch (FederatesCurrentlyJoined exception) {
            log("Didn't destroy federation, federates still joined");
        }
    }

    public void run() throws Exception {
        waitForSynchronization();
    }

    protected abstract void subscribe() throws Exception;

    private void waitForSynchronization() throws Exception {
        rtiAmbassador.registerFederationSynchronizationPoint(READY_TO_RUN, null);

        while (!federateAmbassador.getIsAnnounced()) {
            rtiAmbassador.evokeMultipleCallbacks(0.1, 0.2);
        }

        waitForUser();

        rtiAmbassador.synchronizationPointAchieved(READY_TO_RUN);
        log("Achieved sync point: " + READY_TO_RUN + ", waiting for federation...");
        while (!federateAmbassador.getIsReadyToRun()) {
            rtiAmbassador.evokeMultipleCallbacks(0.1, 0.2);
        }
    }

    private void waitForUser() {
        log(" >>>>>>>>>> Press Enter to Continue <<<<<<<<<<");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            reader.readLine();
        } catch (Exception exception) {
            log("Error while waiting for user input: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}
