package edu.unh.cs.cs619.bulletzone.model;

public class Builder extends Tank{

    private static final String TAG = "Tank";

    private final long id;

    private final String ip;

    private long lastMoveTime;
    private int allowedMoveInterval;

    private long lastFireTime;
    private int allowedFireInterval;

    private int numberOfBullets;
    private int allowedNumberOfBullets;

    private int life;

    private Direction direction;

    public Builder(long id, Direction direction, String ip)
    {
        this.id = id;
        this.ip = ip;
        numberOfBullets = 0;
        allowedNumberOfBullets = 2;
        lastFireTime = 0;
        allowedFireInterval = 1500;
        lastMoveTime = 0;
        allowedMoveInterval = 500;
    }
}
