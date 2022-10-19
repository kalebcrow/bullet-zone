package edu.unh.cs.cs619.bulletzone.game.tiles;

import edu.unh.cs.cs619.bulletzone.R;

public class ObstacleTile extends BlankTile {

    public ObstacleTile(Integer JsonValue, Integer location) {
        this.resourceID = R.drawable.blank;
        this.location = location;
        int row = location / 16 + 1;
        int column = location % 16 + 1;
        String r = String.valueOf(row);
        String c = String.valueOf(column);

        //This is what was the default in the grid adapter view (By plumdog)
        if (JsonValue == 1000 || ( JsonValue > 1000 && JsonValue <= 2000)) {
            this.resourceID = R.drawable.ironwall;
        }

    }





}
