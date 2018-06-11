package mitsk.clients.object;

import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.RTIambassador;
import mitsk.AbstractObject;

public class Client extends AbstractObject {
    public enum Status {
        WAITING_FOR_TABLE,
        WAITING_FOR_WAITER,
        WAITING_FOR_SERVICE,
        WAITING_FOR_MEAL,
        EATING,
        WANT_TO_LEAVE,
        READY_TO_LEAVE
    }
    private static Long nextIdentificationNumber = 0L;

    private ObjectClassHandle clientObjectClassHandle;

    private double freeAt = 0.0;

    private Long identificationNumber;

    private int mealsOrdered = 0;

    private int mealsToOrder;

    private ObjectInstanceHandle objectInstanceHandle;

    private Table table;

    private Status status = Status.WAITING_FOR_TABLE;

    public Client(RTIambassador rtiAmbassador, int mealsToOrder) throws Exception {
        super(rtiAmbassador);

        identificationNumber = nextIdentificationNumber++;

        this.mealsToOrder = mealsToOrder;

//        objectInstanceHandle = getRtiAmbassador().registerObjectInstance(clientObjectClassHandle); // @TODO
    }

    @Override
    public void destruct() throws Exception {
//        getRtiAmbassador().deleteObjectInstance(objectInstanceHandle, generateTag()); // @TODO
    }

    public boolean isEating() {
        return getStatus() == Status.EATING;
    }

    public boolean isReadyToLeave() {
        return getStatus() == Status.READY_TO_LEAVE;
    }

    public boolean isWaitingForService() {
        return getStatus() == Status.WAITING_FOR_SERVICE;
    }

    public boolean isWaitingForWaiter() {
        return getStatus() == Status.WAITING_FOR_WAITER;
    }

    public boolean isWantToLeave() {
        return getStatus() == Status.WANT_TO_LEAVE;
    }

    public double getFreeAt() {
        return freeAt;
    }

    public Long getIdentificationNumber() {
        return identificationNumber;
    }

    public int getMealsOrdered() {
        return mealsOrdered;
    }

    public int getMealsToOrder() {
        return mealsToOrder;
    }

    public ObjectInstanceHandle getObjectInstanceHandle() {
        return objectInstanceHandle;
    }

    public Status getStatus() {
        return status;
    }

    public void giveMeal(Meal meal, double freeAt) {
        status = Status.EATING;

        this.freeAt = freeAt;
    }

    public void letHimFree() {
        if (mealsToOrder < mealsOrdered) {
            status = Status.WAITING_FOR_WAITER;
        } else {
            status = Status.WANT_TO_LEAVE;
        }
    }

    public void mealOrdered() {
        status = Status.WAITING_FOR_MEAL;

        ++mealsOrdered;
    }

    public void readyToLeave() {
        status = Status.READY_TO_LEAVE;
    }

    @Override
    protected void setHandles() throws Exception {
        clientObjectClassHandle = getRtiAmbassador().getObjectClassHandle("HLAobjectRoot.Client");
    }

    public void takeTable(Table table) {
        this.table = table;

        status = Status.WAITING_FOR_WAITER;
    }

    public void waiterIsNotified() {
        status = Status.WAITING_FOR_SERVICE;
    }
}
