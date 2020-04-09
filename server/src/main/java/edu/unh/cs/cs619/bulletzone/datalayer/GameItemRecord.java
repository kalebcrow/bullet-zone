/**
 * Internal package structure for representing data coming out of a SQL query on the Item table
 */
package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

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
            itemID = itemResult.getInt("i.ItemID");
            itemType = itemTypeRepo.typeMap.get(itemResult.getInt("i.ItemTypeID"));
            usageMonitor = itemResult.getDouble("i.UsageMonitor");
            statusID = itemResult.getInt("i.StatusID");
            created = itemResult.getTimestamp("i.Created");
            deleted = itemResult.getTimestamp("i.Deleted");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to extract data fom item result set", e);
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
