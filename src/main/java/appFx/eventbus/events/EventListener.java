package appFx.eventbus.events;

public interface EventListener<T extends Event> extends java.util.EventListener {
    void notify(T e);
}
