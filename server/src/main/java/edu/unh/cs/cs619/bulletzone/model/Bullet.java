package edu.unh.cs.cs619.bulletzone.model;

import static edu.unh.cs.cs619.bulletzone.model.Direction.toByte;

import java.util.Timer;
import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.events.DamageEvent;
import edu.unh.cs.cs619.bulletzone.events.DestroyBulletEvent;
import edu.unh.cs.cs619.bulletzone.events.DestroyTankEvent;
import edu.unh.cs.cs619.bulletzone.events.DestroyWallEvent;
import edu.unh.cs.cs619.bulletzone.events.FireEvent;
import edu.unh.cs.cs619.bulletzone.events.MoveBulletEvent;

public class Bullet extends FieldEntity {

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
    public void travel(){
        int finalBulletId = bulletId;
        final Long finalTankID = tank.getId();
        final boolean[] fireIndicator = {true};
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (this) {
                    System.out.println("Active Bullet: "+tank.getNumberOfBullets()+"---- Bullet ID: "+bullet.getIntValue());
                    FieldHolder currentField = parent;
                    FieldHolder nextField = currentField
                            .getNeighbor(direction);

                    Direction oppDirection = bullet.getDirection();
                    FieldHolder previousField = currentField
                            .getNeighbor(oppDirection);
                    String previousterrain = previousField.getTerrain().toString();
                    String terrain = currentField.getTerrain().toString();


                    // Is the bullet visible on the field?
                    boolean isVisible = currentField.isEntityPresent()
                            && (currentField.getEntity() == bullet);


                    if (nextField.isEntityPresent()) {
                        // Something is there, hit it
                        nextField.getEntity().hit(bullet.getDamage());
                        if(!fireIndicator[0])game.addEvent(new DestroyBulletEvent(finalTankID, finalBulletId, terrain));

                        if ( nextField.getEntity() instanceof  Tank){
                            Tank t = (Tank) nextField.getEntity();
                            System.out.println("tank is hit, tank life: " + t.getLife());
                            game.addEvent(new DamageEvent(Math.toIntExact(t.getId()), t.getLife()));
                            if (t.getLife() <= 0 ){
                                game.addEvent(new DestroyTankEvent(t.getId(), terrain));
                                t.getParent().clearField();
                                t.setParent(null);
                            }
                        }
                        else if ( nextField.getEntity() instanceof  Wall){
                            Wall w = (Wall) nextField.getEntity();
                            if (w.getIntValue() >1000 && w.getIntValue()<=2000 ){
                                game.addEvent(new DestroyWallEvent(w.getPos()+1, terrain));
                                game.getHolderGrid().get(w.getPos()).clearField();
                            }
                        }
                        if (isVisible) {
                            // Remove bullet from field
                            currentField.clearField();
                        }
                        trackActiveBullets[bullet.getBulletId()]=0;
                        tank.setNumberOfBullets(tank.getNumberOfBullets()-1);
                        cancel();

                    } else {
                        if (isVisible) {
                            if(fireIndicator[0]){
                                game.addEvent(new FireEvent(finalTankID, finalBulletId, toByte(direction)));
                                fireIndicator[0] = false;
                            }
                            else game.addEvent(new MoveBulletEvent(finalTankID, finalBulletId, toByte(direction), terrain));
                            // Remove bullet from field
                            currentField.clearField();
                        }

                        nextField.setFieldEntity(bullet);
                        bullet.setParent(nextField);
                    }
                }
            }
        }, 0, BULLET_PERIOD);
    }
}
