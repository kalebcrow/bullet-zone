package edu.unh.cs.cs619.bulletzone.game.events;

import org.androidannotations.annotations.Bean;

import java.io.Serializable;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class GridEvent implements Serializable {

    protected Long time;
    protected String type;
    protected Integer ID;
    protected byte direction;
    protected int pos;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public byte getDirection() {
        return direction;
    }

    public void setDirection(byte direction) {
        this.direction = direction;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTime(){
        return this.time;
    }


}
