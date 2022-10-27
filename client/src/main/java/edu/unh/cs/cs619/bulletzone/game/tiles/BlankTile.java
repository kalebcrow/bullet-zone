package edu.unh.cs.cs619.bulletzone.game.tiles;

import edu.unh.cs.cs619.bulletzone.R;
public class BlankTile {

    /**
     *
     * @return resourceID
     */
    public Integer getResourceID() {
        return resourceID;
    }

    /**
     *
     * @param resourceID resourceID
     */
    public void setResourceID(Integer resourceID) {
        this.resourceID = resourceID;
    }

    public Integer resourceID;

    /**
     *
     * @return location of tile
     */

    public Integer getLocation() {
        return location;
    }

    /**
     *
     * @param location location of tile
     */
    public void setLocation(Integer location) {
        this.location = location;
    }

    public Integer location;

    /**
     *
     * @return the orientation of the tile
     */
    public Integer getOrientation() {
        return orientation;
    }

    /**
     *
     * @param orientation orientation of the tile
     */
    public void setOrientation(Integer orientation) {
        this.orientation = orientation;
    }

    public Integer orientation;

    /**
     * make the tile
     */
    public BlankTile() {
        this.resourceID = R.drawable.blank;
        this.location = 0;
    }

    /**
     *
     * @param JsonValue Value being used from server
     * @param location location
     */
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
