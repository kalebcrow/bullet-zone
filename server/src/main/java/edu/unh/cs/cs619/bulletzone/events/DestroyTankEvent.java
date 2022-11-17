package edu.unh.cs.cs619.bulletzone.events;

public class DestroyTankEvent extends GridEvent{

    public DestroyTankEvent(Long destroyedTankID, String terrain) {
        this.ID = Math.toIntExact(destroyedTankID);
        this.type = "destroyTank";
        this.time = System.currentTimeMillis();
        this.terrain = terrain;
    }
}
