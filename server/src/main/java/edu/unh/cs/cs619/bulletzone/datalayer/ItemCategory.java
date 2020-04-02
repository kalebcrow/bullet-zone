/**
 * Public class for accessing data for individual categories in the ItemCategory table
 */
package edu.unh.cs.cs619.bulletzone.datalayer;

import java.util.HashMap;

public class ItemCategory {
    private final int itemCategoryID;
    private final String name;
    //private final int itemPropertyID[];
    public final HashMap<ItemProperty, Integer> propertyMap = new HashMap<ItemProperty, Integer>();

    ItemCategory(int id, String categoryName, ItemProperty itemProperties[]) {
        itemCategoryID = id;
        name = categoryName;
        //itemPropertyID = itemProperties;
        int index = 0;
        for (ItemProperty prop : itemProperties)
            propertyMap.put(prop, index++);
    }

    @Override
    public String toString() { return name; }

    public int getID() {
        return itemCategoryID;
    }

    public String getName(){
        return name;
    }
}
