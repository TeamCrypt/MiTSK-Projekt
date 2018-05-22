package mitsk;

import hla.rti1516e.RTIambassador;

public abstract class AbstractObject extends RTIConnectable implements DestructableInterface {
    protected AbstractObject(RTIambassador rtiAmbassador) throws Exception {
        super(rtiAmbassador);
    }
}
