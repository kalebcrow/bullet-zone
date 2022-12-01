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
        if (JsonValue == 3111) {
            resourceID = R.drawable.gravity_assist;
        } else if (JsonValue == 3121) {
            resourceID = R.drawable.fusion_reactor;
        }

        this.location = location;
        orientation = 0;
        health = 0;
    }

}
