package edu.unh.cs.cs619.bulletzone.events;

public class MoveBulletEvent extends GridEvent {

    public MoveBulletEvent(Long tankID, int bulletID, byte direction, int pos) {
        this.ID = (Math.toIntExact(tankID) * 10) + bulletID;
        this.direction = direction;
        this.type = "moveBullet";
        this.time = System.currentTimeMillis();
        this.pos = pos;
    }

}
