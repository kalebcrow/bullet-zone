package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.events.AddTankEvent;
import edu.unh.cs.cs619.bulletzone.events.DestroyTankEvent;
import edu.unh.cs.cs619.bulletzone.events.EventManager;
import edu.unh.cs.cs619.bulletzone.events.balanceEvent;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.repository.DataRepository;

public final class Game {
    private static final Logger log = LoggerFactory.getLogger(Game.class);

    /**
     * Field dimensions
     */
    private static final int FIELD_DIM = 16;
    private static final int FIELD_DIM_COL = FIELD_DIM;
    private static final int FIELD_DIM_ROW = FIELD_DIM * 3;
    private final long id;
    private final ArrayList<FieldHolder> holderGrid = new ArrayList<>();
    private EventManager eventManager = EventManager.getInstance();
    private DataRepository data = new DataRepository();
    private final AtomicLong idGenerator = new AtomicLong();

    // Event History for the clients


    private final ConcurrentMap<Long, Tank> tanks = new ConcurrentHashMap<>();
    private final ConcurrentMap<String,Factory> factories = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, HashMap<String,Long>> playersIP = new ConcurrentHashMap<>();

    private final Object monitor = new Object();

    public Game() {
        this.id = 0;
        Tank.setGame(this);
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @JsonIgnore
    public ArrayList<FieldHolder> getHolderGrid() {
        return holderGrid;
    }

    public void addTank(Tank tank) {
        synchronized (tanks) {
            String ip = tank.getIp();
            int typeIndex = tank.getTypeIndex();
            String key;
            switch(typeIndex){
                case 1:
                    key = "miner";
                    break;
                case 2:
                    key = "builder";
                    break;
                default:
                    key = "tank";
            }
            tanks.put(tank.getId(), tank);
            if(!playersIP.containsKey(ip)) playersIP.put(ip,new HashMap<>());
            HashMap<String, Long> map = playersIP.get(ip);
            map.put(key, tank.getId());
            playersIP.put(ip, map);
        }
    }

    public Tank getTank(Long tankId) {
        return tanks.get(tankId);
    }

    public Factory getFactory(String ip){return factories.get(ip);}

    public ConcurrentMap<String,Factory> getFactories(){return factories;}

    public ConcurrentMap<Long, Tank> getTanks() {
        return tanks;
    }

    public ConcurrentMap<String, HashMap<String,Long>> getPlayersIP() { return playersIP; }

    public List<Optional<FieldEntity>> getGrid() {
        synchronized (holderGrid) {
            List<Optional<FieldEntity>> entities = new ArrayList<Optional<FieldEntity>>();

            FieldEntity entity;
            FieldTerrain terrain;
            for (FieldHolder holder : holderGrid) {
                if (holder.isEntityPresent()) {
                    entity = holder.getEntity();
                    entity = entity.copy();

                    entities.add(Optional.<FieldEntity>of(entity));
                } else if (!holder.isEntityPresent()){
                    // should always set the terrain?
                    terrain = holder.getTerrain();
                    terrain = terrain.copy();

                    entities.add(Optional.<FieldEntity>of(terrain));
                }
            }
            return entities;
        }
    }

    public HashMap<String,Long> getTanks(String ip){
        if (playersIP.containsKey(ip)){
            return playersIP.get(ip);
        }
        return null;
    }

    public void removeTank(Tank tank){
        synchronized (tanks) {
            long tankId = tank.getId();
            Tank t = tanks.remove(tankId);
            if (t != null) {
                playersIP.remove(t.getIp());
            }
        }
    }

    public int[][][] getGrid3D() {

        int[][][] grid = new int[FIELD_DIM_ROW][FIELD_DIM_COL][3];

        synchronized (holderGrid) {
            // get the grid
            FieldHolder holder;
            // row
            for (int i = 0; i < FIELD_DIM_ROW; i++) {
                // col
                for (int j = 0; j < FIELD_DIM_COL; j++) {
                    holder = holderGrid.get(i * FIELD_DIM_COL + j);
                    // set entity if there is one
                    grid[i][j][1] = -1; // make it blank if theres no entity
                    if (holder.isEntityPresent()) {
                        grid[i][j][1] = holder.getEntity().getIntValue();
                    }
                    // check for improvements
                    if(holder.isImprovementPresent())
                    {
                        if(holder.getImprovement().toString() == "P")
                        {
                            grid[i][j][2] = holder.getImprovement().getIntValue();
                        }
                        else
                        {
                            grid[i][j][2] = 30000001;
                        }
                    }
                    else
                    {
                        grid[i][j][2] = 30000000;
                    }
                    // set terrain (should always be there)
                    grid[i][j][0] = holder.getTerrain().getIntValue();
                }
            }
        }
        return grid;
    }

    public void leave(long tankId)
            throws TankDoesNotExistException {
        synchronized (this.monitor) {

            String ip = tanks.get(tankId).getIp();
            HashMap<String, Long> map = playersIP.get(ip);

            Tank miner = tanks.get(map.get("miner"));
            Tank builder = tanks.get(map.get("builder"));
            Tank tank = tanks.get(map.get("tank"));


            System.out.println("leave() called, tank ID: " + tank.getId());
            System.out.println("leave() called, tank ID: " + miner.getId());
            System.out.println("leave() called, tank ID: " + builder.getId());

            double amount = (miner.getResourcesByResource(0) * 25) + (miner.getResourcesByResource(1) * 78) + (miner.getResourcesByResource(2) * 16);
            System.out.println("AMOUNT: " + amount);
            data.modifyAccountBalance(tank.getUsername(), amount);
            eventManager.addEvent(new balanceEvent(data.getUserAccountBalance(tank.getUsername()), tankId));
            System.out.println("AMOUNT balance: " + data.getUserAccountBalance(tank.getUsername()));
            miner.subtractBundleOfResources(0, miner.getResourcesByResource(0));
            miner.subtractBundleOfResources(1, miner.getResourcesByResource(1));
            miner.subtractBundleOfResources(2, miner.getResourcesByResource(2));

            if(tank.getLife() > 0) {
                tank.getParent().clearField();
                eventManager.addEvent(new DestroyTankEvent(tank.getId()));
            }
            tanks.remove(tank.getId());

            if(miner.getLife() > 0) {
                miner.getParent().clearField();
                eventManager.addEvent(new DestroyTankEvent(miner.getId()));
            }
            tanks.remove(miner.getId());

            if(builder.getLife() > 0) {
                builder.getParent().clearField();
                eventManager.addEvent(new DestroyTankEvent(builder.getId()));
            }
            tanks.remove(builder.getId());

            playersIP.remove(ip);
        }

    }

    public Tank[] join(String username, String ip){
        Tank[] playerTanks = new Tank[3];

        if(!playersIP.containsKey(ip)) {
            Long tankID = this.idGenerator.getAndIncrement();
            Long minerID = this.idGenerator.getAndIncrement();
            Long builderID = this.idGenerator.getAndIncrement();

            playerTanks[0] = new Tank(username, tankID, Direction.Up, ip, 0);
            playerTanks[1] = new Tank(username, minerID, Direction.Up, ip, 1);
            playerTanks[2] = new Tank(username, builderID, Direction.Up, ip, 2);

            addTank(playerTanks[0]);
            addTank(playerTanks[1]);
            addTank(playerTanks[2]);

            if(ip == "test"){
                int tankSpawnLocation = 16;
                int minerSpawnLocation = 33;
                int builderSpawnLocation = 251;
                FieldHolder place = holderGrid.get(tankSpawnLocation);
                place.setFieldEntity(playerTanks[0]);
                playerTanks[0].setParent(place);
                place = holderGrid.get(minerSpawnLocation);
                place.setFieldEntity(playerTanks[1]);
                playerTanks[1].setParent(place);
                place = holderGrid.get(builderSpawnLocation);
                place.setFieldEntity(playerTanks[2]);
                playerTanks[2].setParent(place);
                eventManager.addEvent(new AddTankEvent(tankSpawnLocation/16, tankSpawnLocation%16, tankID));
                eventManager.addEvent(new AddTankEvent(minerSpawnLocation/16, minerSpawnLocation%16, minerID));
                eventManager.addEvent(new AddTankEvent(builderSpawnLocation/16, builderSpawnLocation%16, builderID));
            } else {

                Random random = new Random();
                int x;
                int y;

                // This may run for forever.. If there is no free space. XXX
                for (; ; ) {
                    x = random.nextInt(FIELD_DIM);
                    y = random.nextInt(FIELD_DIM);
                    FieldHolder fieldElement = holderGrid.get(x * FIELD_DIM + y);
                    if (!fieldElement.isEntityPresent() && !fieldElement.getTerrain().toString().equals("W") && !fieldElement.getTerrain().toString().equals("F")) {
                        fieldElement.setFieldEntity(playerTanks[0]);
                        playerTanks[0].setParent(fieldElement);
                        break;
                    }
                }
                eventManager.addEvent(new AddTankEvent(x, y, tankID));
                for (; ; ) {
                    x = random.nextInt(FIELD_DIM);
                    y = random.nextInt(FIELD_DIM);
                    FieldHolder fieldElement = holderGrid.get(x * FIELD_DIM + y);
                    if (!fieldElement.isEntityPresent() && !fieldElement.getTerrain().toString().equals("W") && !fieldElement.getTerrain().toString().equals("F")) {
                        fieldElement.setFieldEntity(playerTanks[1]);
                        playerTanks[1].setParent(fieldElement);
                        break;
                    }
                }
                eventManager.addEvent(new AddTankEvent(x, y, minerID));
                for (; ; ) {
                    x = random.nextInt(FIELD_DIM);
                    y = random.nextInt(FIELD_DIM);
                    FieldHolder fieldElement = holderGrid.get(x * FIELD_DIM + y);
                    if (!fieldElement.isEntityPresent() && !fieldElement.getTerrain().toString().equals("W") && !fieldElement.getTerrain().toString().equals("F")) {
                        fieldElement.setFieldEntity(playerTanks[2]);
                        playerTanks[2].setParent(fieldElement);
                        break;
                    }
                }
                eventManager.addEvent(new AddTankEvent(x, y, builderID));
            }
        } else {
            HashMap<String,Long> map = playersIP.get(ip);
            playerTanks[0] = tanks.get(map.get("tank"));
            playerTanks[1] = tanks.get(map.get("miner"));
            playerTanks[2] = tanks.get(map.get("builder"));
        }
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                eventManager.addEvent(new balanceEvent(data.getUserAccountBalance(playerTanks[0].getUsername()), playerTanks[0].getId()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        return playerTanks;
    }
}

