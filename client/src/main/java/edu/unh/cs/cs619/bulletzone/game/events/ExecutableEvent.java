package edu.unh.cs.cs619.bulletzone.game.events;

import com.squareup.otto.Bus;

import org.androidannotations.annotations.Bean;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class ExecutableEvent {

    /**
     * an event that can be executed
     * @param gridEvent the rest object to derive info from
     */
    public ExecutableEvent(GridEvent gridEvent) {
        time = gridEvent.getTime();
        type = gridEvent.getType();
        ID = gridEvent.getID();
        direction = gridEvent.getDirection();
        pos = gridEvent.getPos() - 1;
    }

    protected Long time;
    protected String type;
    protected Integer ID = -1;
    protected byte direction = -1;
    protected int pos = -1;
    public TileUpdateEvent tileUpdateEvent;

    /**
     *
     * @return ID of bullet or tank
     */
    public Integer getID() {
        return ID;
    }

    /**
     *
     * @param ID ID of bullet or tank
     */
    public void setID(Integer ID) {
        this.ID = ID;
    }

    /**
     *
     * @return direction
     */
    public byte getDirection() {
        return direction;
    }

    /**
     *
     * @param direction direction
     */
    public void setDirection(byte direction) {
        this.direction = direction;
    }

    /**
     *
     * @return location
     */
    public int getPos() {
        return pos;
    }

    /**
     *
     * @param pos position
     */
    public void setPos(int pos) {
        this.pos = pos;
    }

    /**
     *
     * @param time time of the event
     */
    public void setTime(Long time) {
        this.time = time;
    }

    /**
     *
     * @return command type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type command type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return time of event
     */
    public Long getTime(){
        return this.time;
    }

    /**
     *
     * @param bus Bus to send events on
     */
    public void execute(Bus bus) {

    }

}
