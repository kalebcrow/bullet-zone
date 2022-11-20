package edu.unh.cs.cs619.bulletzone.game.tiles;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.game.BulletList;

public class ItemTile extends GroundTile {

    /**
     *
     * @param JsonValue value from gird
     * @param location location of the tile
     */
    public ItemTile(Integer JsonValue, Integer location) {
        if (JsonValue == 501) {
            resourceID = R.drawable.clay;
        } else if (JsonValue == 502) {
            resourceID = R.drawable.rock;
        } else if (JsonValue == 503) {
            resourceID = R.drawable.iron;
        } else if (JsonValue == 7) {
            resourceID = R.drawable.thingamajig;
        }
        this.location = location;
        orientation = 0;
        health = 0;
    }

}
