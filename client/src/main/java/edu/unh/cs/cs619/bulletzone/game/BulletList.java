package edu.unh.cs.cs619.bulletzone.game;

import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.game.tiles.BulletTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;

public class BulletList {
    private static volatile BulletList INSTANCE = null;
    public HashMap<Integer, BulletTile> bullets;

    /**
     * List of all bullets on screen
     */
    private BulletList() {
        bullets = new HashMap<Integer, BulletTile>();

    }

    /**
     *
     * @return returns BulletList
     */
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

    public void clear(){
        bullets.clear();
    }

    /**
     *
     * @param bulletID bulletID
     * @param tile bulletile to add
     */
    public void addBullet(Integer bulletID, BulletTile tile) {
        bullets.put(bulletID, tile);
    }

    /**
     *
     * @param tankID tankID
     * @return BulletTile
     */
    public BulletTile getBulletTile(Integer tankID) {
        return bullets.get(tankID);
    }

    /**
     *
     * @param bulletID bulletID
     */
    public void removeBullet(Integer bulletID) {
        bullets.remove(bulletID);
    }

}