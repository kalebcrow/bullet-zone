package edu.unh.cs.cs619.bulletzone.model.events;

public class DestroyBulletEvent extends GridEvent{

    public DestroyBulletEvent(Long tankID, Integer bulletID){
        this.ID =  Math.toIntExact(tankID)*10+bulletID;
        this.type = "destroyTank";
        this.time = System.currentTimeMillis();
    }
}
