package edu.unh.cs.cs619.bulletzone.util;

import java.io.Serializable;
import java.util.LinkedList;

import edu.unh.cs.cs619.bulletzone.model.events.GridEvent;

public class EventWrapper<T> implements Serializable {

    private LinkedList<T> update;
    private Long timeStamp;

    public EventWrapper(LinkedList<T> update) {
        this.update = update;
        this.timeStamp = System.currentTimeMillis();
    }

    public EventWrapper(){}


    public LinkedList<T> getUpdate() {
        return update;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setUpdate(LinkedList<T> update) {
        this.update = update;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
