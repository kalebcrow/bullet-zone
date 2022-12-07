package edu.unh.cs.cs619.bulletzone.model;

public class UnPowered extends PowerUp {

    private int[] allowedMoveIntervals;
    private int[] allowedTurnIntervals;
    private int[] allowedFireIntervals;
    private int[] allowedNumberOfBullets;
    private int typeIndex;

    UnPowered(int typeIndex){
        this.allowedMoveIntervals = new int[]{500,800,1000};
        this.allowedTurnIntervals = new int[]{500,800,300};
        this.allowedFireIntervals = new int[]{1500,200,1000};
        this.allowedNumberOfBullets = new int[]{2,4,6};
        this.typeIndex = typeIndex;
    }

    @Override
    public long getAllowedMoveInterval() {
        return allowedMoveIntervals[typeIndex];
    }

    @Override
    public long getAllowedTurnInterval() {
        return allowedTurnIntervals[typeIndex];
    }

    @Override
    public long getAllowedFireInterval() {
        return allowedFireIntervals[typeIndex];
    }

    @Override
    public int getAllowedNumberOfBullets() {
        return allowedNumberOfBullets[typeIndex];
    }

    @Override
    public PowerUp powerDown() {
        return this;
    }
}
