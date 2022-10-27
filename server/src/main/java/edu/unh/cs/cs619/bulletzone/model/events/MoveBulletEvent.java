package edu.unh.cs.cs619.bulletzone.model.events;

public class MoveBulletEvent extends GridEvent {

    private Long tankID;
    private int bulletID;
    private byte direction;

    public MoveBulletEvent(Long tankID, int bulletID, byte direction) {
        this.tankID = tankID;
        this.bulletID = bulletID;
        this.direction = direction;
        this.type = "moveBullet";
        this.time = System.currentTimeMillis();
    }

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

    public void setDirection(byte direction) {
        this.direction = direction;
    }
}
