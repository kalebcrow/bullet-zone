package edu.unh.cs.cs619.bulletzone.model;

import static edu.unh.cs.cs619.bulletzone.model.Direction.toByte;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.events.DamageEvent;
import edu.unh.cs.cs619.bulletzone.events.DestroyTankEvent;
import edu.unh.cs.cs619.bulletzone.events.EventManager;
import edu.unh.cs.cs619.bulletzone.events.MoveTankEvent;
import edu.unh.cs.cs619.bulletzone.repository.DataRepository;
import jdk.internal.org.jline.utils.Log;


public class Tank extends FieldEntity {
    // typeIndex 0 for tank, 1 for miner, 2 for builder

    private static final String TAG = "Tank";
    private static Game game;
    private EventManager eventManager = EventManager.getInstance();
    private DataRepository data = new DataRepository();

    public boolean allowMovement = true;
    private long lastFireTime = System.currentTimeMillis();
    private long lastMoveTime = System.currentTimeMillis();
    private int numberOfBullets = 0;

    private int life;
    private Direction direction;
    private int typeIndex;
    private String username;
    private String ip;
    private long id;
    private PowerUp powerUp = new UnPowered(typeIndex);
    private int[] resources;
    //rock = index 0
    //iron = index 1
    //clay = index 2
    //wood = index 3

    private final double[] takesDamage = {.1, .05, .1};
    private final double[] givesDamage = {.1, .1, .05};
    private final int[] healths = {100,300,80};

    public Tank(String username, long id, Direction direction, String ip, int typeIndex) {
        this.id = id;
        this.username = username;
        this.direction = direction;
        this.ip = ip;
        this.typeIndex = typeIndex;
        this.life = healths[typeIndex];
        if (typeIndex == 1) {
            resources = new int[]{0,0,0,0};
        }
    }

    public Tank(){
        ip = null;
        id = 0;
        typeIndex = 0;
    }

    @Override
    public FieldEntity copy(){ return new Tank(username,id, direction, ip, typeIndex); }

    @Override
    public void hit(int damage) {
        life -= damage;
        System.out.println("Tank life: " + id + " : " + life);
		//Log.d(TAG, "TankId: " + id + " hit -> life: " + life);
        eventManager.addEvent(new DamageEvent(Math.toIntExact(id), life + 1));
        if (life <= 0 ){
            eventManager.addEvent(new DestroyTankEvent(id));
            parent.clearField();
            parent = null;
        }
    }

    public void enhance(Powered power){
        power.setSubject(powerUp);
        powerUp = power;
    }

    public boolean advance(Direction direction){

        FieldHolder nextField = parent.getNeighbor(direction);
        boolean present = nextField.isEntityPresent();
        FieldEntity ent = null;
        if(present) ent = nextField.getEntity();

        if (!present || ent.gather(this)) {
            nextField.setFieldEntity(this);
            parent.clearField();
            setParent(nextField);
            eventManager.addEvent(new MoveTankEvent(id, toByte(direction)));
            return true;
        } else {
                // hit the whatever is there
                ent.hit((int) Math.ceil(life * giveDamageModifier()));
                // do appropriate damage to tank
                hit((int) Math.floor(ent.getLife() * getDamageModifier()));
                return false;
            }
        }

    public long getAllowedMoveInterval(){ return powerUp.getAllowedMoveInterval(); }
    public long getAllowedTurnInterval(){ return powerUp.getAllowedTurnInterval(); }
    public long getAllowedFireInterval(){ return powerUp.getAllowedFireInterval(); }
    public int getAllowedNumberOfBullets(){ return powerUp.getAllowedNumberOfBullets(); }

    public long getLastMoveTime() { return lastMoveTime; }
    public void setLastMoveTime(long lastMoveTime) { this.lastMoveTime = lastMoveTime; }

    public long getLastFireTime() { return lastFireTime; }
    public void setLastFireTime(long lastFireTime) { this.lastFireTime = lastFireTime; }

    public int getNumberOfBullets() { return numberOfBullets; }
    public void setNumberOfBullets(int numberOfBullets) { this.numberOfBullets = numberOfBullets; }

    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }

    public void strip(){
        powerUp = powerUp.powerDown();
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @JsonIgnore
    public String getUsername() {
        return username;
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

    @Override
    public int getLife() {
        return life;
    }

    public void setLife(int life) {this.life = life;}

    public String getIp(){
        return ip;
    }
    public int getTypeIndex(){return typeIndex;}

    public boolean addBundleOfResources(int resourceType, int amount) {
        if (resourceType < 0 || resourceType >= 4) {
            return false;
        }
        resources[resourceType]+= amount;
        return true;
    }

    public boolean subtractBundleOfResources(int resourceType, int amount) {
        if (resourceType < 0 || resourceType >= 4) {
            return false;
        }
        if (resources[resourceType] < amount) {
            return false;
        }
        resources[resourceType] -= amount;
        return true;
    }

    public Integer getResourcesByResource(int resourceType) {
        return resources[resourceType];
    }

    public int[] getAllResources() {
        return resources;
    }

    public double getDamageModifier() {
        return takesDamage[typeIndex];
    }

    public double giveDamageModifier() {
        return givesDamage[typeIndex];
    }

    public static void setGame(Game g){game = g;}
}


