//add to dependencies in build.gradle:    implementation 'org.mariadb.jdbc:mariadb-java-client:2.1.2'

package edu.unh.cs.cs619.bulletzone.datalayer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class BulletZoneData {
    private Connection dataConnection;
    private HashMap<Integer, ItemCategory> categoryMap = new HashMap<Integer, ItemCategory>();
    private HashMap<Integer, ItemType> typeMap = new HashMap<Integer, ItemType>();
    private HashMap<Integer, FrameType> frameMap = new HashMap<Integer, FrameType>();
    private HashMap<Integer, WeaponType> weaponMap = new HashMap<Integer, WeaponType>();
    private HashMap<Integer, GeneratorType> generatorMap = new HashMap<Integer, GeneratorType>();
    private HashMap<Integer, EngineType> engineMap = new HashMap<Integer, EngineType>();
    private HashMap<Integer, DriveType> driveMap = new HashMap<Integer, DriveType>();

    public BulletZoneData(String url, String username, String password)
    {
        try {
            dataConnection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected.");
        } catch (
                SQLException e) {
            throw new IllegalStateException("Cannot connect to the database!", e);
        }

        try {
            Statement statement = dataConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("show tables");
            if (!resultSet.next()) //no next row means we don't have any tables
                initializeData();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot access tables!", e);
        }

        readStaticInfo();
    }

    public void listTables() {
        try {
            Statement statement = dataConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("show tables");
            System.out.println("Database tables:");
            while (resultSet.next())
                System.out.println(resultSet.getString(1));

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot show tables!", e);
        }
    }

    public void close() {
        try {
            dataConnection.close();
            System.out.println("Database connection closed.");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot close the database!", e);
        }
    }

    public Collection<ItemCategory> getItemCategories() {
        return categoryMap.values();
    }

    public Collection<ItemType> getItemTypes() {
        return typeMap.values();
    }

    public Collection<FrameType> getFrameTypes() {
        return frameMap.values();
    }

    public Collection<WeaponType> getWeaponTypes() {
        return weaponMap.values();
    }

    public Collection<GeneratorType> getGeneratorTypes() {
        return generatorMap.values();
    }

    public Collection<EngineType> getEngineTypes() {
        return engineMap.values();
    }

    public Collection<DriveType> getDriveTypes() {
        return driveMap.values();
    }

    private void readStaticInfo() {
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

    private void initializeData() {
        executeScript("/CreateTables.sql", "Cannot create tables!");
        executeScript("/PopulateTables.sql", "Cannot populate tables!");
    }

    public void rebuildData() {
        executeScript("/DropTables.sql", "Cannot drop all tables!");
        initializeData();
    }

    ResultSet executeScript(String resourceName, String errorMessage)
    {
        ResultSet resultSet = null;
        try {
            Statement statement = dataConnection.createStatement();
            InputStream in = getClass().getResourceAsStream(resourceName);
            if (in == null)
                System.out.println("missing resource file");
            else {
                String createQuery = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
                resultSet = statement.executeQuery(createQuery);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(errorMessage, e);
        }
        return resultSet;
    }
}
