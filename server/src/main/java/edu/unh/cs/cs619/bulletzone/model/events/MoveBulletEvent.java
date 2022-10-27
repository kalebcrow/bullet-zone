package edu.unh.cs.cs619.bulletzone.model.events;

public class MoveBulletEvent extends GridEvent {

    private Long tankID;
    private int bulletID;
    private int direction;

    public MoveBulletEvent(Long tankID, int bulletID, int direction) {
        this.tankID = tankID;
        this.bulletID = bulletID;
        this.direction = direction;
        this.type = "moveBullet";
        this.time = System.currentTimeMillis();
    }
}
