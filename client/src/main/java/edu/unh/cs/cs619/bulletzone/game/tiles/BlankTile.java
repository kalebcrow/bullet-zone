package edu.unh.cs.cs619.bulletzone.game.tiles;

import edu.unh.cs.cs619.bulletzone.R;
public class BlankTile {
    public Integer getResourceID() {
        return resourceID;
    }

    public void setResourceID(Integer resourceID) {
        this.resourceID = resourceID;
    }

    public Integer resourceID;
    public Integer location;

    public Integer getOrientation() {
        return orientation;
    }

    public void setOrientation(Integer orientation) {
        this.orientation = orientation;
    }

    public Integer orientation;

    public BlankTile() {
        this.resourceID = R.drawable.blank;
        this.location = 0;
    }

    public BlankTile(Integer JsonValue, Integer location) {
        this.resourceID = R.drawable.blank;
        this.location = location;
        orientation = 0;
        int row = location / 16 + 1;
        int column = location % 16 + 1;
        String r = String.valueOf(row);
        String c = String.valueOf(column);
    }
}
