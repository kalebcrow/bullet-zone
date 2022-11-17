package edu.unh.cs.cs619.bulletzone.game.tiles;

import edu.unh.cs.cs619.bulletzone.R;

public class ResourceTile extends GroundTile {

    /**
     *
     * @return JsonValue
     */
    public Integer getJSONValue() {
        return jsonValue;
    }

    public Integer jsonValue = -1;
    public Integer resourceJsonValue = -1;

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
    public ResourceTile() {
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
    public ResourceTile(Integer JsonValue, Integer location) {
        // same as blank tile initialization
        this.resourceID = R.drawable.blank;
        this.location = location;
        this.jsonValue = JsonValue;
        this.resourceJsonValue = JsonValue;
        orientation = 0;

        // using given json values
        if (JsonValue == 501) {
            this.resourceID = R.drawable.clay;
        } else if (JsonValue == 502) {
            this.resourceID = R.drawable.rock;
        } else if (JsonValue == 502) {
            this.resourceID = R.drawable.meadow;
        } else if (JsonValue == 7) {
            this.resourceID = R.drawable.thingamajig;
        }
    }

    public int getResource() {
        // using given json values
        if (resourceJsonValue == 501) {
            return R.drawable.clay;
        } else if (resourceJsonValue == 502) {
            return R.drawable.rock;
        } else if (resourceJsonValue == 503) {
            return R.drawable.iron;
        } else if (resourceJsonValue == 7) {
            return R.drawable.thingamajig;
        }
        // not a resource basically
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