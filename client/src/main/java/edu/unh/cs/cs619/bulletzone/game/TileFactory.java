package edu.unh.cs.cs619.bulletzone.game;


import edu.unh.cs.cs619.bulletzone.game.tiles.BlankTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.ObstacleTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;

//Not sure if this needs to be a singleton but it is now
public class TileFactory {
    private static volatile TileFactory INSTANCE = null;

    private TileFactory() {

    }

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

    public BlankTile makeTile(Integer JsonValue, Integer location) {
        if (JsonValue == 0 || JsonValue >= 4000 && JsonValue <  1000000) {
            return new BlankTile(JsonValue, location);
        } else if (JsonValue >= 1000 && JsonValue <= 3000) {
            return new ObstacleTile(JsonValue, location);
        } else if (JsonValue >= 1000000) {
            return new TankTile(JsonValue, location);
        } else {
            return new BlankTile(JsonValue, location);
        }
    }
}
