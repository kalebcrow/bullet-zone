package edu.unh.cs.cs619.bulletzone.game.tiles;

import edu.unh.cs.cs619.bulletzone.R;

public class TerrainTile extends BlankTile {

    /**
     *
     * @param JsonValue value from server
     * @param location location
     */
    public TerrainTile(Integer JsonValue, Integer location) {
        this.resourceID = R.drawable.blank;
        this.location = location;
        int row = location / 16 + 1;
        int column = location % 16 + 1;
        String r = String.valueOf(row);
        String c = String.valueOf(column);
        orientation = 0;

        //This is what was the default in the grid adapter view (By plumdog)
        if (JsonValue == 1000) {
            this.resourceID = R.drawable.ironwall;
        } else if (JsonValue < 2000 && JsonValue > 1000) {
            this.resourceID = R.drawable.stonewall;
        }



    }

    /**
     *
     * @param JSONValue value from server
     * @return orientation
     */
    private Integer findOrientation(Integer JSONValue) {
        String number = String.valueOf(JSONValue);
        char[] digits1 = number.toCharArray();
        return Integer.parseInt(String.copyValueOf(digits1, 4, 1));
    }

}
