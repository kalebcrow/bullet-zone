package edu.unh.cs.cs619.bulletzone.events;

public class DestroyBulletEvent extends GridEvent{

    public DestroyBulletEvent(Long tankID, Integer bulletID){
        this.ID =  (Math.toIntExact(tankID) * 10) + bulletID;
        this.type = "destroyBullet";
        this.time = System.currentTimeMillis();
    }
}
