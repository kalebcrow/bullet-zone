package edu.unh.cs.cs619.bulletzone.model;

import static edu.unh.cs.cs619.bulletzone.model.Direction.toByte;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.events.AddResourceEvent;
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

    private Direction direction;
    private int typeIndex;
    private String username;
    private String ip;
    private long id;
    private PowerUp powerUp;
    private int[] resources;
    //rock = index 0
    //iron = index 1
    //clay = index 2
    //wood = index 3

    private final double[] takesDamage = {.1, .05, .1};
    private final double[] givesDamage = {.1, .1, .05};

    public Tank(String username, long id, Direction direction, String ip, int typeIndex) {
        this.id = id;
        this.username = username;
        this.direction = direction;
        this.ip = ip;
        this.typeIndex = typeIndex;
        powerUp = new UnPowered(typeIndex, id);
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
        int newHealth = powerUp.getHealth() - damage;
        powerUp.setHealth(newHealth);
        System.out.println("Tank life: " + id + " : " + newHealth);
		//Log.d(TAG, "TankId: " + id + " hit -> life: " + life);
        eventManager.addEvent(new DamageEvent(Math.toIntExact(id), newHealth + 1));
        if (newHealth <= 0 ){
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
                ent.hit((int) Math.ceil(powerUp.getHealth() * giveDamageModifier()));
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

    public FieldResource strip(){
        FieldResource fr = powerUp.getFieldElement();
        System.out.println("stripping powerUp");
        if(fr != null) {
            System.out.println("trying to place powerUp");
            if (!parent.getNeighbor(Direction.Up).isEntityPresent()) {
                parent.getNeighbor(Direction.Up).setFieldEntity(fr);
                eventManager.addEvent(new AddResourceEvent(parent.getNeighbor(Direction.Up).getPos()+1,fr.toString()));
            } else if (!parent.getNeighbor(Direction.Down).isEntityPresent()) {
                parent.getNeighbor(Direction.Down).setFieldEntity(fr);
                eventManager.addEvent(new AddResourceEvent(parent.getNeighbor(Direction.Down).getPos()+1,fr.toString()));
            } else if (!parent.getNeighbor(Direction.Left).isEntityPresent()) {
                parent.getNeighbor(Direction.Left).setFieldEntity(fr);
                eventManager.addEvent(new AddResourceEvent(parent.getNeighbor(Direction.Left).getPos()+1,fr.toString()));
            } else if (!parent.getNeighbor(Direction.Right).isEntityPresent()) {
                parent.getNeighbor(Direction.Right).setFieldEntity(fr);
                eventManager.addEvent(new AddResourceEvent(parent.getNeighbor(Direction.Right).getPos()+1,fr.toString()));
            }
        }
        powerUp = powerUp.powerDown();
        return fr;
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
        return (int) (10000000 + 10000 * id + 10 * powerUp.getHealth() + Direction
                .toByte(direction));
    }

    @Override
    public String toString() {
        return "T";
    }

    @Override
    public int getLife() {
        return powerUp.getHealth();
    }

    public void setLife(int life) {powerUp.setHealth(life);}

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


