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
import edu.unh.cs.cs619.bulletzone.events.PortalEvent;
import edu.unh.cs.cs619.bulletzone.events.RestrictionsEvent;
import edu.unh.cs.cs619.bulletzone.events.balanceEvent;
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

    //Make it so dock doesn't count as a disable
    public void setRestrictions() {
        int[] restrictions = new int[]{1,1,1,1,1};
        switch(getTypeIndex()) {
            case 0: //tank
                if (isWaterOrForest(getParent().getNeighbor(getDirection()))) {
                    restrictions[0] = 0;
                }
                if (isWaterOrForest(getParent().getNeighbor(Direction.fromByte((byte) ((Direction.toByte(getDirection()) + 4) % 8))))) {
                    restrictions[1] = 0;
                }
                if (getNumberOfBullets() >= getAllowedNumberOfBullets()) {
                    restrictions[2] = 0;
                }
                break;
            case 1: //miner
                System.out.println("Forward: " + getParent().getNeighbor(getDirection()).getTerrain());
                System.out.println("Backwards: " + getParent().getNeighbor(Direction.fromByte((byte) ((Direction.toByte(getDirection()) + 4) % 8))).getTerrain());
                String test = getParent().getNeighbor(getDirection()).getTerrain().toString();
                System.out.println("Actual Forward: " + test);

                if (getParent().getNeighbor(getDirection()).getTerrain().toString().equals("W")){
                    restrictions[0] = 0;
                }
                if (getParent().getNeighbor(Direction.fromByte((byte) ((Direction.toByte(getDirection()) + 4) % 8))).getTerrain().toString().equals("W")) {
                    restrictions[1] = 0;
                }
                if (getNumberOfBullets() >= getAllowedNumberOfBullets()) {
                    restrictions[2] = 0;
                }
                break;
            case 2: //builder
                if (getParent().getNeighbor(getDirection()).getTerrain().toString().equals("F")) {
                    restrictions[0] = 0;
                }
                if (getParent().getNeighbor(Direction.fromByte((byte) ((Direction.toByte(getDirection()) + 4) % 8))).getTerrain().toString().equals("F")) {
                    restrictions[1] = 0;
                }
                if (getNumberOfBullets() >= getAllowedNumberOfBullets()) {
                    restrictions[2] = 0;
                }
                break;
        }
        eventManager.addEvent(new RestrictionsEvent(getId(), restrictions));
    }

    public void enhance(Powered power){
        power.setSubject(powerUp);
        powerUp = power;
    }

    public boolean advance(Direction direction){
        boolean isCompleted;
        FieldHolder nextField = parent.getNeighbor(direction);
        // check if next field is empty and go there if it is
        if (!nextField.isEntityPresent()) {
            if(nextField.isImprovementPresent())
            {
                if(nextField.getImprovement().toString() == "P")
                {
                    Portal p = (Portal)nextField.getImprovement();
                    if(p.direction == direction) {
                        nextField = p.exit.getParent().getNeighbor(p.exit.direction);
                        nextField.setFieldEntity(parent.getEntity());
                        parent.clearField();
                        setParent(nextField);
                        byte Ndirection = (byte) (Direction.toByte(p.exit.direction) - Direction.toByte(p.direction));
                        byte Ydirection = (byte) ((Direction.toByte(this.direction) + Ndirection) % 8);
                        if (Ydirection < 0) {
                            Ydirection = (byte) (Ydirection + 8);
                        }
                        this.direction = Direction.fromByte(Ydirection);
                        eventManager.addEvent(new PortalEvent(id, Ydirection, parent.getPos() + 1));
                        isCompleted = true;
                    }
                    else
                    {
                        nextField.setFieldEntity(parent.getEntity());
                        parent.clearField();
                        setParent(nextField);
                        eventManager.addEvent(new MoveTankEvent(id, toByte(direction), parent.getPos()));
                        isCompleted = true;
                    }
                }
                else
                {
                    nextField.setFieldEntity(parent.getEntity());
                    parent.clearField();
                    setParent(nextField);
                    eventManager.addEvent(new MoveTankEvent(id, toByte(direction), parent.getPos()));
                    isCompleted = true;
                }
            }
            else
            {
                nextField.setFieldEntity(parent.getEntity());
                parent.clearField();
                setParent(nextField);
                eventManager.addEvent(new MoveTankEvent(id, toByte(direction), parent.getPos()));
                isCompleted = true;
            }
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
                    } else if (fr.getIntValue() == 504) {
                        miner.addBundleOfResources(3, 1);
                        System.out.println("Finished item pickup process, adding wood to stash");
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
                eventManager.addEvent(new MoveTankEvent(id, toByte(direction), parent.getPos()));
            }
            else {
        boolean present = nextField.isEntityPresent();
        FieldEntity ent = null;
        if(present) ent = nextField.getEntity();

        if (!present || ent.gather(this)) {
            nextField.setFieldEntity(this);
            parent.clearField();
            setParent(nextField);
            eventManager.addEvent(new MoveTankEvent(id, toByte(direction)));
            isCompleted = true;
        } else {
                // hit the whatever is there
                ent.hit((int) Math.ceil(life * giveDamageModifier()));
                // do appropriate damage to tank
                hit((int) Math.floor(ent.getLife() * getDamageModifier()));
                isCompleted = false;
            }
        if (isCompleted) {
            setRestrictions();
            System.out.println("Restrictions added");
        }
        return isCompleted;
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
        if(powerUp.getFieldElement() != null) {
            if (!parent.getNeighbor(Direction.Up).isEntityPresent()) {
                parent.getNeighbor(Direction.Up).setFieldEntity(powerUp.getFieldElement());
                eventManager.addEvent(new AddResourceEvent(parent.getNeighbor(Direction.Up).getPos(),powerUp.toString()));
            } else if (!parent.getNeighbor(Direction.Down).isEntityPresent()) {
                parent.getNeighbor(Direction.Down).setFieldEntity(powerUp.getFieldElement());
                eventManager.addEvent(new AddResourceEvent(parent.getNeighbor(Direction.Down).getPos(),powerUp.toString()));
            } else if (!parent.getNeighbor(Direction.Left).isEntityPresent()) {
                parent.getNeighbor(Direction.Left).setFieldEntity(powerUp.getFieldElement());
                eventManager.addEvent(new AddResourceEvent(parent.getNeighbor(Direction.Left).getPos(),powerUp.toString()));
            } else if (!parent.getNeighbor(Direction.Right).isEntityPresent()) {
                parent.getNeighbor(Direction.Right).setFieldEntity(powerUp.getFieldElement());
                eventManager.addEvent(new AddResourceEvent(parent.getNeighbor(Direction.Right).getPos(),powerUp.toString()));
            }
        }
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

    private boolean isResource(FieldHolder nextField) {
        if (nextField.isEntityPresent()) {
            FieldEntity fr = nextField.getEntity();
            return fr.getIntValue() == 501 || fr.getIntValue() == 502 ||
                    fr.getIntValue() == 503 || fr.getIntValue() == 504 ||
                    fr.getIntValue() == 7;

        }
        return false;
    }

    private boolean isWaterOrForest(FieldHolder nextField) {
        FieldTerrain fr = nextField.getTerrain();
        return fr.toString().equals("W") || fr.toString().equals("F");
    }

    public static void setGame(Game g){game = g;}
}


