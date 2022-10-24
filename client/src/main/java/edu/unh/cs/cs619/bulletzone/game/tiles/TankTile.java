package edu.unh.cs.cs619.bulletzone.game.tiles;

import android.util.Log;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.game.TankController;

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


    public TankTile(Integer JsonValue, Integer location) {
        this.resourceID = R.drawable.blank;
        this.location = location;
        ID = findID(JsonValue);
        Long controller = TankController.getTankController().getTankID();


        //This is what was the default in the grid adapter view (By plumdog)
         if (JsonValue >= 2000000 && JsonValue <= 3000000) {
            resourceID = R.drawable.bullet;
         } else if (JsonValue >= 10000000 && JsonValue <= 20000000) {
             if (ID == Math.toIntExact(controller)) {
                 this.resourceID = R.drawable.tank;

             } else {
                 this.resourceID = R.drawable.redtank;
             }
        }

        orientation = findOrientation(JsonValue);
        if (ID == Math.toIntExact(controller)) {
            TankController.getTankController().setTankOrientation(orientation);
        }
    }

    public void moveTo(boolean forward) {
        switch(orientation) {
            case 0:
                if (forward) {
                    location = location - 16;
                } else {
                    location = location + 16;
                }
                break;
            case 1:
                if (forward) {
                    location = location + 1;
                } else {
                    location = location - 1;
                }

                break;
            case 2:
                if (forward) {
                    location = location + 16;
                } else {
                    location = location - 16;
                }
                break;
            default:
                if (forward) {
                    location = location - 1;
                } else {
                    location = location + 1;
                }
        }

    }

    //These two functions rip the value of digits from their respective spots
    //Not sure where in the Integer where we'll store the ID and orientation but we'll need these anyway
    private Integer findID(Integer JSONValue) {
        String number = String.valueOf(JSONValue);
        char[] digits1 = number.toCharArray();
        return Integer.parseInt(String.copyValueOf(digits1, 1, 3));

    }
    //Clockwise (0 up, 1 right, 2 down, 3 is left)
    private Integer findOrientation(Integer JSONValue) {
        String number = String.valueOf(JSONValue);
        char[] digits1 = number.toCharArray();
        int offset = 6;
        if (resourceID != R.drawable.bullet) {
            offset = 7;
        }
        return Integer.parseInt(String.copyValueOf(digits1, offset, 1));
    }

}
