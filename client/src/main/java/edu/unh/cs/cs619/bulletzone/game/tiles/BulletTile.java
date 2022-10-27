package edu.unh.cs.cs619.bulletzone.game.tiles;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.game.BulletList;

public class BulletTile extends BlankTile {


    public Integer getTankID() {
        return bulletid;
    }

    public void setTankID(Integer BulletID) {
        this.bulletid = BulletID;
    }

    public Integer getOrientation() {
        return orientation;
    }

    public void setOrientation(Integer orientation) {
        this.orientation = orientation;
    }

    public Integer bulletid;


    public BulletTile(Integer JsonValue, Integer location) {
        this.resourceID = R.drawable.bullet;
        this.location = location;
        bulletid = findID(JsonValue);
        BulletList.getBulletList().addBullet(this.bulletid, this);
    }

    public BulletTile(Long bulletid, Integer JsonValue, Integer location) {
        this.resourceID = R.drawable.bullet;
        this.location = location;
        this.bulletid = JsonValue * 10 + Math.toIntExact(bulletid);

        BulletList.getBulletList().addBullet(this.bulletid, this);
    }

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
