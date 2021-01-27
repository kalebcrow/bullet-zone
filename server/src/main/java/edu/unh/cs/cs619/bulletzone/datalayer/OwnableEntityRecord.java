package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Timestamp;
import java.util.Date;

public class OwnableEntityRecord extends EntityRecord {
    int userID;
    int permissionID;

    OwnableEntityRecord(int pTargetID, int pUserID, Permission p) {
        super(EntityType.Invalid);
        entityID = pTargetID;
        userID = pUserID;
        permissionID = p.ordinal();
    }

    String getInsertString() {
        return " INSERT INTO Entity_User_Permissions ( EntityID, User_EntityID, PermissionID, StatusID, Created )\n" +
                "    VALUES (" + entityID + ", "
                + userID + ", "
                + permissionID + ", "
                + statusID + ", '"
                + created + "'); ";
    }
}
