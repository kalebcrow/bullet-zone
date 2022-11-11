package edu.unh.cs.cs619.bulletzone.util;

import java.io.Serializable;
import java.util.LinkedList;

import edu.unh.cs.cs619.bulletzone.game.events.GridEvent;

public class EventWrapper implements Serializable {
    private LinkedList<GridEvent> update;
    private Long timeStamp;

    public EventWrapper(LinkedList<GridEvent> update) {
        this.update = update;
        this.timeStamp = update.getFirst().getTime();
    }

    public EventWrapper(){

    }

    public LinkedList<GridEvent> getUpdate() {
        return update;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setUpdate(LinkedList<GridEvent> update) {
        this.update = update;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}