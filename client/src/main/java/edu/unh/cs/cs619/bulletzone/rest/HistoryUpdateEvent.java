package edu.unh.cs.cs619.bulletzone.rest;

import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.HistoryWrapper;

public class HistoryUpdateEvent {
    HistoryWrapper hw;

    public HistoryUpdateEvent(HistoryWrapper gw) {
        this.hw = gw;
    }

}
