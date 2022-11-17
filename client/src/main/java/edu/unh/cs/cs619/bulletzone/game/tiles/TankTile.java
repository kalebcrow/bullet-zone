package edu.unh.cs.cs619.bulletzone.game.tiles;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.game.TankList;

public class TankTile extends BlankTile {


    /**
     *
     * @return ID
     */
    public Integer getID() {
        return ID;
    }

    /**
     *
     * @param ID ID to be set to
     */
    public void setID(Integer ID) {
        this.ID = ID;
    }

    /**
     *
     * @return orientation
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

    public Integer ID;


    /**
     *
     * @param JsonValue jsonValue
     * @param location location
     */
    public TankTile(Integer JsonValue, Integer location) {
        this.resourceID = R.drawable.blank;
        this.location = location;
        ID = findID(JsonValue);
        boolean friendly = TankController.getTankController().containsTankID(Long.parseLong(String.valueOf(ID)));


        //This is what was the default in the grid adapter view (By plumdog)
        if (JsonValue >= 10000000 && JsonValue <= 20000000) {
             if (friendly) {
                 this.resourceID = R.drawable.tank;

             } else {
                 this.resourceID = R.drawable.redtank;
             }
        }

        orientation = findOrientation(JsonValue);
        if (friendly) {
            TankController.getTankController().setTankOrientation(orientation);
        }

        TankList.getTankList().addTank(ID, this);
    }

    /**
     *
     * @param TankID tankID
     * @param location location
     * @param orientation orientation
     */
    public TankTile(Integer TankID, Integer location, Integer orientation) {
        this.resourceID = R.drawable.blank;
        this.location = location;
        ID = TankID;
        boolean friendly = TankController.getTankController().containsTankID(Long.parseLong(String.valueOf(TankID)));

        //This is what was the default in the grid adapter view (By plumdog)
        if (friendly) {
            this.resourceID = R.drawable.tank;

        } else {
            this.resourceID = R.drawable.redtank;
        }

        this.orientation = orientation;
        if (friendly) {
            TankController.getTankController().setTankOrientation(orientation);
        }

        TankList.getTankList().addTank(ID, this);
    }

    /**
     *
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
     *
     * @param JSONValue value
     * @return orientation
     */
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
