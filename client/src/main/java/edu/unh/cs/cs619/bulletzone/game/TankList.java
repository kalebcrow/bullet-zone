package edu.unh.cs.cs619.bulletzone.game;

import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;

public class TankList {
    private static volatile TankList INSTANCE = null;
    public HashMap<Integer, TankTile> tanks;


    private TankList() {
        tanks = new HashMap<Integer, TankTile>();

    }

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

    public void addTank(Integer tankID, TankTile tile) {
        tanks.put(tankID, tile);
    }

    public TankTile getLocation(Integer tankID) {
        return tanks.get(tankID);
    }

    public void remove(Integer tankID) {
        tanks.remove(tankID);
    }

}
