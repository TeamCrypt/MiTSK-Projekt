package federates;

import federates.ambassadors.QueueFederateAmbassador;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.ResignAction;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger16BE;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;


public class QueueFederate {
    //----------------------------------------------------------
    //                    STATIC VARIABLES
    //----------------------------------------------------------
    /**
     * The number of times we will update our attributes and send an interaction
     */
    public static final int ITERATIONS = 20;

    /**
     * The sync point all federates will sync up on before starting
     */
    public static final String READY_TO_RUN = "ReadyToRun";

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------
    private RTIambassador rtiamb;
    private QueueFederateAmbassador fedamb;  // created when we connect
    private HLAfloat64TimeFactory timeFactory; // set when we join

    protected ObjectClassHandle queueHandle;
    protected ObjectClassHandle clientHandle;
    protected ObjectClassHandle tableHandle;
    protected InteractionClassHandle newInQueueHandle;
    protected InteractionClassHandle leaveFromQueueHandle;
    protected InteractionClassHandle clientImpatienceHandle;
    protected InteractionClassHandle newClientHandle;
    protected InteractionClassHandle tableBecomesFreeHandle;
    protected InteractionClassHandle freeTableShoutsHandle;

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------

    /**
     * This is just a helper method to make sure all logging it output in the same form
     */
    private void log(String message) {
        System.out.println("QueueFederate   : " + message);
    }

    /**
     * This method will block until the user presses enter
     */
    private void waitForUser() {
        log(" >>>>>>>>>> Press Enter to Continue <<<<<<<<<<");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            reader.readLine();
        } catch (Exception e) {
            log("Error while waiting for user input: " + e.getMessage());
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    ////////////////////////// Main Simulation Method /////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * This is the main simulation loop. It can be thought of as the main method of
     * the federate. For a description of the basic flow of this federate, see the
     * class level comments
     */

    public void runFederate(String federateName) throws Exception {
        /////////////////////////////////////////////////
        // 1 & 2. create the RTIambassador and Connect //
        /////////////////////////////////////////////////
        log("Creating RTIambassador");
        rtiamb = RtiFactoryFactory.getRtiFactory().getRtiAmbassador();

        // connect
        log("Connecting...");
        fedamb = new QueueFederateAmbassador(this);
        rtiamb.connect(fedamb, CallbackModel.HLA_EVOKED);

        //////////////////////////////
        // 3. create the federation //
        //////////////////////////////
        log("Creating Federation...");

        // We attempt to create a new federation with the queue
        // FOM module
        try {
            URL[] modules = new URL[]{
                    (new File("foms/Queue.xml")).toURI().toURL()
            };

            rtiamb.createFederationExecution("ExampleFederation", modules);
            log("Created Federation");
        } catch (FederationExecutionAlreadyExists exists) {
            log("Didn't create federation, it already existed");
        } catch (MalformedURLException urle) {
            log("Exception loading one of the FOM modules from disk: " + urle.getMessage());
            urle.printStackTrace();
            return;
        }

        ////////////////////////////
        // 4. join the federation //
        ////////////////////////////
        URL[] joinModules = new URL[]{
                (new File("foms/Queue.xml")).toURI().toURL()
        };

        rtiamb.joinFederationExecution(federateName,            // name for the federate
                "ExampleFederateType",   // federate type
                "ExampleFederation",     // name of federation
                joinModules);           // modules we want to add

        log("Joined Federation as " + federateName);

        // cache the time factory for easy access
        this.timeFactory = (HLAfloat64TimeFactory) rtiamb.getTimeFactory();

        ////////////////////////////////
        // 5. announce the sync point //
        ////////////////////////////////
        // announce a sync point to get everyone on the same page. if the point
        // has already been registered, we'll get a callback saying it failed,
        // but we don't care about that, as long as someone registered it
        rtiamb.registerFederationSynchronizationPoint(READY_TO_RUN, null);
        // wait until the point is announced
        while (fedamb.getIsAnnounced() == false) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }

        // WAIT FOR USER TO KICK US OFF
        // So that there is time to add other federates, we will wait until the
        // user hits enter before proceeding. That was, you have time to start
        // other federates.
        waitForUser();

        ///////////////////////////////////////////////////////
        // 6. achieve the point and wait for synchronization //
        ///////////////////////////////////////////////////////
        // tell the RTI we are ready to move past the sync point and then wait
        // until the federation has synchronized on
        rtiamb.synchronizationPointAchieved(READY_TO_RUN);
        log("Achieved sync point: " + READY_TO_RUN + ", waiting for federation...");
        while (fedamb.getIsReadyToRun() == false) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }

        /////////////////////////////
        // 7. enable time policies //
        /////////////////////////////
        // in this section we enable/disable all time policies
        // note that this step is optional!
        enableTimePolicy();
        log("Time Policy Enabled");

        //////////////////////////////
        // 8. publish and subscribe //
        //////////////////////////////
        // in this section we tell the RTI of all the data we are going to
        // produce, and all the data we want to know about
        publishAndSubscribe();
        log("Published and Subscribed");

        /////////////////////////////////////
        // 9. register an object to update //
        /////////////////////////////////////
        //ObjectInstanceHandle objectHandle = registerObject();
        //log("Registered Object, handle=" + objectHandle);

