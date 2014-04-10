package appFx.eventbus.events;

public enum  EventBusContext {

    INSTANCE;

    public SimpleEventBus eventBus = new SimpleEventBus();

}
