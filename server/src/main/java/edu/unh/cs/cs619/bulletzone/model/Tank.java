package edu.unh.cs.cs619.bulletzone.model;

import static edu.unh.cs.cs619.bulletzone.model.Direction.toByte;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.events.DamageEvent;
import edu.unh.cs.cs619.bulletzone.events.DestroyTankEvent;
import edu.unh.cs.cs619.bulletzone.events.EventManager;
import edu.unh.cs.cs619.bulletzone.events.MineEvent;
import edu.unh.cs.cs619.bulletzone.events.MoveTankEvent;
import edu.unh.cs.cs619.bulletzone.events.balanceEvent;
import edu.unh.cs.cs619.bulletzone.repository.DataRepository;
import jdk.internal.org.jline.utils.Log;


public class Tank extends FieldEntity {
    // typeIndex 0 for tank, 1 for miner, 2 for builder

    private static final String TAG = "Tank";
    private static Game game;
    private final long id;
    private final String ip;
    private EventManager eventManager = EventManager.getInstance();
    private DataRepository data = new DataRepository();

    public boolean allowMovement = true;
    private long lastMoveTime;
    private final int[] allowedMoveIntervals = {500,800,1000};
    private final int[] allowedTurnIntervals = {500,800,300};
    private final double[] takesDamage = {.1, .05, .1};
    private final double[] givesDamage = {.1, .1, .05};

    private long lastFireTime;
    private final int[] allowedFireIntervals = {1500,200,1000};

    private int numberOfBullets;
    private final int[] allowedNumbersOfBullets = {2,4,6};

    private final int[] healths = {100,300,80};
    private int life;

    private int[] resources;
    //rock = index 0
    //iron = index 1
    //clay = index 2

    private Direction direction;

    private int typeIndex;
    private String username;

    public Tank(String username, long id, Direction direction, String ip, int typeIndex) {
        this.id = id;
        this.username = username;
        this.direction = direction;
        this.ip = ip;
        this.typeIndex = typeIndex;
        this.life = healths[typeIndex];
        if (typeIndex == 1) {
            resources = new int[]{0,0,0};
        }
        this.lastMoveTime = 0;
        this.lastFireTime = 0;
        this.numberOfBullets = 0;
    }

    public Tank(){
        ip = null;
        id = 0;
        typeIndex = -1;
    }

    @Override
    public FieldEntity copy() {
        return new Tank(username, id, direction, ip, typeIndex);
    }

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

    public boolean moveTank(Direction direction){
        boolean isCompleted;
        FieldHolder nextField = parent.getNeighbor(direction);
        // check if next field is empty and go there if it is
        if (!nextField.isEntityPresent()) {
            // and a tank/builder on a forest or tank/miner on a water is not allowed
            if  ((typeIndex != 2 && nextField.getTerrain().toString().equals("F"))
                    || (typeIndex != 1 && nextField.getTerrain().toString().equals("W"))) {
                return false;
            }
            nextField.setFieldEntity(parent.getEntity());
            parent.clearField();
            setParent(nextField);
            eventManager.addEvent(new MoveTankEvent(id, toByte(direction)));
            isCompleted = true;
        } else { // if it's not then you have to "hit" whatever is there
            isCompleted = false;
            FieldEntity ent = nextField.getEntity();
            if (ent.toString().equals("IW")){
                // you can't "hit" indestructible wall OR deso nothing happens
                return false;
            }
            if (isResource(nextField)) {
                //Grab Miner
                isCompleted = true;
                Tank miner = new Tank();
                HashMap<String, Long> tanks = game.getTanks(getIp());
                assert tanks != null;
                if (tanks.containsKey("miner"))
                    miner = game.getTank(tanks.get("miner"));
                if (miner.getTypeIndex() == 1) { //Incase this is not the miner
                    FieldResource fr = (FieldResource) nextField.getEntity();
                    if (fr.getIntValue() == 503) { //iron
                        miner.addBundleOfResources(1, 1);
                        System.out.println("Finished item pickup process, adding iron to stash");
                        eventManager.addEvent(new MineEvent(id, miner.getAllResources()));
                    } else if (fr.getIntValue() == 502) { //rock
                        miner.addBundleOfResources(0, 1);
                        System.out.println("Finished item pickup process, adding rock to stash");
                        eventManager.addEvent(new MineEvent(id, miner.getAllResources()));
                    } else if (fr.getIntValue() == 501) { //clay
                        miner.addBundleOfResources(2, 1);
                        System.out.println("Finished item pickup process, adding clay to stash");
                        eventManager.addEvent(new MineEvent(id, miner.getAllResources()));
                    } else if (fr.getIntValue() == 7) {
                        Thingamajig tb = (Thingamajig) fr;
                        double amount = tb.getCredits();
                        data.modifyAccountBalance(getUsername(), amount);
                        eventManager.addEvent(new balanceEvent(data.getUserAccountBalance(getUsername()), id));
                    } else {
                        System.out.println("Resource ID does not exist");
                    }

                }
                nextField.setFieldEntity(parent.getEntity());
                parent.clearField();
                setParent(nextField);
                eventManager.addEvent(new MoveTankEvent(id, toByte(direction)));
            }
            else {
                // hit the whatever is there
                ent.hit((int) Math.ceil(life * giveDamageModifier()));
                // do appropriate damage to tank
                hit((int) Math.floor(ent.getLife() * getDamageModifier()));
            }
            }
        return isCompleted;
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


