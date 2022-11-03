package edu.unh.cs.cs619.bulletzone.game.events;

import java.io.Serializable;


public class GridEvent implements Serializable {

    protected Long time;
    protected String type;
    protected Integer ID = -1;
    protected byte direction = -1;
    protected int pos = -1;

    public int getTankType() {
        return tankType;
    }

    public void setTankType(int tankType) {
        this.tankType = tankType;
    }

    //1 is regular 2 is builder 3 is steve
    protected int tankType = -1;

    /**
     *
     * @return ID of tank or bullet
     */
    public Integer getID() {
        return ID;
    }

    /**
     *
     * @param ID ID of tank or bullet
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
     * @param direction direction of tank
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
     * @param pos location
     */
    public void setPos(int pos) {
        this.pos = pos;
    }

    /**
     *
     * @param time time
     */
    public void setTime(Long time) {
        this.time = time;
    }

    /**
     *
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type setType
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return time
     */
    public Long getTime(){
        return this.time;
    }


}