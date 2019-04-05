/**
 * Public class for accessing data for individual items in the Item table
 */
package edu.unh.cs.cs619.bulletzone.datalayer;

public class GameItem {
    protected int itemID;
    protected ItemType itemType;
    protected double usageMonitor;
    protected int statusID;
    protected GameUser owner;

    public ItemType getType() { return itemType; }

    public String getTypeName(){
        return itemType.getName();
    }

    public GameUser getOwner() { return owner; }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------

    GameItem(GameItemRecord rec) {
        itemID = rec.itemID;
        itemType = rec.itemType;
        usageMonitor = rec.usageMonitor;
        statusID = rec.statusID;
    }

    void setOwner(GameUser user) { owner = user; }
}
