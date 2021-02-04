/**
 * Public class for accessing data for individual items in the Item table
 */
package edu.unh.cs.cs619.bulletzone.datalayer;

import edu.unh.cs.cs619.bulletzone.datalayer.itemType.Describable;
import edu.unh.cs.cs619.bulletzone.datalayer.itemType.ItemProperty;
import edu.unh.cs.cs619.bulletzone.datalayer.itemType.ItemType;
import edu.unh.cs.cs619.bulletzone.datalayer.permission.OwnableEntity;

public class GameItem extends OwnableEntity implements Describable {
    protected ItemType itemType;
    protected double usageMonitor;
    protected GameItemContainer parent;

    public ItemType getType() { return itemType; }

    public String getTypeName(){
        return itemType.getName();
    }

    public GameItemContainer getParent() { return parent; }

    public boolean isContainer() { return itemType.isContainer();}

    /** Returns the property of only the item, not all its contents */
    public double getLocalProperty(ItemProperty property) { return itemType.getProperty(property); }

    /** Returns the aggregate property of the item, including its contents (if any) */
    public double getProperty(ItemProperty property) {return getLocalProperty(property);}

    public double getSize() {return itemType.getSize();}
    public double getWeight() {return itemType.getWeight();}
    public double getPrice() {return itemType.getPrice();}

    //----------------------------------END OF PUBLIC METHODS--------------------------------------
    GameItem(GameItemRecord rec) {
        super(rec);
        itemType = rec.itemType;
        usageMonitor = rec.usageMonitor;
    }

    protected void setParent(GameItemContainer container) {
        parent = container;
    }
}
