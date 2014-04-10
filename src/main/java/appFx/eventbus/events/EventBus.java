package appFx.eventbus.events;

public interface EventBus {
    public <T extends Event> void addListener(Class<T> type, EventListener<T> listener);
    public <T extends Event> void removeListener(Class<T> type, EventListener<T> listener);
    public <T extends Event> void fireEvent(T event);
}