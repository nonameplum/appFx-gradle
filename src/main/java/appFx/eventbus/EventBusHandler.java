package appFx.eventbus;

public abstract class EventBusHandler<T> {

    private T value;

    public EventBusHandler(T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

}
