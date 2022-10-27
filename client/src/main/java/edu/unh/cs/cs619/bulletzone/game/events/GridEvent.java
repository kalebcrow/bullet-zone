package edu.unh.cs.cs619.bulletzone.game.events;

import org.androidannotations.annotations.Bean;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public abstract class GridEvent {

    //Time of event which is needed for getting the right events for the client
    protected Long time;
    //type of event
    protected String type;
    BusProvider busProvider;



    public Long getTime(){return this.time;}

    public void execute() {
    }

}
