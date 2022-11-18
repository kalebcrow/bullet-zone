package edu.unh.cs.cs619.bulletzone.events;


import java.io.Serializable;
import java.util.Optional;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;


public class GridEvent implements Serializable {

    protected Long time;
    protected String type;
    protected Integer ID = -1;
    protected byte direction = -1;
    protected int pos = -1;
    protected int[] resources = new int[]{0, 0, 0};
    protected String terrain = "";
    protected int buildType = -1;

    public String getTerrain() {
        return terrain;
    }

    public void setTerrain(String terrain) {
        this.terrain = terrain;
    }

    public int getTankType() {
        return tankType;
    }

    public void setTankType(int tankType) {
        this.tankType = tankType;
    }

    //1 is regular 2 is builder 3 is steve
    protected int tankType = -1;

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

    public int[] getResources() {
        return resources;
    }

    public void setResources(int[] resources) {
        this.resources = resources;
    }


}
