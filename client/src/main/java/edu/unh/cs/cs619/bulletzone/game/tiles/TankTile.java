package edu.unh.cs.cs619.bulletzone.game.tiles;

import edu.unh.cs.cs619.bulletzone.R;

public class TankTile extends BlankTile {


    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getOrientation() {
        return orientation;
    }

    public void setOrientation(Integer orientation) {
        this.orientation = orientation;
    }

    public Integer ID;

    //Clockwise (0 up, 1 right, 2 down, 3 is left)
    public Integer orientation;

    public TankTile(Integer JsonValue, Integer location) {
        this.resourceID = R.drawable.blank;
        this.location = location;
        int row = location / 16 + 1;
        int column = location % 16 + 1;
        String r = String.valueOf(row);
        String c = String.valueOf(column);

        //This is what was the default in the grid adapter view (By plumdog)
         if (JsonValue >= 2000000 && JsonValue <= 3000000) {
            this.resourceID = R.drawable.bullet;
        } else if (JsonValue >= 10000000 && JsonValue <= 20000000) {
            this.resourceID = R.drawable.tank;
        }

         ID = findID(JsonValue);
         orientation = findOrientation(JsonValue);
    }

    //These two functions rip the value of digits from their respective spots
    //Not sure where in the Integer where we'll store the ID and orientation but we'll need these anyway
    private Integer findID(Integer JSONValue) {
        String number = String.valueOf(JSONValue);
        char[] digits1 = number.toCharArray();
        return Integer.parseInt(String.copyValueOf(digits1, 1, 3));

    }

    private Integer findOrientation(Integer JSONValue) {
        String number = String.valueOf(JSONValue);
        char[] digits1 = number.toCharArray();
        return Integer.parseInt(String.copyValueOf(digits1, 4, 1));
    }

}
