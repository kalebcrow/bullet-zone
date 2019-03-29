/**
 * Internal package class for creating and interpreting database data corresponding to GameItems
 */
package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

class GameItemRepository {
    HashMap<Integer, GameItem> itemMap = new HashMap<Integer, GameItem>();
    HashMap<Integer, GameItemContainer> containerMap = new HashMap<Integer, GameItemContainer>();
    ItemTypeRepository typeRepo;
    Connection dataConnection;

    /**
     * Return the GameItem associated with the passed internal ID (does not return containers)
     * @param itemID    ID for the item being requested
     * @return  GameItem corresponding to the passed itemID
     */
    public GameItem getItem(int itemID) { return itemMap.get(itemID); }

    /**
     * Return the GameItemContainer associated with teh passed internal ID
     * @param itemID    ID for the container being requested
     * @return  GameItemContainer corresponding to the passed itemID;
     */
    public GameItemContainer getContainer(int itemID) { return containerMap.get(itemID); }

    /**
     * Return the GameItem associated with teh passed internal ID (may be a container)
     * @param itemID    ID for the item/container being requested
     * @return  GameItem corresponding to the passed itemID;
     */
    public GameItem getItemOrContainer(int itemID) {
        GameItem item = itemMap.get(itemID);
        if (item == null)
            item = containerMap.get(itemID);
        return item;
    }

    /**
     * Return a collection of all containers that are not deleted
     *
     * @return  Collection of all GameItemContainers that do not have a "Deleted" status
     */
    public Collection<GameItemContainer> getContainers() {
        return containerMap.values();
    }

    /**
     * Return a collection of all non-container items that are not deleted
     *
     * @return  Collection of all GameItems that do not have a "Deleted" status
     */
    public Collection<GameItem> getGameItems() {
        return itemMap.values();
    }

    /**
     * Create an item of the passed type and insert it into the database
     *
     * @param typeName  The name of the type for the item to be created (can be a container)
     * @return          An appropriate GameItem representing what was added to the database
     */
    public GameItem create(String typeName) { return create(typeRepo.get(typeName)); }

    /**
     * Create an container of the passed type and insert it into the database
     *
     * @param typeName  The name of the type for the item to be created (can be a container)
     * @return          An appropriate GameItemContainer representing what was added to the database,
     *                  or null if the type is not a container type.
     */
    public GameItemContainer createContainer(String typeName) {
        ItemType type = typeRepo.get(typeName);
        if (type.isContainer())
            return (GameItemContainer)create(typeRepo.get(typeName));
        else
            return null;
    }

