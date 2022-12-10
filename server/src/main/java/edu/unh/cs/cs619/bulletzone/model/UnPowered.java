package edu.unh.cs.cs619.bulletzone.model;

public class UnPowered extends PowerUp {

    private final int[] allowedMoveIntervals;
    private final int[] allowedTurnIntervals;
    private final int[] allowedFireIntervals;
    private final int[] allowedNumberOfBullets;
    private int[] maxHealths;
    private int health;
    private int typeIndex;
    private long id;

    UnPowered(int typeIndex, long id){
        this.allowedMoveIntervals = new int[]{500,800,1000};
        this.allowedTurnIntervals = new int[]{500,800,300};
        this.allowedFireIntervals = new int[]{1500,200,1000};
        this.allowedNumberOfBullets = new int[]{2,4,6};
        this.maxHealths = new int[]{100,300,80};
        this.health = maxHealths[typeIndex];
        this.typeIndex = typeIndex;
        this.fieldElement = null;
        this.id = id;
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
    public int getHealth() {
        return health;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public int getMaxHealth(){return maxHealths[typeIndex];}

    @Override
    public void setMaxHealth(int health){maxHealths[typeIndex] = health;}

    @Override
    public PowerUp powerDown() {
        return this;
    }

    @Override
    public long getId(){return id;}
}
