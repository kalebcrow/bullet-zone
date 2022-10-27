package edu.unh.cs.cs619.bulletzone.game;

import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.game.tiles.BulletTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;

public class BulletList {
    private static volatile BulletList INSTANCE = null;
    public HashMap<Integer, BulletTile> bullets;


    private BulletList() {
        bullets = new HashMap<Integer, BulletTile>();

    }

    public static BulletList getBulletList() {
        if (INSTANCE == null) {
            synchronized (TileFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BulletList();
                }
            }
        }
        return INSTANCE;
    }

    public void addBullet(Integer bulletID, BulletTile tile) {
        bullets.put(bulletID, tile);
    }

    public BulletTile getBulletTile(Integer tankID) {
        return bullets.get(tankID);
    }

    public void removeBullet(Integer bulletID) {
        bullets.remove(bulletID);
    }

}