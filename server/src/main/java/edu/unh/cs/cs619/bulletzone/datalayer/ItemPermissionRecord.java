package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Timestamp;
import java.util.Date;

class ItemPermissionRecord {
    int itemID;
    int userID;
    int permissionID;
    int statusID;
    public Timestamp created;
    public Timestamp deleted;

    ItemPermissionRecord(int ipItemID, int ipUserID, Permission p) {
        itemID = ipItemID;
        userID = ipUserID;
        permissionID = p.ordinal();
        statusID = Status.Active.ordinal();
        Date date = new Date();
        created = new Timestamp(date.getTime());
    }

    String getInsertString() {
        return " INSERT INTO ItemContainer_User_Permissions ( ItemID, UserID, PermissionID, StatusID, Created )\n" +
                "    VALUES (" + itemID + ", "
                + userID + ", "
                + permissionID + ", "
                + statusID + ", '"
                + created + "'); ";
    }
}