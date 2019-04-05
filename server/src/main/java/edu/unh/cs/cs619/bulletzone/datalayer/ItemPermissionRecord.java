package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Timestamp;

class ItemPermissionRecord {
    public int itemID;
    public int userID;
    public int permissionID;
    public int statusID;
    public Timestamp created;
    public Timestamp deleted;
}
