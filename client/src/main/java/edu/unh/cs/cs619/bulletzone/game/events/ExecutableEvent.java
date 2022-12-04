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
        terrain = gridEvent.getTerrain();
        resource = gridEvent.getResource();
        buildType = gridEvent.buildType;
        resources = gridEvent.resources;
    }

    protected Long time;
    protected String type;
    protected Integer ID = -1;
    protected byte direction = -1;
    protected int pos = -1;
    protected String terrain = "";
    protected String resource = "";
    public TileUpdateEvent tileUpdateEvent;
    protected int[] resources = new int[]{0, 0, 0};
    protected Integer buildType;


    /**
     *
     * @return String representing resource
     */
    public String getResource() {
        return resource;
    }

    /**
     *
     * @param resource String representing the resource
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     *
     * @return String representing terrain
     */
    public String getTerrain() {
        return terrain;
    }

    /**
     *
     * @param terrain String representing Terrain
     */
    public void setTerrain(String terrain) {
        this.terrain = terrain;
    }


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
     * Converts the json value to the string value
     *
     * @param terrain terrain in string form
     * @return terrain in json form
     */
    public Integer getJSONValueFromString(String terrain) {
        // using given json values
        if (terrain.equals("W")) {
            return 50;
        } else if (terrain.equals("F")) {
            return 3;
        } else if (terrain.equals("H")) {
            return 2;
        } else if (terrain.equals("R")) {
            return 1;
        } else if (terrain.equals("M")) {
            return 0;
        } else {
            return -1; // something is wrong
        }
    }

    /**
     *
     * @param bus Bus to send events on
     */
    public void execute(Bus bus) {

    }

}
