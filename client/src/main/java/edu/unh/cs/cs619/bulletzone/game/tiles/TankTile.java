package edu.unh.cs.cs619.bulletzone.game.tiles;

import android.util.Log;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.game.TankList;

public class TankTile extends GroundTile {


    /**
     * @return ID
     */
    public Integer getID() {
        return ID;
    }

    /**
     * @param ID ID to be set to
     */
    public void setID(Integer ID) {
        this.ID = ID;
    }

    /**
     * @return orientation
     */
    public Integer getOrientation() {
        return orientation;
    }

    /**
     * @param orientation orientation of the tile
     */
    public void setOrientation(Integer orientation) {
        this.orientation = orientation;
    }

    public Integer ID;


    /**
     * @param JsonValue jsonValue
     * @param location  location
     */
    public TankTile(Integer JsonValue, Integer location) {
        this.resourceID = R.drawable.blank;
        this.location = location;
        ID = findID(JsonValue);
        boolean friendly = TankController.getTankController().containsTankID(Long.parseLong(String.valueOf(ID)));
        determineResourceID(friendly);
        orientation = findOrientation(JsonValue);
        health = findHealth(JsonValue);

        if (friendly) {
            TankController.getTankController().setTankOrientation(orientation, ID % 3);
        }
        TankList.getTankList().addTank(ID, this);
    }

    /**
     * @param TankID      tankID
     * @param location    location
     * @param orientation orientation
     */
    public TankTile(Integer TankID, Integer location, Integer orientation) {
        this.resourceID = R.drawable.blank;
        this.location = location;
        ID = TankID;
        boolean friendly = TankController.getTankController().containsTankID(Long.parseLong(String.valueOf(TankID)));
        health = determineResourceID(friendly);

        this.orientation = orientation;
        if (friendly) {
            TankController.getTankController().setTankOrientation(orientation, ID % 3);
        }

        TankList.getTankList().addTank(ID, this);
    }

    /**
     * @param JSONValue value
     * @return ID
     */
    //These two functions rip the value of digits from their respective spots
    //Not sure where in the Integer where we'll store the ID and orientation but we'll need these anyway
    private Integer findID(Integer JSONValue) {
        String number = String.valueOf(JSONValue);
        char[] digits1 = number.toCharArray();
        return Integer.parseInt(String.copyValueOf(digits1, 1, 3));

    }

    /**
     * @param JSONValue value
     * @return orientation
     */
    //Clockwise (0 up, 1 right, 2 down, 3 is left)
    private Integer findOrientation(Integer JSONValue) {
        String number = String.valueOf(JSONValue);
        char[] digits1 = number.toCharArray();
        int offset = 6;
        return Integer.parseInt(String.copyValueOf(digits1, offset, 1));
    }

    private Integer findHealth(Integer JSONValue) {
        String number = String.valueOf(JSONValue);
        //Log.d("Tank Health", "Json Value " + number);
        char[] digits1 = number.toCharArray();
        int offset = 4;
        return Integer.parseInt(String.copyValueOf(digits1, offset, 3));
    }


    private Integer determineResourceID(boolean freindly) {
        if (freindly) {
            switch (ID % 3) {
                // Tank
                case 0:
                    resourceID = R.drawable.tank;
                    return 100;
                // Miner
                case 1:
                    resourceID = R.drawable.miner;
                    return 300;
                // Builder
                case 2:
                    resourceID = R.drawable.builder;
                    return 80;
            }
        } else {
            switch (ID % 3) {
                // Tank
                case 0:
                    resourceID = R.drawable.redtank;
                    return 100;
                // Miner
                case 1:
                    resourceID = R.drawable.redminer;
                    return 300;
                // Builder
                case 2:
                    resourceID = R.drawable.redbuilder;
                    return 80;
            }
        }

        return  0;
    }
}

