package edu.unh.cs.cs619.bulletzone.model.events;

public class MoveBulletEvent extends GridEvent {

    private Long tankID;
    private Long bulletID;

    public MoveBulletEvent(Long tankID, Long bulletID) {
        this.tankID = tankID;
        this.bulletID = bulletID;
        this.type = "moveBullet";
        this.time = System.currentTimeMillis();
    }
}
