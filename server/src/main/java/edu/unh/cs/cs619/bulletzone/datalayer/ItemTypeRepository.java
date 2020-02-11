/**
 * Internal package class for interpreting (and creating?) database data corresponding to ItemTypes
 */
package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;

public class ItemTypeRepository {
    HashMap<Integer, ItemCategory> categoryMap = new HashMap<Integer, ItemCategory>();
    HashMap<Integer, ItemCategory> baseCategoryMap = new HashMap<Integer, ItemCategory>();
    HashMap<Integer, ItemType> typeMap = new HashMap<Integer, ItemType>();
    HashMap<Integer, ItemType> frameMap = new HashMap<Integer, ItemType>();
    HashMap<Integer, ItemType> weaponMap = new HashMap<Integer, ItemType>();
    HashMap<Integer, ItemType> generatorMap = new HashMap<Integer, ItemType>();
    HashMap<Integer, ItemType> engineMap = new HashMap<Integer, ItemType>();
    HashMap<Integer, ItemType> driveMap = new HashMap<Integer, ItemType>();
    HashMap<String, ItemType> nameToTypeMap = new HashMap<>();

    /**
     * Gives the ItemType associated with the passed name
     * @param typeName  Name of the desired type (case-sensitive)
     * @return  ItemType object corresponding to the typeName
     */
    public ItemType get(String typeName) {
        ItemType type = nameToTypeMap.get(typeName);
        if (type == null)
            throw new NullPointerException("Unable to resolve " + typeName + " to a valid type of game item.");
        return type;
    }

    /**
     * @return A collection of all ItemCategories in the database
     */
    public Collection<ItemCategory> getCategories() { return baseCategoryMap.values(); }

    /**
     * @return A collection of all ItemTypes in the database
     */
    public Collection<ItemType> getTypes() {
        return typeMap.values();
    }

    /**
     * @param typeID    Type ID for the requested type (used in other objects)
     * @return  ItemType corresponding to the passed typeID
     */
    public ItemType getType(int typeID) { return typeMap.get(typeID); }

    /**
     * @return A collection of only the FrameTypes in the database
     */
    public Collection<ItemType> getFrames() {
        return frameMap.values();
    }

    /**
     * @return A collection of only the WeaponTypes in the database
     */
    public Collection<ItemType> getWeapons() {
        return weaponMap.values();
    }

    /**
     * @return A collection of only the GeneratorTypes in the database
     */
    public Collection<ItemType> getGenerators() {
        return generatorMap.values();
    }

    /**
     * @return A collection of only the EngineTypes in the database
     */
    public Collection<ItemType> getEngines() {
        return engineMap.values();
    }

    /**
     * @return A collection of only the DriveTypes in the database
     */
    public Collection<ItemType> getDrives() {
        return driveMap.values();
    }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------

    /**
     * Reads the database and fills the HashMaps as appropriate with information about
     * ItemCategories and ItemTypes. Intended to be called once at time of initialization.
     *
     * @param dataConnection connection on which to make SQL queries
     */
    void readStaticInfo(Connection dataConnection) {
        try {
            Statement statement = dataConnection.createStatement();

            ResultSet itemPropertyResult = statement.executeQuery("SELECT * FROM ItemProperty");
            while (itemPropertyResult.next()) {
                int id = itemPropertyResult.getInt("ItemPropertyID");
                int type = itemPropertyResult.getInt("ItemPropertyTypeID");
                String name = itemPropertyResult.getString("Name");
                ItemProperty ip = new ItemProperty(id, name, ItemProperty.PropertyType.values()[type]);
            }

            ResultSet itemCategoryResult = statement.executeQuery("SELECT * FROM ItemCategory");
            while (itemCategoryResult.next()) {
                int id = itemCategoryResult.getInt("ItemCategoryID");
                int properties[] = new int[3];
                for (int i = 0; i < 3; i++)
                    properties[i] = itemCategoryResult.getInt("ItemPropertyID" + (i + 1));
                ItemCategory ic = new ItemCategory(id, itemCategoryResult.getString("Name"), properties);
                categoryMap.put(id, ic);
                if (id < 10)
                    baseCategoryMap.put(id, ic);
            }

            ResultSet itemTypeResult = statement.executeQuery("SELECT * FROM ItemType");
            while (itemTypeResult.next()) {
                ItemTypeRecord rec = new ItemTypeRecord();
                rec.itemTypeID = itemTypeResult.getInt("ItemTypeID");
                rec.name = itemTypeResult.getString("Name");
                rec.category = categoryMap.get(itemTypeResult.getInt("ItemCategoryID"));
                rec.val = new double[ItemProperty.ID.values().length];
                for (ItemProperty.ID id : ItemProperty.ID.values())
                    rec.val[id.ordinal()] = itemTypeResult.getDouble(id.name());
                recordItemType(rec);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot read static info!", e);
        }
    }

    /**
     * Internal factory method for generating an ItemType based on information in the passed
     * ItemTypeRecord and inserting it into the appropriate HashMap.
     * @param rec   reference to an ItemTypeRecord that's the result of an SQL query
     */
    private void recordItemType(ItemTypeRecord rec) {
        ItemType itemType;
        int id = rec.itemTypeID;
        boolean idFrame = (rec.category.getName() == "Frame");
        itemType = new ItemType(rec);
        typeMap.put(id, itemType);
        nameToTypeMap.put(rec.name, itemType);
        switch (rec.category.getName())
        {
            case "Frame":
                frameMap.put(id, itemType);
                break;
            case "Weapon":
                weaponMap.put(id, itemType);
                break;
            case "Generator":
                generatorMap.put(id, itemType);
                break;
            case "Engine":
                engineMap.put(id, itemType);
                break;
            case "Drive":
                driveMap.put(id, itemType);
                break;
        }
    }
}
