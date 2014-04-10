package appFx.eventbus.events;

import java.util.*;

public class SimpleEventBus implements EventBus {

    private Map<Class, List<EventListener>> listenersMap;

    protected Map<Class, List<EventListener>> getListenersMap() {
        return listenersMap;
    }

    public SimpleEventBus() {
        listenersMap = new HashMap<>();
    }

    @Override
    public <T extends Event> void addListener(Class<T> type, EventListener<T> listener) {
        if (type == null || listener == null) {
            throw new IllegalArgumentException("one of the parameter is NULL");
        }
        if (!listenersMap.containsKey(type)) {
            listenersMap.put(type, new ArrayList<EventListener>());
        }
        List<EventListener> handlers = listenersMap.get(type);
        if (!handlers.contains(listener)) {
            handlers.add(listener);
        }
    }

    @Override
    public <T extends Event> void fireEvent(T event) {
        if (listenersMap.containsKey(event.getClass())) {
            List<EventListener> listeners = listenersMap.get(event.getClass());
            for (Iterator<EventListener> it = listeners.iterator(); it.hasNext(); ) {
                EventListener eventHandler = it.next();
                eventHandler.notify(event);
            }
        }
    }

    @Override
    public <T extends Event> void removeListener(Class<T> type, EventListener<T> listener) {
        if (listenersMap.containsKey(type)) {
            List<EventListener> listeners = listenersMap.get(type);
            if (listeners.contains(listener))
                listeners.remove(listener);
        }
    }
}