    /**
     * Create a new GameItem of type itemType and insert it into the database and the appropriate
     * hashmap. This method can create both GameItemContainers and GameItems.
     * @param itemType  Individual type to be created
     * @return  GameItem representation of the item that was inserted into the database.
     * @throws IllegalStateException for any database errors encountered.
     */
    public GameItem create(ItemType itemType) {
        GameItemRecord rec = new GameItemRecord();
        rec.itemType = itemType;
        rec.usageMonitor = 0;
        rec.statusID = Status.Active.ordinal();
        Date date = new Date();
        rec.created = new Timestamp(date.getTime());
        String name = "[Unnamed]";
        GameItem newItem;
        try {
            // Create base item
            PreparedStatement insertStatement = dataConnection.prepareStatement(
                    " INSERT INTO Item ( ItemTypeID, UsageMonitor, StatusID, Created )\n" +
                            "    VALUES (" + rec.itemType.getID() + ", "
                            + rec.usageMonitor + ", "
                            + rec.statusID + ", '"
                            + rec.created + "'); ", Statement.RETURN_GENERATED_KEYS);
            int affectedRows = insertStatement.executeUpdate();
            if (affectedRows == 0)
                throw new SQLException("Creating Item of type " + itemType.getName() + " failed.");

            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                rec.itemID = generatedKeys.getInt(1);
            }
            else {
                throw new SQLException("Created Item of type " + itemType.getName() + " but failed to obtain ID.");
            }

            // Create ItemContainer record if it's a container, then create actual GameItem/Container
            if (itemType.isContainer()) {
                PreparedStatement containerStatement = dataConnection.prepareStatement(
                        "INSERT INTO ItemContainer ( ItemID, Name ) VALUES ( " + rec.itemID + ", '" + name + "');");
                affectedRows = containerStatement.executeUpdate();
                if (affectedRows == 0)
                    throw new SQLException("Creating ItemContainer record for type " + itemType.getName() + " failed.");

                newItem = new GameItemContainer(rec, name);
                containerMap.put(rec.itemID, (GameItemContainer)newItem);
            }
            else
                newItem = new GameItem(rec);
            itemMap.put(rec.itemID, newItem);
        } catch (SQLException e) {
            throw new IllegalStateException("Error while creating item!", e);
        }
        System.out.println("New " + newItem.getTypeName() + " added with ID " + rec.itemID);
        return newItem;
    }

    /**
     * Add the passed item to the passed container in both the database and in-memory
     * representations. Note that it is up to the caller to ensure that capacity limits are
     * not being exceeded--no checks are done here.
     * @param item    GameItem to be added
     * @param container   GameItemContainer we're adding into
     * @return  true if the operation was successful, and false otherwise.
     */
    public boolean addItemToContainer(GameItem item, GameItemContainer container) {
        return addItemToContainer(item.itemID, container.itemID);
    }

    /**
     * Add the referenced item to the referenced container in both the database and in-memory
     * representations. Note that it is up to the caller to ensure that capacity limits are
     * not being exceeded--no checks are done here.
     * @param itemID    ID of the item to be added
     * @param containerID   ID of the container we're adding into
     * @return  true if the operation was successful, and false otherwise.
     */
    public boolean addItemToContainer(int itemID, int containerID) {
        GameItem item = itemMap.get(itemID);
        GameItemContainer container = containerMap.get(containerID);
        if (item == null || container == null)
            return false;

        ItemContainmentRecord rec = new ItemContainmentRecord();
        rec.itemID = itemID;
        rec.container_itemID = containerID;
        try {
            // Create base item
            PreparedStatement insertStatement = dataConnection.prepareStatement(
                    " INSERT INTO ItemContainer_Item ( Container_ItemID, ItemID, StartSlot, EndSlot, Modifier )\n" +
                            "    VALUES (" + rec.container_itemID + ", "
                            + rec.itemID + ", "
                            + rec.startSlot + ", "
                            + rec.endSlot + ", "
                            + rec.modifier + "); ");
            if (insertStatement.executeUpdate() == 0)
                return false;
        } catch (SQLException e) {
            throw new IllegalStateException("Error while adding item to container.", e);
        }

        container.addItem(item);
        return true;
    }

    /**
     * Removes the passed item from the passed container in both the database and in-memory
     * representations.
     * @param item    GameItem to be removed
     * @param container   GameItemContainer we're removing from
     * @return  true if the operation was successful, and false otherwise.
     */
    public boolean removeItemFromContainer(GameItem item, GameItemContainer container) {
        return removeItemFromContainer(item.itemID, container.itemID);
    }

    /**
     * Removes the referenced item from the referenced container in both the database and in-memory
     * representations.
     * @param itemID    ID of the item to be removed
     * @param containerID   ID of the container we're removing from
     * @return  true if the operation was successful, and false otherwise.
     */
    public boolean removeItemFromContainer(int itemID, int containerID) {
        GameItem item = itemMap.get(itemID);
        GameItemContainer container = containerMap.get(containerID);
        if (item == null || container == null)
            return false;

        try {
            PreparedStatement deleteStatement = dataConnection.prepareStatement(
                    " DELETE FROM ItemContainer_Item WHERE " +
                            " Container_ItemID=" + containerID + " AND " +
                            " ItemID=" + itemID + "; ");
            if (deleteStatement.executeUpdate() == 0)
                return false; //nothing deleted
        } catch (SQLException e) {
            throw new IllegalStateException("Error while removing item from container.", e);
        }

        container.removeItem(item);
        return true;
    }

    /**
     * Removes all items from the passed container in both the database and in-memory
     * representations.
     * @param container   GameItemContainer we're removing from
     * @return  true if the operation was successful, and false otherwise.
     */
    public boolean removeAllFromContainer(GameItemContainer container) {
        return removeAllFromContainer(container.itemID);
    }

    /**
     * Removes all items from the referenced container in both the database and in-memory
     * representations.
     * @param containerID   ID of the container we're removing from
     * @return  true if the operation was successful, and false otherwise.
     */
    public boolean removeAllFromContainer(int containerID) {
        GameItemContainer container = containerMap.get(containerID);
        if (container == null)
            return false;

        try {
            PreparedStatement deleteStatement = dataConnection.prepareStatement(
                    " DELETE FROM ItemContainer_Item WHERE " +
                            " Container_ItemID=" + containerID + "; ");
            if (deleteStatement.executeUpdate() == 0)
                return false; //nothing deleted
        } catch (SQLException e) {
            throw new IllegalStateException("Error while removing all items from container.", e);
        }

        container.removeAll();
        return true;
    }

    /**
     * Deletes the referenced item from the in-memory representation,
     * marks it as deleted in the database, and removes it from any containers in the database.
     * NOTE: this method does not remove the item from its container in the in-memory representation.
     * @param itemID    ID of the item to be marked as deleted
     * @return  true if the operation was successful, and false otherwise.
     */
    public boolean delete(int itemID) {
        boolean isContainer = containerMap.containsKey(itemID);
        if (!isContainer && !itemMap.containsKey(itemID))
            return false;

        Date date = new Date();

        try {
            PreparedStatement deleteStatement = dataConnection.prepareStatement(
                    " DELETE FROM ItemContainer_Item WHERE " +
                            " ItemID=" + itemID + "; ");
        } catch (SQLException e) {
            throw new IllegalStateException("Error while deleting item from container.", e);
        }
        try {
            PreparedStatement updateStatement = dataConnection.prepareStatement(
                    " UPDATE Item SET StatusID=" + Status.Deleted.ordinal() +
                                        ", Deleted='" + new Timestamp(date.getTime()) +
                                        "' WHERE ItemID=" + itemID + "; ");
            if (updateStatement.executeUpdate() == 0)
                return false; //nothing deleted
        } catch (SQLException e) {
            throw new IllegalStateException("Error while deleting item.", e);
        }

        if (isContainer)
            containerMap.remove(itemID);
        else
            itemMap.remove(itemID);

        return true;
    }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------

    /**
     * Reads the database and fills the HashMaps as appropriate. Intended to be called once
     * at time of initialization.
     *
     * @param sqlDataConnection connection on which to make all future SQL queries
     * @param itemTypeRepo      reference to an already-initialized ItemTypeRepository
     */
    void refresh(Connection sqlDataConnection, ItemTypeRepository itemTypeRepo) {
        typeRepo = itemTypeRepo;
        dataConnection = sqlDataConnection;
        try {
            Statement statement = dataConnection.createStatement();

            // Read collections that aren't deleted
            ResultSet itemContainerResult = statement.executeQuery(
                    "SELECT * FROM ItemContainer c, Item i WHERE c.ItemID = i.ItemID" +
                            " AND i.StatusID != " + Status.Deleted.ordinal());
            while (itemContainerResult.next()) {
                GameItemRecord rec = makeItemRecordFromResultSet(itemContainerResult, itemTypeRepo);
                GameItemContainer container = new GameItemContainer(rec, itemContainerResult.getString("c.Name"));
                containerMap.put(rec.itemID, container);
                itemMap.put(rec.itemID, container);
            }

            // Read non-collections (non-Frames)
            ResultSet itemResult = statement.executeQuery(
                    "SELECT * FROM Item i WHERE ItemTypeID >= 20 AND StatusID != " + Status.Deleted.ordinal());
            while (itemResult.next()) {
                GameItemRecord rec = makeItemRecordFromResultSet(itemResult, itemTypeRepo);
                itemMap.put(rec.itemID, new GameItem(rec));
            }

            // Read mapping of collections to items that are inside them
            ResultSet mappingResult = statement.executeQuery("SELECT * FROM ItemContainer_Item"); //Non-frames
            while (mappingResult.next()) {
                int containerID = mappingResult.getInt("Container_ItemID");
                int itemID = mappingResult.getInt("ItemID");
                // not worrying about StartSlot, EndSlot, or Modifier right now...
                GameItemContainer container = getContainer(containerID);
                GameItem item = getItem(itemID);
                container.addItem(item);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot read static info!", e);
        }
    }

    /**
     * Converts a ResultSet to a GameItemRecord for further processing. It assumes that the record
     * it should be getting data for was labeled with the name "i".
     * @param itemResult    The ResultSet that's the result of an SQL query with item labeled "i"
     * @param itemTypeRepo  Reference to an initialized ItemTypeRepository
     * @return GameItemRecord filled with data from the current item in the ResultSet.
     */
    private GameItemRecord makeItemRecordFromResultSet(ResultSet itemResult, ItemTypeRepository itemTypeRepo) {
        GameItemRecord rec = new GameItemRecord();
        try {
            rec.itemID = itemResult.getInt("i.ItemID");
            rec.itemType = itemTypeRepo.typeMap.get(itemResult.getInt("i.ItemTypeID"));
            rec.usageMonitor = itemResult.getDouble("i.UsageMonitor");
            rec.statusID = itemResult.getInt("i.StatusID");
            rec.created = itemResult.getTimestamp("i.Created");
            rec.deleted = itemResult.getTimestamp("i.Deleted");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to extract data fom item result set", e);
        }
        return rec;
    }

}
