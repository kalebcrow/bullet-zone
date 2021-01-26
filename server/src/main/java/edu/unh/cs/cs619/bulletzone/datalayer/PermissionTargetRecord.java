package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Timestamp;
import java.util.Date;

public class PermissionTargetRecord {
    int targetID;
    int userID;
    int permissionID;
    int statusID;
    public Timestamp created;
    public Timestamp deleted;

    PermissionTargetRecord(int pTargetID, int pUserID, Permission p) {
        targetID = pTargetID;
        userID = pUserID;
        permissionID = p.ordinal();
        statusID = Status.Active.ordinal();
        Date date = new Date();
        created = new Timestamp(date.getTime());
    }
}
