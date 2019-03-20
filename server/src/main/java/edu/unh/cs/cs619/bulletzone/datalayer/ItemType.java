/**
 * Public class for accessing data for individual types in the ItemType table
 */
package edu.unh.cs.cs619.bulletzone.datalayer;

public class ItemType {
    protected final ItemTypeRecord info;

    ItemType(ItemTypeRecord rec) {
        info = rec;
    }

    public int getID() {
        return info.itemTypeID;
    }

    public String getName() {
        return info.name;
    }

    public ItemCategory getCategory() {
        return info.category;
    }

    public boolean isContainer() {
        return false;
    }

    public double getSize() {
        return info.size;
    }

    public double getWeight() {
        return info.weight;
    }

    public double getPrice() {
        return info.price;
    }
}
