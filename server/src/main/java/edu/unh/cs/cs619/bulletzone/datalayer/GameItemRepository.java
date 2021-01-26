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

import edu.unh.cs.cs619.bulletzone.datalayer.itemType.ItemType;
import edu.unh.cs.cs619.bulletzone.datalayer.itemType.ItemTypeRepository;

public class GameItemRepository implements PermissionTargetRepository {
    HashMap<Integer, GameItem> itemMap = new HashMap<Integer, GameItem>();
    HashMap<Integer, GameItemContainer> containerMap = new HashMap<Integer, GameItemContainer>();
    ItemTypeRepository typeRepo;
    BulletZoneData data;

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
    public PermissionTarget getTarget(int id) { return getContainer(id); }

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
        return createContainer(typeRepo.get(typeName));
    }

    public GameItemContainer createContainer(ItemType itemType) {
        if (itemType.isContainer())
            return (GameItemContainer)create(itemType);
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
        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return null;

        GameItemRecord rec = new GameItemRecord(itemType);
        String name = "[Unnamed " + itemType.getName() + "]";
        GameItem newItem;
        try {
            // Create base item
            PreparedStatement insertStatement = dataConnection.prepareStatement(
                    rec.getInsertString(), Statement.RETURN_GENERATED_KEYS);
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
            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error while creating item!", e);
        }
        System.out.println("New " + newItem.getTypeName() + " added with ID " + rec.itemID);
        return newItem;
    }

    /**
     * Sets the name of the passed container to the passed name (and updates the database)
     * @param container GameItemContainer to be renamed
     * @param name      String containing the new name of the container. It may not contain single quotes.
     * @return true if successful, and false otherwise
     */
    public boolean renameContainer(GameItemContainer container, String name) {
        if (container == null || name.contains("'"))
            return false;

        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return false;

        try {
            PreparedStatement updateStatement = dataConnection.prepareStatement(
                    " UPDATE ItemContainer SET Name='" + name +
                            "' WHERE ItemID=" + container.getItemID() + "; ");
            if (updateStatement.executeUpdate() == 0) {
                dataConnection.close();
                return false; //nothing deleted
            }
            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error while deleting item.", e);
        }

        container.name = name;
        return true;
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
        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return false;

        ItemContainmentRecord rec = new ItemContainmentRecord(itemID, containerID);
        try {
            // Create base item
            PreparedStatement insertStatement = dataConnection.prepareStatement(rec.getInsertString());
            if (insertStatement.executeUpdate() == 0) {
                dataConnection.close();
                return false;
            }
            dataConnection.close();
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
        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return false;

        try {
            PreparedStatement deleteStatement = dataConnection.prepareStatement(
                    " DELETE FROM ItemContainer_Item WHERE " +
                            " Container_ItemID=" + containerID + " AND " +
                            " ItemID=" + itemID + "; ");
            if (deleteStatement.executeUpdate() == 0) {
                dataConnection.close();
                return false; //nothing deleted
            }
            dataConnection.close();
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
        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return false;

        try {
            PreparedStatement deleteStatement = dataConnection.prepareStatement(
                    " DELETE FROM ItemContainer_Item WHERE " +
                            " Container_ItemID=" + containerID + "; ");
            if (deleteStatement.executeUpdate() == 0) {
                dataConnection.close();
                return false; //nothing deleted
            }
            dataConnection.close();
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
     * @param item    GameItem to be marked as deleted
     * @return  true if the operation was successful, and false otherwise.
     */
    public boolean delete(GameItem item) {
        return delete(item.itemID);
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
        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
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
            if (updateStatement.executeUpdate() == 0) {
                dataConnection.close();
                return false; //nothing deleted
            }
            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error while deleting item.", e);
        }

        if (isContainer)
            containerMap.remove(itemID);
        itemMap.remove(itemID);

        return true;
    }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------

    /**
     * Reads the database and fills the HashMaps as appropriate. Intended to be called once
     * at time of initialization.
     *
     * @param bzData        reference to BulletZoneData class to use for SQL queries
     * @param itemTypeRepo  reference to an already-initialized ItemTypeRepository
     */
    void refresh(BulletZoneData bzData, ItemTypeRepository itemTypeRepo) {
        typeRepo = itemTypeRepo;
        data = bzData;
        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return;
        try {
            Statement statement = dataConnection.createStatement();

            // Read collections that aren't deleted
            ResultSet itemContainerResult = statement.executeQuery(
                    "SELECT * FROM ItemContainer c, Item i WHERE c.ItemID = i.ItemID" +
                            " AND i.StatusID != " + Status.Deleted.ordinal());
            while (itemContainerResult.next()) {
                GameItemRecord rec = new GameItemRecord(itemContainerResult, itemTypeRepo);
                GameItemContainer container = new GameItemContainer(rec, itemContainerResult.getString("Name"));
                containerMap.put(rec.itemID, container);
                itemMap.put(rec.itemID, container);
            }

            // Read non-collections (non-Frames)
            ResultSet itemResult = statement.executeQuery(
                    "SELECT * FROM Item i WHERE ItemTypeID >= 20 AND StatusID != " + Status.Deleted.ordinal());
            while (itemResult.next()) {
                GameItemRecord rec = new GameItemRecord(itemResult, itemTypeRepo);
                itemMap.put(rec.itemID, new GameItem(rec));
            }

            // Read mapping of collections to items that are inside them
            ResultSet mappingResult = statement.executeQuery("SELECT * FROM ItemContainer_Item"); //Non-frames
            while (mappingResult.next()) {
                ItemContainmentRecord rec = new ItemContainmentRecord(mappingResult);
                // not worrying about StartSlot, EndSlot, or Modifier right now...
                GameItemContainer container = getContainer(rec.container_itemID);
                GameItem item = getItem(rec.itemID);
                if (container != null && item != null) //can happen if these were deleted
                    container.addItem(item);
            }

            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot read static info!", e);
        }
    }

}
