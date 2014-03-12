package appFx.eventbus;

import reactfx.EventSource;

import java.util.HashMap;
import java.util.Map;

public class EventBusContext<T> {
    private final static EventBusContext instance = new EventBusContext();

    public static EventBusContext getInstance() {
        return instance;
    }

    Map<Class, EventSource> eventBusMap = new HashMap<>();

    public <T extends EventBusHandler> void registerEventSource(Class<T> clazz, EventSource<T> eventSource) {
        eventBusMap.put(clazz, eventSource);
    }

    public <T extends EventBusHandler> EventSource<?> getEventSource(Class<T> clazz) {
        EventSource<T> eventSource = eventBusMap.get(clazz);
        return eventSource;
    }
}
