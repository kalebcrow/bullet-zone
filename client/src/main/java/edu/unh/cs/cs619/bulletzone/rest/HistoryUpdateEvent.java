package edu.unh.cs.cs619.bulletzone.rest;

import edu.unh.cs.cs619.bulletzone.util.EventWrapper;

public class HistoryUpdateEvent {
    public EventWrapper getHw() {
        return hw;
    }

    public void setHw(EventWrapper hw) {
        this.hw = hw;
    }

    EventWrapper hw;

    public HistoryUpdateEvent(EventWrapper gw) {
        this.hw = gw;
    }

}
