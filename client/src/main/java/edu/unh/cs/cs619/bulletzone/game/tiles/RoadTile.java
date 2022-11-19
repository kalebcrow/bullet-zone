package edu.unh.cs.cs619.bulletzone.game.tiles;

import android.util.Log;

import edu.unh.cs.cs619.bulletzone.R;

public class RoadTile extends GroundTile {

    public RoadTile(Integer JsonValue, Integer location) {
        this.location = location;
        this.orientation = 0;
        this.jsonValue = JsonValue;

        if (jsonValue == 30000001) {
            this.resourceID = R.drawable.road;
        } else {
            this.resourceID = R.drawable.blank;
        }
    }

}

