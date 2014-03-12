package appFx.eventbus.events;


import appFx.eventbus.EventBusHandler;
import appFx.models.Something;

public class SomethingEvent extends EventBusHandler<Something> {

    public SomethingEvent(Something value) {
        super(value);
    }

}
