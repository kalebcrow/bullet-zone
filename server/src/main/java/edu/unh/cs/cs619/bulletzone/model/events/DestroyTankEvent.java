package edu.unh.cs.cs619.bulletzone.model.events;

public class DestroyTankEvent extends GridEvent{

    private Long destroyedTankID;
    private Long destroyerTankID;
    private int bulletID;

    public DestroyTankEvent(Long destroyedTankID, Long destroyerTankID, int bulletID) {
        this.destroyedTankID = destroyedTankID;
        this.destroyerTankID = destroyerTankID;
        this.bulletID = bulletID;
        this.type = "destroyTank";
        this.time = System.currentTimeMillis();
    }
}
