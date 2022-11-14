package edu.unh.cs.cs619.bulletzone.game.tiles;

import edu.unh.cs.cs619.bulletzone.R;
public class GroundTile {

    /**
     *
     * @return JsonValue
     */
    public Integer getJSONValue() {
        return jsonValue;
    }

    public Integer jsonValue = -1;
    public Integer terrainJsonValue = -1;

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
    public GroundTile() {
        // default meadow tile with json 0
        this.resourceID = R.drawable.meadow;
        this.location = 0;
        this.jsonValue = 0;
    }

    /**
     * Initializes the ground tile.
     *
     * @param JsonValue Value being used from server
     * @param location location
     */
    public GroundTile(Integer JsonValue, Integer location) {
        // same as blank tile initialization
        this.resourceID = R.drawable.blank; // SARA
        this.location = location;
        this.jsonValue = JsonValue;
        this.terrainJsonValue = JsonValue;
        orientation = 0;
        int row = location / 16 + 1;
        int column = location % 16 + 1;
        String r = String.valueOf(row);
        String c = String.valueOf(column);

        // using given json values
        if (JsonValue == 2) {
            this.resourceID = R.drawable.hilly;
        } else if (JsonValue == 1) {
            this.resourceID = R.drawable.rocky;
        } else if (JsonValue == 0) {
            this.resourceID = R.drawable.meadow;// somethng is wrong SARA
        }
    }

    public int getTerrain() {
        // using given json values
        if (terrainJsonValue == 2) {
            return R.drawable.hilly;
        } else if (terrainJsonValue == 1) {
            return R.drawable.rocky;
        } else if (terrainJsonValue == 0) {
            return R.drawable.meadow;
        }
        // not a terrain basically
        return R.drawable.blank;
    }
}

/*
package edu.unh.cs.cs619.bulletzone.game.tiles;

import edu.unh.cs.cs619.bulletzone.R;

public class GroundTile extends BlankTile {

    /**
     * Initializes the ground tile.
     *
     * @param JsonValue value from server
     * @param location location

}

 */
