package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Tank extends FieldEntity {

    private static final String TAG = "Tank";

    private final long id;

    private final String ip;

    private long lastMoveTime;
    private final int[] allowedMoveIntervals = {500,800,1000};
    private final int[] allowedTurnIntervals = {500,800,300};

    private long lastFireTime;
    private final int[] allowedFireIntervals = {1500,200,1000};

    private int numberOfBullets;
    private final int[] allowedNumbersOfBullets = {2,4,6};

    private final int[] healths = {100,300,80};
    private int life;

    private Direction direction;

    private int typeIndex;

    public Tank(long id, Direction direction, String ip, int typeIndex) {
        this.id = id;
        this.direction = direction;
        this.ip = ip;
        this.typeIndex = typeIndex;
        this.life = healths[typeIndex];
    }

    public Tank(){
        ip = null;
        id = 0;
    }

    @Override
    public FieldEntity copy() {
        return new Tank(id, direction, ip, typeIndex);
    }

    @Override
    public void hit(int damage) {
        life = life - damage;
        System.out.println("Tank life: " + id + " : " + life);
//		Log.d(TAG, "TankId: " + id + " hit -> life: " + life);

        if (life <= 0) {
//			Log.d(TAG, "Tank event");
            //eventBus.post(Tank.this);
            //eventBus.post(new Object());
        }
    }

    public long getLastMoveTime() {
        return lastMoveTime;
    }
    public void setLastMoveTime(long lastMoveTime) {
        this.lastMoveTime = lastMoveTime;
    }
    public long getAllowedMoveInterval() { return allowedMoveIntervals[typeIndex]; }
    public long getAllowedTurnInterval() { return allowedTurnIntervals[typeIndex]; }


    public long getLastFireTime() {
        return lastFireTime;
    }
    public void setLastFireTime(long lastFireTime) { this.lastFireTime = lastFireTime; }
    public long getAllowedFireInterval() { return allowedFireIntervals[typeIndex]; }

    public int getNumberOfBullets() {
        return numberOfBullets;
    }
    public void setNumberOfBullets(int numberOfBullets) {
        this.numberOfBullets = numberOfBullets;
    }
    public int getAllowedNumberOfBullets() { return allowedNumbersOfBullets[typeIndex]; }

    public Direction getDirection() {
        return direction;
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @Override
    public int getIntValue() {
        return (int) (10000000 + 10000 * id + 10 * life + Direction
                .toByte(direction));
    }

    @Override
    public String toString() {
        return "T";
    }

    public int getLife() {
        return life;
    }

    public String getIp(){
        return ip;
    }

}
