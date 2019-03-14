package edu.unh.cs.cs619.bulletzone.datalayer;

public class ItemCategory {
    private final int itemCategoryID;
    private final String name;

    ItemCategory(int id, String categoryName) {
        itemCategoryID = id;
        name = categoryName;
    }

    public int getID() {
        return itemCategoryID;
    }

    public String getName(){
        return name;
    }
}
