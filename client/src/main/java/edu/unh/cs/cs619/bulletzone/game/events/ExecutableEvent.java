package edu.unh.cs.cs619.bulletzone.game.events;

import org.androidannotations.annotations.Bean;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;

public class ExecutableEvent {

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


    public void execute(BusProvider busProvider) {

    }

}
