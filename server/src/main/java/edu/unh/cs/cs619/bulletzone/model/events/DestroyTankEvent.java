package edu.unh.cs.cs619.bulletzone.model.events;

public class DestroyTankEvent extends GridEvent{

    private Long destroyedTankID;

    public Long getDestroyedTankID() {
        return destroyedTankID;
    }

    public void setDestroyedTankID(Long destroyedTankID) {
        this.destroyedTankID = destroyedTankID;
    }

    public Long getDestroyerTankID() {
        return destroyerTankID;
    }

    public void setDestroyerTankID(Long destroyerTankID) {
        this.destroyerTankID = destroyerTankID;
    }

    public int getBulletID() {
        return bulletID;
    }

    public void setBulletID(int bulletID) {
        this.bulletID = bulletID;
    }

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
