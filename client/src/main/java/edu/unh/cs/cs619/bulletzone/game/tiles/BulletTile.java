package edu.unh.cs.cs619.bulletzone.game.tiles;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.game.TankList;

public class BulletTile extends BlankTile {


    public Integer getTankID() {
        return TankID;
    }

    public void setTankID(Integer tankID) {
        this.TankID = tankID;
    }

    public Integer getOrientation() {
        return orientation;
    }

    public void setOrientation(Integer orientation) {
        this.orientation = orientation;
    }

    public Integer TankID;


    public BulletTile(Integer JsonValue, Integer location) {
        this.resourceID = R.drawable.blank;
        this.location = location;
        TankID = findID(JsonValue);
        Long controller = TankController.getTankController().getTankID();


        //This is what was the default in the grid adapter view (By plumdog)
        if (JsonValue >= 2000000 && JsonValue <= 3000000) {
            resourceID = R.drawable.bullet;
        } else if (JsonValue >= 10000000 && JsonValue <= 20000000) {
            if (TankID == Math.toIntExact(controller)) {
                this.resourceID = R.drawable.tank;

            } else {
                this.resourceID = R.drawable.redtank;
            }
        }

        orientation = findOrientation(JsonValue);
        if (TankID == Math.toIntExact(controller)) {
            TankController.getTankController().setTankOrientation(orientation);
        }

        TankList.getTankList().addTank(TankID, this);
    }

    public TankTile(Integer TankID, Integer location, Integer orientation) {
        this.resourceID = R.drawable.blank;
        this.location = location;
        this.TankID = TankID;
        Long controller = TankController.getTankController().getTankID();

        //This is what was the default in the grid adapter view (By plumdog)
        if (this.TankID == Math.toIntExact(controller)) {
            this.resourceID = R.drawable.tank;

        } else {
            this.resourceID = R.drawable.redtank;
        }

        this.orientation = orientation;
        if (this.TankID == Math.toIntExact(controller)) {
            TankController.getTankController().setTankOrientation(orientation);
        }

        TankList.getTankList().addTank(this.TankID, this);
    }

    //These two functions rip the value of digits from their respective spots
    //Not sure where in the Integer where we'll store the ID and orientation but we'll need these anyway
    private Integer findID(Integer JSONValue) {
        String number = String.valueOf(JSONValue);
        char[] digits1 = number.toCharArray();
        return Integer.parseInt(String.copyValueOf(digits1, 1, 3));

    }

}
