package edu.unh.cs.cs619.bulletzone.game;

import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;

public class TankList {
    private static volatile TankList INSTANCE = null;
    public HashMap<Integer, TankTile> tanks;

    /**
     * Tank List
     */
    private TankList() {
        tanks = new HashMap<Integer, TankTile>();

    }

    /**
     * Returns tank list
     * @return list
     */
    public static TankList getTankList() {
        if(INSTANCE == null) {
            synchronized (TileFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TankList();
                }
            }
        }
        return INSTANCE;
    }

    /**
     *
     * @param tankID ID
     * @param tile tank to be added
     */
    public void addTank(Integer tankID, TankTile tile) {
        tanks.put(tankID, tile);
    }

    /**
     *
     * @param tankID tank
     * @return tile
     */
    public TankTile getLocation(Integer tankID) {
        return tanks.get(tankID);
    }

    /**
     * removes tank in hashmap
     * @param tankID id
     */
    public void remove(Integer tankID) {
        tanks.remove(tankID);
    }

}
