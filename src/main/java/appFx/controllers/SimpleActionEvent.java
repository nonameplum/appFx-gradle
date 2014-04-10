package appFx.controllers;

import appFx.eventbus.events.Event;

public class SimpleActionEvent extends Event {

    private String anotherText;

    public String getAnotherText() {
        return anotherText;
    }

    public SimpleActionEvent(String anotherText) {
        this.anotherText = anotherText;
    }
}
