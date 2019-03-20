/**
 * This is the main interaction point for the datalayer package.
 * <p/>
 * Each instance of this class corresponds to an instance of a game database. In most cases,
 * you will just want a single instance (and it could be problematic to have more than one
 * instance). However, since it's possible that one application may need to be able to access
 * more than one game database at a time, this class is not a singleton.
 * It is important to remember to close the database connection when you're done with the
 * BulletZoneData object, so that you don't open so many connections that the server becomes
 * inaccessible (the connections will otherwise time out eventually, but it may not be as
 * early as you like).
 *
 */
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
import java.util.stream.Collectors;

public class BulletZoneData {
    private Connection dataConnection;
    ItemTypeRepository typeRepo = new ItemTypeRepository();
    GameItemRepository itemRepo = new GameItemRepository();

    /**
     * Opens the database at the specified URL and initializes internal structures
     * @param url       URL for the database, usually in the form "jdbc:mysql://server-name.com:3306/database-name"
     * @param username  Username with appropriate access to the database specified by url
     * @param password  Password for the user specified by username
     */
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

        typeRepo.readStaticInfo(dataConnection);
        itemRepo.refresh(dataConnection, typeRepo);
    }

    /**
     * Lists the names of all tables in the currently open database to System.out, one per line.
     * This is intended to be a debugging method only.
     */
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

    /**
     * Close the database connection. Calls to other methods after this method is called
     * will have undefined behavior and may cause an exception.
     */
    public void close() {
        try {
            dataConnection.close();
            System.out.println("Database connection closed.");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot close the database!", e);
        }
    }

    /**
     * Return a collection of all containers that are not deleted
     *
     * @return  Collection of all GameItemContainers that do not have a "Deleted" status
     */
    public Collection<GameItemContainer> getContainers() {
        return itemRepo.containerMap.values();
    }

    /**
     * Return a collection of all non-container items that are not deleted
     *
     * @return  Collection of all GameItems that do not have a "Deleted" status
     */
    public Collection<GameItem> getGameItems() {
        return itemRepo.itemMap.values();
    }

    /**
     * Create an item of the passed type and insert it into the database
     *
     * @param type  The individual type for the item to be created (can be a container)
     * @return      An appropriate GameItem representing what was added to the database
     */
    public GameItem createItem(ItemType type) { return itemRepo.create(type);}

    /**
     * Create an item of the passed type and insert it into the database
     *
     * @param typeName  The name of the type for the item to be created (can be a container)
     * @return          An appropriate GameItem representing what was added to the database
     */
    public GameItem createItem(String typeName) { return createItem(getTypeFromName(typeName)); }

    public ItemType getTypeFromName(String typeName) {
        ItemType type = typeRepo.nameToTypeMap.get(typeName);
        if (type == null)
            throw new NullPointerException("Unable to resolve " + typeName + " to a valid type of game item.");
        return type;
    }

    /**
     * @return A collection of all ItemCategories in the database
     */
    public Collection<ItemCategory> getItemCategories() { return typeRepo.categoryMap.values(); }

    /**
     * @return A collection of all ItemTypes in the database
     */
    public Collection<ItemType> getItemTypes() {
        return typeRepo.typeMap.values();
    }

    /**
     * @return A collection of only the FrameTypes in the database
     */
    public Collection<FrameType> getFrameTypes() {
        return typeRepo.frameMap.values();
    }

    /**
     * @return A collection of only the WeaponTypes in the database
     */
    public Collection<WeaponType> getWeaponTypes() {
        return typeRepo.weaponMap.values();
    }

    /**
     * @return A collection of only the GeneratorTypes in the database
     */
    public Collection<GeneratorType> getGeneratorTypes() {
        return typeRepo.generatorMap.values();
    }

    /**
     * @return A collection of only the EngineTypes in the database
     */
    public Collection<EngineType> getEngineTypes() {
        return typeRepo.engineMap.values();
    }

    /**
     * @return A collection of only the DriveTypes in the database
     */
    public Collection<DriveType> getDriveTypes() {
        return typeRepo.driveMap.values();
    }

    /**
     * Assuming an empty database, creates all the appropriate tables and then populates
     * them with appropriate data (especially for enumeration tables).
     */
    private void initializeData() {
        executeScript("/CreateTables.sql", "Cannot create tables!");
        executeScript("/PopulateTables.sql", "Cannot populate tables!");
    }

    /**
     * Drops all tables (which destroys all existing data!!), then rebuilds it by calling the
     * initializeData method.
     */
    public void rebuildData() {
        executeScript("/DropTables.sql", "Cannot drop all tables!");
        initializeData();
    }

    /**
     * Executes the passed SQL script on the current database (it assumes the account given
     * at construction time has the appropriate permissions to execute whatever is in the script).
     * @param resourceName  Name of the Java resource file, usually of the form "/SomeFile.sql"
     * @param errorMessage  String to be printed
     * @return  A SQL ResultSet with the raw result of the query.
     */
    ResultSet executeScript(String resourceName, String errorMessage)
    {
        ResultSet resultSet = null;
        try {
            Statement statement = dataConnection.createStatement();
            InputStream in = getClass().getResourceAsStream(resourceName);
            if (in == null)
                System.err.println("missing resource file");
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
