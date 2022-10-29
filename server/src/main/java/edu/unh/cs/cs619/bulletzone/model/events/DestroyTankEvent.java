package edu.unh.cs.cs619.bulletzone.model.events;

public class DestroyTankEvent extends GridEvent{

    public DestroyTankEvent(Long destroyedTankID) {
        this.ID = Math.toIntExact(destroyedTankID);
        this.type = "destroyTank";
        this.time = System.currentTimeMillis();
    }
}
