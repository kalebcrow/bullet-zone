package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Timestamp;
import java.util.Date;

class ItemPermissionRecord extends PermissionTargetRecord {

    ItemPermissionRecord(int ipItemID, int ipUserID, Permission p) {
       super(ipItemID, ipUserID, p);
    }

    String getInsertString() {
        return " INSERT INTO ItemContainer_User_Permissions ( ItemID, UserID, PermissionID, StatusID, Created )\n" +
                "    VALUES (" + targetID + ", "
                + userID + ", "
                + permissionID + ", "
                + statusID + ", '"
                + created + "'); ";
    }
}