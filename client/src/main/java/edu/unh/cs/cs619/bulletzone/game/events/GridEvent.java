package edu.unh.cs.cs619.bulletzone.game.events;

import org.androidannotations.annotations.Bean;

import java.io.Serializable;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public abstract class GridEvent implements Serializable {

    //Time of event which is needed for getting the right events for the client
    protected Long time;
    //type of event
    protected String type;
    BusProvider busProvider;


    /**
     *
     * @return time
     */
    public Long getTime(){return this.time;}

    /**
     * runs the command updating the board
     */
    public void execute() {
    }

}
