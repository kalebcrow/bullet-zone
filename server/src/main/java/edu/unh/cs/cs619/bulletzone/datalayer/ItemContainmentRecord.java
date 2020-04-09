package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.ResultSet;
import java.sql.SQLException;

class ItemContainmentRecord {
    int container_itemID;
    int itemID;
    int startSlot;
    int endSlot;
    int modifier;

    ItemContainmentRecord(int icItemID, int icContainerID) {
        itemID = icItemID;
        container_itemID = icContainerID;
    }

    ItemContainmentRecord(ResultSet mappingResult) {
        try {
            container_itemID = mappingResult.getInt("Container_ItemID");
            itemID = mappingResult.getInt("ItemID");
            startSlot = mappingResult.getInt("startSlot");
            endSlot = mappingResult.getInt("endSlot");
            modifier = mappingResult.getInt("modifier");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to extract data from item containment result set", e);
        }
    }

    String getInsertString() {
        return " INSERT INTO ItemContainer_Item ( Container_ItemID, ItemID, StartSlot, EndSlot, Modifier )\n" +
                "    VALUES (" + container_itemID + ", "
                + itemID + ", "
                + startSlot + ", "
                + endSlot + ", "
                + modifier + "); ";
    }
}
