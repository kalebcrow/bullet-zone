package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class ItemTypeRepository {
    HashMap<Integer, ItemCategory> categoryMap = new HashMap<Integer, ItemCategory>();
    HashMap<Integer, ItemType> typeMap = new HashMap<Integer, ItemType>();
    HashMap<Integer, FrameType> frameMap = new HashMap<Integer, FrameType>();
    HashMap<Integer, WeaponType> weaponMap = new HashMap<Integer, WeaponType>();
    HashMap<Integer, GeneratorType> generatorMap = new HashMap<Integer, GeneratorType>();
    HashMap<Integer, EngineType> engineMap = new HashMap<Integer, EngineType>();
    HashMap<Integer, DriveType> driveMap = new HashMap<Integer, DriveType>();

    void readStaticInfo(Connection dataConnection) {
        try {
            Statement statement = dataConnection.createStatement();
            ResultSet itemTypeResult = statement.executeQuery("SELECT * FROM ItemType");
            ResultSet itemCategoryResult = statement.executeQuery("SELECT * FROM ItemCategory");
            while (itemCategoryResult.next()) {
                int id = itemCategoryResult.getInt("ItemCategoryID");
                categoryMap.put(id, new ItemCategory(id, itemCategoryResult.getString("Name")));
            }
            System.out.println("Item types:");
            while (itemTypeResult.next()) {
                ItemTypeRecord rec = new ItemTypeRecord();
                rec.itemTypeID = itemTypeResult.getInt("ItemTypeID");
                rec.name = itemTypeResult.getString("Name");
                rec.category = categoryMap.get(itemTypeResult.getInt("ItemCategoryID"));
                rec.size = itemTypeResult.getDouble("Size");
                rec.weight = itemTypeResult.getDouble("Weight");
                rec.price = itemTypeResult.getDouble("Price");
                rec.val1 = itemTypeResult.getDouble("PropertyVal1");
                rec.val2 = itemTypeResult.getDouble("PropertyVal2");
                rec.val3 = itemTypeResult.getDouble("PropertyVal3");
                recordItemType(rec);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot read static info!", e);
        }
    }

    ///Factory method for generating an ItemTypes based on a record
    private void recordItemType(ItemTypeRecord rec) {
        ItemType itemType;
        int id = rec.itemTypeID;
        switch (rec.category.getName())
        {
            case "Frame":
                frameMap.put(id, new FrameType(rec));
                itemType = frameMap.get(id);
                break;
            case "Weapon":
                weaponMap.put(id, new WeaponType(rec));
                itemType = weaponMap.get(id);
                break;
            case "Generator":
                generatorMap.put(id, new GeneratorType(rec));
                itemType = generatorMap.get(id);
                break;
            case "Engine":
                engineMap.put(id, new EngineType(rec));
                itemType = engineMap.get(id);
                break;
            case "Drive":
                driveMap.put(id, new DriveType(rec));
                itemType = driveMap.get(id);
                break;
            default:
                itemType = new ItemType(rec);
        }
        typeMap.put(id, itemType);
    }
}
