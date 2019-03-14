package edu.unh.cs.cs619.bulletzone.datalayer;

public class GameItem {
    protected int itemID;
    protected ItemType itemType;

    protected float usageMonitor;

    public String getTypeName(){
        return itemType.getName();
    }
}
