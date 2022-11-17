package edu.unh.cs.cs619.bulletzone.game;


import edu.unh.cs.cs619.bulletzone.game.tiles.BulletTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.ObstacleTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.ResourceTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;

//Not sure if this needs to be a singleton but it is now
public class TileFactory {
    private static volatile TileFactory INSTANCE = null;

    /**
     * Static class
     */
    private TileFactory() {

    }

    /**
     * get factory
     * @return list
     */
    public static TileFactory getFactory() {
        if(INSTANCE == null) {
            synchronized (TileFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TileFactory();
                }
            }
        }
        return INSTANCE;
    }

    /**
     *
     * @param JsonValue value to generate tile
     * @param location location of tile
     * @return tile
     */
    public GroundTile makeTile(Integer JsonValue, Integer location) {
        //if (JsonValue >= 4000 && JsonValue <  1000000) {
        //    return new BlankTile(JsonValue, location);
        //} else
        if (JsonValue == 0 || JsonValue == 1 || JsonValue == 2) {
            return new GroundTile(JsonValue, location);
        } else if (JsonValue == 501 || JsonValue == 502 || JsonValue == 503 || JsonValue == 7){
            return new ResourceTile(JsonValue, location);
        } else if (JsonValue >= 1000 && JsonValue <= 3000) {
            return new ObstacleTile(JsonValue, location);
        } else if (JsonValue >= 2000000 && JsonValue < 3000000) {
            return new BulletTile(JsonValue, location);
        } else if (JsonValue >= 10000000) {
            return new TankTile(JsonValue, location);
        } else {
            // return blank tile
            return new GroundTile(-1, location);
        }
    }
}
