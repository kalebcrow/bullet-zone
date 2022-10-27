package edu.unh.cs.cs619.bulletzone.game.events;

import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public abstract class GridEvent {

    //Time of event which is needed for getting the right events for the client
    protected Long time;
    //type of event
    protected String type;


    public Long getTime(){return this.time;}

    public TileUpdateEvent execute() {
        return null;
    }

}
