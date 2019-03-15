package edu.unh.cs.cs619.bulletzone.datalayer;

public class GameItem {
    protected int itemID;
    protected ItemType itemType;
    protected double usageMonitor;
    protected int statusID;

    GameItem(GameItemRecord rec) {
        itemID = rec.itemID;
        itemType = rec.itemType;
        usageMonitor = rec.usageMonitor;
        statusID = rec.statusID;
    }

    public ItemType getType() { return itemType; }
    public String getTypeName(){
        return itemType.getName();
    }
}
