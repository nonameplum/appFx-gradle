package appFx.eventbus.events;

import appFx.models.Something;

public class SomethingModelActionEvent extends Event {

    private Something model;

    public Something getModel() {
        return model;
    }

    public SomethingModelActionEvent(Something model) {
        this.model = model;
    }
}
