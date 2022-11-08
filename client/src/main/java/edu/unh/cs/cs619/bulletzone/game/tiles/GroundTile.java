package edu.unh.cs.cs619.bulletzone.game.tiles;

import edu.unh.cs.cs619.bulletzone.R;

public class GroundTile extends BlankTile {

    /**
     * Initializes the ground tile.
     *
     * @param JsonValue value from server
     * @param location location
     */
    public GroundTile(Integer JsonValue, Integer location) {
        // same as blank tile initialization
        this.resourceID = R.drawable.blank;
        this.location = location;
        orientation = 0;
        int row = location / 16 + 1;
        int column = location % 16 + 1;
        String r = String.valueOf(row);
        String c = String.valueOf(column);

        // making up json values temp until confirmed there are not existing ones
        if (JsonValue == 10) {
            this.resourceID = R.drawable.hilly;
        } else if (JsonValue == 20) {
            this.resourceID = R.drawable.rocky;
        }
    }
}
