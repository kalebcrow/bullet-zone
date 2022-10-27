package edu.unh.cs.cs619.bulletzone.model.events;

import edu.unh.cs.cs619.bulletzone.model.events.GridEvent;

public class FireEvent extends GridEvent {

    private Long tankID;

    public Long getTankID() {
        return tankID;
    }

    public void setTankID(Long tankID) {
        this.tankID = tankID;
    }

    public int getBulletID() {
        return bulletID;
    }

    public void setBulletID(int bulletID) {
        this.bulletID = bulletID;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    private int bulletID;
    private int direction;

    public FireEvent(Long tankID, int bulletID, int direction) {
        this.tankID = tankID;
        this.bulletID = bulletID;
        this.direction = direction;
        this.type = "fire";
        this.time = System.currentTimeMillis();
    }
}
