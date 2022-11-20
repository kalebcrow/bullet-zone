package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Timer;

import edu.unh.cs.cs619.bulletzone.events.AddResourceEvent;
import edu.unh.cs.cs619.bulletzone.events.GridEvent;

public final class Game {
    private static final Logger log = LoggerFactory.getLogger(Game.class);

    /**
     * Field dimensions
     */
    private static final int FIELD_DIM = 16;
    private final long id;
    private final ArrayList<FieldHolder> holderGrid = new ArrayList<>();

    // Event History for the clients
    private LinkedList<GridEvent> eventHistory = new LinkedList<>();


    private final ConcurrentMap<Long, Tank> tanks = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, HashMap<String,Long>> playersIP = new ConcurrentHashMap<>();

    private final Object monitor = new Object();

    private final Timer timer = new Timer();

    public Game() {
        this.id = 0;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @JsonIgnore
    public ArrayList<FieldHolder> getHolderGrid() {
        return holderGrid;
    }

    public void addTank(String ip, Tank tank, String key) {
        synchronized (tanks) {
            tanks.put(tank.getId(), tank);
            if(!playersIP.containsKey(ip)){
                playersIP.put(ip, new HashMap<>());
            }
            HashMap<String, Long> map = playersIP.get(ip);
            map.put(key, tank.getId());
            playersIP.put(ip, map);
        }
    }

    public Tank getTank(Long tankId) {
        return tanks.get(tankId);
    }

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

    public void removeTank(long tankId){
        synchronized (tanks) {
            Tank t = tanks.remove(tankId);
            if (t != null) {
                playersIP.remove(t.getIp());
            }
        }
    }

    public int[][][] getGrid3D() {
        // start randomly spawning resources
        //getRandomResources(); // TODO add randomly spawning resources

        int[][][] grid = new int[FIELD_DIM][FIELD_DIM][3];

        synchronized (holderGrid) {
            // get the grid // SARA
            FieldHolder holder;
            for (int i = 0; i < FIELD_DIM; i++) {
                for (int j = 0; j < FIELD_DIM; j++) {
                    holder = holderGrid.get(i * FIELD_DIM + j);
                    // set entity if there is one
                    grid[i][j][1] = -1; // make it blank if theres no entity
                    if (holder.isEntityPresent()) {
                        grid[i][j][1] = holder.getEntity().getIntValue();
                    }
                    // check for improvements
                    if(holder.isImprovementPresent())
                    {
                        grid[i][j][2] = 30000001;
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

    // Adds to the event history
    public void addEvent(GridEvent gridEvent){
        eventHistory.addFirst(gridEvent);
    }


    // gets events more recent than the time given
    public LinkedList<GridEvent> getEvents(Long time){

        // initiate the update list, iterator, GridEvent placeholder
        LinkedList<GridEvent> update = new LinkedList<>();
        GridEvent gridEvent;

        // check that the list has the next node
        for (int i = 0; i < eventHistory.size(); i++) {
            gridEvent = eventHistory.get(i);
            if(gridEvent.getTime() <= time) break; // break if event's time is less recent than time given
            update.addFirst(gridEvent); // otherwise put it at the front of the list
        }
        //go to the back of the list (oldest events)

        long cutOff = System.currentTimeMillis()-120000;
        // set a cutoff of 2 min behind current time
        for (int index = eventHistory.size() - 1; index > 0; index--) {
            if (eventHistory.get(index).getTime() <= cutOff) {
                eventHistory.remove(index);
            }
        }
        return update;
    }
}
