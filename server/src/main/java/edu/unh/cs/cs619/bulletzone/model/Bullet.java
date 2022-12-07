package edu.unh.cs.cs619.bulletzone.model;

import static edu.unh.cs.cs619.bulletzone.model.Direction.toByte;

import java.util.Timer;
import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.events.DestroyBulletEvent;
import edu.unh.cs.cs619.bulletzone.events.EventManager;
import edu.unh.cs.cs619.bulletzone.events.FireEvent;
import edu.unh.cs.cs619.bulletzone.events.MoveBulletEvent;

public class Bullet extends FieldEntity {

    private boolean fireIndicator = false;
    private EventManager eventManager = EventManager.getInstance();
    private long tankId;
    private Direction direction;
    private int damage, bulletId;
    private static final int BULLET_PERIOD = 200;

    public Bullet(long tankId, Direction direction, int damage) {
        this.damage = damage;
        this.setTankId(tankId);
        this.setDirection(direction);
    }

    @Override
    public int getIntValue() {
        return (int) (2000000 + 1000 * tankId + damage * 10 + bulletId);
    }

    @Override
    public String toString() {
        return "B";
    }

    @Override
    public FieldEntity copy() {
        return new Bullet(tankId, direction, damage);
    }

    public long getTankId() {
        return tankId;
    }

    public void setTankId(long tankId) {
        this.tankId = tankId;
    }

    public Direction getDirection() {
        return direction;
    }

    public Direction getOppositeDirection() {
        Direction opp = Direction.Down;
        if (direction.compareTo(Direction.Up) == 0) {
            opp = Direction.Down;
        } else if (direction.compareTo(Direction.Down) == 0) {
            opp = Direction.Up;
        } else if (direction.compareTo(Direction.Left) == 0) {
            opp = Direction.Right;
        } else if (direction.compareTo(Direction.Right) == 0) {
            opp = Direction.Left;
        }
        return opp;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setBulletId(int bulletId){
        this.bulletId = bulletId;
    }

    public int getBulletId(){
        return bulletId;
    }

    //does bullet traveling loop
    private final Timer timer = new Timer();

    public void travel(Tank tank){
        setParent(tank.getParent());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                    System.out.println("Active Bullet: "+tank.getNumberOfBullets()+"---- Bullet ID: "+getIntValue());
                    FieldHolder nextField = parent.getNeighbor(direction);

                    if (nextField.isEntityPresent()) {
                        // Something is there, hit it
                        if(fireIndicator){
                            eventManager.addEvent(new DestroyBulletEvent(tankId, bulletId));
                            parent.clearField();
                        }
                        tank.setNumberOfBullets(tank.getNumberOfBullets()-1);
                        if(nextField.getEntity().getParent() == null) nextField.getEntity().setParent(nextField);
                        nextField.getEntity().hit(damage);
                        cancel();
                    } else {
                        if(!fireIndicator){
                            System.out.println("++++++++++++++");
                            eventManager.addEvent(new FireEvent(tankId, bulletId, toByte(direction)));
                            fireIndicator = true;
                            } else {
                            System.out.println("------------------");
                            eventManager.addEvent(new MoveBulletEvent(tankId, bulletId, toByte(direction)));
                            parent.clearField();
                        }
                        nextField.setFieldEntity(copy());
                        setParent(nextField);
                    }
            }
        }, 0, BULLET_PERIOD);
    }
}
