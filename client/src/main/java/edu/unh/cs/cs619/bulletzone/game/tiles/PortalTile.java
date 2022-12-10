package edu.unh.cs.cs619.bulletzone.game.tiles;

import edu.unh.cs.cs619.bulletzone.R;

public class PortalTile extends GroundTile {

    /**
     *
     * @param JsonValue value from server
     * @param location location
     */
    public PortalTile(Integer JsonValue, Integer location) {
        this.resourceID = R.drawable.blank;
        this.location = location;
        this.jsonValue = JsonValue;
        int row = location / 16 + 1;
        int column = location % 16 + 1;
        String r = String.valueOf(row);
        String c = String.valueOf(column);
        orientation = findOrientation(JsonValue);
        orientation = orientation + 4 % 8;

        //This is what was the default in the grid adapter view (By plumdog)
        this.resourceID = R.drawable.portal;
    }

    /**
     *
     * @param JSONValue value from server
     * @return orientation
     */
    private Integer findOrientation(Integer JSONValue) {
        String number = String.valueOf(JSONValue);
        char[] digits1 = number.toCharArray();
        return Integer.parseInt(String.copyValueOf(digits1, 3, 1));
    }

}
