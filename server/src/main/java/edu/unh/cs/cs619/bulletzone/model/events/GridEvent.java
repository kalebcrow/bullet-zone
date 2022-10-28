package edu.unh.cs.cs619.bulletzone.model.events;


import java.io.Serializable;


public class GridEvent implements Serializable {

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


}
