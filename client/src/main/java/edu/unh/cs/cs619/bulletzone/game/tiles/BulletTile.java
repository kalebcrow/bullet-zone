package edu.unh.cs.cs619.bulletzone.game.tiles;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.game.BulletList;

public class BulletTile extends BlankTile {

    /**
     *
     * @return bulletID
     */
    public Integer getTankID() {
        return bulletid;
    }

    /**
     *
     * @param BulletID bulletID
     */
    public void setTankID(Integer BulletID) {
        this.bulletid = BulletID;
    }

    /**
     *
     * @return return the orientation
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

    public Integer bulletid;

    /**
     *
     * @param JsonValue value from gird
     * @param location location of the tile
     */
    public BulletTile(Integer JsonValue, Integer location) {
        this.resourceID = R.drawable.bullet;
        this.location = location;
        bulletid = findID(JsonValue);
        orientation = 0;
        BulletList.getBulletList().addBullet(this.bulletid, this);
    }

    /**
     *
     * @param tankID tankID
     * @param location location
     */
    public BulletTile(Integer tankID, Integer location, boolean value) {
        this.resourceID = R.drawable.bullet;
        this.location = location;
        this.bulletid = tankID;
        orientation = 0;

        BulletList.getBulletList().addBullet(this.bulletid, this);
    }

    /**
     *
     * @param JSONValue value to derive bulletID
     * @return ID
     */
    //These two functions rip the value of digits from their respective spots
    //Not sure where in the Integer where we'll store the ID and orientation but we'll need these anyway
    private Integer findID(Integer JSONValue) {
        String number = String.valueOf(JSONValue);
        char[] digits1 = number.toCharArray();
        String id = String.copyValueOf(digits1, 1, 3);
        id += String.copyValueOf(digits1, 6, 6);
        return Integer.valueOf(id);

    }

}
