package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

class GameItemRepository {
    HashMap<Integer, GameItem> itemMap = new HashMap<Integer, GameItem>();
    HashMap<Integer, GameItemContainer> containerMap = new HashMap<Integer, GameItemContainer>();
    Connection dataConnection;

    GameItem create(ItemType itemType) {
        GameItemRecord rec = new GameItemRecord();
        rec.itemType = itemType;
        rec.usageMonitor = 0;
        rec.statusID = Status.Active.ordinal();
        String name = "[Unnamed]";
        GameItem newItem;
        try {
            // Create base item
            PreparedStatement insertStatement = dataConnection.prepareStatement(
                    " INSERT INTO Item ( ItemTypeID, UsageMonitor, StatusID )\n" +
                            "    VALUES (" + rec.itemType.getID() + ", " +
                                           + rec.usageMonitor + ", " +
                                           + rec.statusID + "); ", Statement.RETURN_GENERATED_KEYS);
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

    void refresh(Connection sqlDataConnection, ItemTypeRepository itemTypeRepo) {
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
                GameItemContainer container = containerMap.get(containerID);
                GameItem item = itemMap.get(itemID);
                container.addItem(item);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot read static info!", e);
        }
    }

    private GameItemRecord makeItemRecordFromResultSet(ResultSet itemResult, ItemTypeRepository itemTypeRepo) {
        GameItemRecord rec = new GameItemRecord();
        try {
            rec.itemID = itemResult.getInt("i.ItemID");
            rec.itemType = itemTypeRepo.typeMap.get(itemResult.getInt("i.ItemTypeID"));
            rec.usageMonitor = itemResult.getDouble("i.UsageMonitor");
            rec.statusID = itemResult.getInt("i.StatusID");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to extract data fom item result set", e);
        }
        return rec;
    }

}
