/**
 * Internal package structure for representing data coming out of a SQL query on the Item table
 */
package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import edu.unh.cs.cs619.bulletzone.datalayer.itemType.ItemType;
import edu.unh.cs.cs619.bulletzone.datalayer.itemType.ItemTypeRepository;

class GameItemRecord {
    int itemID;
    ItemType itemType;
    double usageMonitor;
    int statusID;
    Timestamp created;
    Timestamp deleted;

    GameItemRecord(ItemType giItemType) {
        itemType = giItemType;
        usageMonitor = 0;
        statusID = Status.Active.ordinal();
        Date date = new Date();
        created = new Timestamp(date.getTime());
    }

    GameItemRecord(ResultSet itemResult, ItemTypeRepository itemTypeRepo){
        try {
            itemID = itemResult.getInt("ItemID");
            itemType = itemTypeRepo.getType(itemResult.getInt("ItemTypeID"));
            usageMonitor = itemResult.getDouble("UsageMonitor");
            statusID = itemResult.getInt("StatusID");
            created = itemResult.getTimestamp("Created");
            deleted = itemResult.getTimestamp("Deleted");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to extract data from item result set", e);
        }
    }

    String getInsertString() {
        return " INSERT INTO Item ( ItemTypeID, UsageMonitor, StatusID, Created )\n" +
                "    VALUES (" + itemType.getID() + ", "
                + usageMonitor + ", "
                + statusID + ", '"
                + created + "'); ";
    }
}
