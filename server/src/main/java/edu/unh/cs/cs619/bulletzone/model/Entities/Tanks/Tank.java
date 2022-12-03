package edu.unh.cs.cs619.bulletzone.model.Entities.Tanks;

import static edu.unh.cs.cs619.bulletzone.model.Miscellaneous.Direction.toByte;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.events.DamageEvent;
import edu.unh.cs.cs619.bulletzone.events.DestroyTankEvent;
import edu.unh.cs.cs619.bulletzone.events.EventManager;
import edu.unh.cs.cs619.bulletzone.events.MineEvent;
import edu.unh.cs.cs619.bulletzone.events.MoveTankEvent;
import edu.unh.cs.cs619.bulletzone.events.balanceEvent;
import edu.unh.cs.cs619.bulletzone.model.Entities.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Miscellaneous.Direction;
import edu.unh.cs.cs619.bulletzone.model.Miscellaneous.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Miscellaneous.Game;
import edu.unh.cs.cs619.bulletzone.model.Entities.GameResources.FieldResource;
import edu.unh.cs.cs619.bulletzone.model.Entities.GameResources.Thingamajig;
import edu.unh.cs.cs619.bulletzone.repository.DataRepository;


public abstract class Tank extends FieldEntity {

    // typeIndex 0 for tank, 1 for miner, 2 for builder

    private static final String TAG = "Tank";
    private static Game game;
    private EventManager eventManager = EventManager.getInstance();
    private DataRepository data = new DataRepository();

    public boolean allowMovement = true;
    private long lastFireTime = 0;
    private long lastMoveTime = 0;
    private int numberOfBullets = 0;

    protected int life;
    protected Direction direction;
    protected int typeIndex;
    protected String username;
    protected String ip;
    protected long id;
    protected int[] resources;
    //rock = index 0
    //iron = index 1
    //clay = index 2

    private final double[] takesDamage = {.1, .05, .1};
    private final double[] givesDamage = {.1, .1, .05};
    private final int[] healths = {100,300,80};

    @Override
    abstract public FieldEntity copy();

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

    public boolean advance(Direction direction){

        FieldHolder nextField = parent.getNeighbor(direction);
        boolean present = nextField.isEntityPresent();
        FieldEntity ent = null;
        if(present) ent = nextField.getEntity();

        if (!present || present && ent.gather(this)) {
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

    public long getLastMoveTime() {
        return lastMoveTime;
    }
    public void setLastMoveTime(long lastMoveTime) {
        this.lastMoveTime = lastMoveTime;
    }
    public abstract long getAllowedMoveInterval();
    public abstract long getAllowedTurnInterval();


    public long getLastFireTime() { return lastFireTime; }
    public void setLastFireTime(long lastFireTime) { this.lastFireTime = lastFireTime; }
    public abstract long getAllowedFireInterval();

    public int getNumberOfBullets() {
        return numberOfBullets;
    }
    public void setNumberOfBullets(int numberOfBullets) {
        this.numberOfBullets = numberOfBullets;
    }
    public abstract int getAllowedNumberOfBullets();

    public Direction getDirection() {
        return direction;
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public abstract Tank strip();

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

    public String getIp(){
        return ip;
    }
    public int getTypeIndex(){return typeIndex;}

    public boolean addBundleOfResources(int resourceType, int amount) {
        if (resourceType < 0 || resourceType >= 3) {
            return false;
        }
        resources[resourceType]+= amount;
        return true;
    }

    public boolean subtractBundleOfResources(int resourceType, int amount) {
        if (resourceType < 0 || resourceType >= 3) {
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

    private boolean isResource(FieldHolder nextField) {
        if (nextField.isEntityPresent()) {
            FieldEntity fr = nextField.getEntity();
            return fr.getIntValue() == 501 || fr.getIntValue() == 502 ||
                    fr.getIntValue() == 503 || fr.getIntValue() == 7;

        }
        return false;
    }

    public static void setGame(Game g){game = g;}
}


