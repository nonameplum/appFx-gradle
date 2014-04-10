package appFx.eventbus.events;

public class SimpleActionEvent extends Event {

    private String someText;

    public String getSomeText() {
        return someText;
    }

    public SimpleActionEvent(String someText) {
        this.someText = someText;
    }

}