        /////////////////////////////////////
        // 10. do the main simulation loop //
        /////////////////////////////////////
        // here is where we do the meat of our work. in each iteration, we will
        // update the attribute values of the object we registered, and will
        // send an interaction.
        for (int i = 0; i < ITERATIONS; i++) {
            // 9.1 update the attribute values of the instance //
            // updateAttributeValues(objectHandle);

            // 9.2 send an interaction
            // sendInteraction();

            // 9.3 request a time advance and wait until we get it
            advanceTime(1.0);
            log("Time Advanced to " + fedamb.getFederateTime());
        }

        //////////////////////////////////////
        // 11. delete the object we created //
        //////////////////////////////////////
        // deleteObject(objectHandle);
        // log("Deleted Object, handle=" + objectHandle);

        ////////////////////////////////////
        // 12. resign from the federation //
        ////////////////////////////////////
        rtiamb.resignFederationExecution(ResignAction.DELETE_OBJECTS);
        log("Resigned from Federation");

        ////////////////////////////////////////
        // 13. try and destroy the federation //
        ////////////////////////////////////////
        // NOTE: we won't die if we can't do this because other federates
        //       remain. in that case we'll leave it for them to clean up
        try {
            rtiamb.destroyFederationExecution("ExampleFederation");
            log("Destroyed Federation");
        } catch (FederationExecutionDoesNotExist dne) {
            log("No need to destroy federation, it doesn't exist");
        } catch (FederatesCurrentlyJoined fcj) {
            log("Didn't destroy federation, federates still joined");
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// Helper Methods //////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * This method will attempt to enable the various time related properties for
     * the federate
     */
    private void enableTimePolicy() throws Exception {
        // NOTE: Unfortunately, the LogicalTime/LogicalTimeInterval create code is
        //       Portico specific. You will have to alter this if you move to a
        //       different RTI implementation. As such, we've isolated it into a
        //       method so that any change only needs to happen in a couple of spots
        HLAfloat64Interval lookahead = timeFactory.makeInterval(fedamb.getFederateLookahead());

        ////////////////////////////
        // enable time regulation //
        ////////////////////////////
        this.rtiamb.enableTimeRegulation(lookahead);

        // tick until we get the callback
        while (fedamb.getIsRegulating() == false) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }

        /////////////////////////////
        // enable time constrained //
        /////////////////////////////
        this.rtiamb.enableTimeConstrained();

        // tick until we get the callback
        while (fedamb.getIsConstrained() == false) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }
    }

    /**
     * This method will inform the RTI about the types of data that the federate will
     * be creating, and the types of data we are interested in hearing about as other
     * federates produce it.
     */
    private void publishAndSubscribe() throws RTIexception {
        ///////////////////////////////////////////////
        // publish all attributes of Food.Drink.Soda //
        ///////////////////////////////////////////////
        // before we can register instance of the object class Food.Drink.Soda and
        // update the values of the various attributes, we need to tell the RTI
        // that we intend to publish this information

        // get all the handle information for the attributes of Food.Drink.Soda
        this.queueHandle = rtiamb.getObjectClassHandle("HLAobjectRoot.Queue");
        this.clientHandle = rtiamb.getObjectClassHandle("HLAobjectRoot.Client");
        this.tableHandle = rtiamb.getObjectClassHandle("HLAobjectRoot.Table");
        this.newInQueueHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.NewInQueue");
        this.leaveFromQueueHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.LeaveFromQueue");
        this.clientImpatienceHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.LeaveFromQueue.ClientImpatience");
        this.newClientHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.NewClient");
        this.tableBecomesFreeHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.TableBecomesFree");
        this.freeTableShoutsHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.FreeTableShouts");

        // do the actual publication
        rtiamb.publishInteractionClass(newInQueueHandle);
        rtiamb.publishInteractionClass(leaveFromQueueHandle);
        rtiamb.publishInteractionClass(clientImpatienceHandle);


        /////////////////////////////////////////////////////////
        // subscribe to the FoodServed.DrinkServed interaction //
        /////////////////////////////////////////////////////////
        // we also want to receive other interaction of the same type that are
        // sent out by other federates, so we have to subscribe to it first
        rtiamb.subscribeInteractionClass(newClientHandle);
        rtiamb.subscribeInteractionClass(tableBecomesFreeHandle);
        rtiamb.subscribeInteractionClass(freeTableShoutsHandle);
    }

    /**
     * This method will request a time advance to the current time, plus the given
     * timestep. It will then wait until a notification of the time advance grant
     * has been received.
     */
    private void advanceTime(double timestep) throws RTIexception {
        // request the advance
        fedamb.setIsAdvancing(true);
        HLAfloat64Time time = timeFactory.makeTime(fedamb.getFederateTime() + timestep);
        rtiamb.timeAdvanceRequest(time);

        // wait for the time advance to be granted. ticking will tell the
        // LRC to start delivering callbacks to the federate
        while (fedamb.getIsAdvancing()) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }
    }

    //----------------------------------------------------------
    //                     STATIC METHODS
    //----------------------------------------------------------
    public static void main(String[] args) {
        // get a federate name, use "exampleFederate" as default
        String federateName = "exampleFederate";
        if (args.length != 0) {
            federateName = args[0];
        }

        try {
            // run the example federate
            new ExampleFederate().runFederate(federateName);
        } catch (Exception rtie) {
            // an exception occurred, just log the information and exit
            rtie.printStackTrace();
        }
    }

    public InteractionClassHandle getNewClientHandle() {
        return newClientHandle;
    }

    public InteractionClassHandle getTableBecomesFreeHandle() {
        return tableBecomesFreeHandle;
    }

    public InteractionClassHandle getFreeTableShoutsHandle() {
        return freeTableShoutsHandle;
    }


}
