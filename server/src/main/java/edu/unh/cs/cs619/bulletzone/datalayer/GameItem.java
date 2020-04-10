/**
 * Public class for accessing data for individual items in the Item table
 */
package edu.unh.cs.cs619.bulletzone.datalayer;

public class GameItem implements Describable {
    protected int itemID;
    protected ItemType itemType;
    protected double usageMonitor;
    protected int statusID;
    protected GameUser owner;

    @Override
    public String toString() { return getTypeName() + " (ID: " + itemID + ")"; }

    public int getItemID() { return itemID; }

    public ItemType getType() { return itemType; }

    public String getTypeName(){
        return itemType.getName();
    }

    public GameUser getOwner() { return owner; }

    public boolean isContainer() {return itemType.isContainer();}

    public double getProperty(ItemProperty property) {return itemType.getProperty(property);}

    public double getSize() {return itemType.getSize();}
    public double getWeight() {return itemType.getWeight();}
    public double getPrice() {return itemType.getPrice();}

    //----------------------------------END OF PUBLIC METHODS--------------------------------------

    GameItem(GameItemRecord rec) {
        itemID = rec.itemID;
        itemType = rec.itemType;
        usageMonitor = rec.usageMonitor;
        statusID = rec.statusID;
    }

    void setOwner(GameUser user) { owner = user; }
}
