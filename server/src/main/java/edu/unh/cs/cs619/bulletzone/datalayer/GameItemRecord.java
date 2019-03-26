/**
 * Internal package structure for representing data coming out of a SQL query on the Item table
 */
package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Timestamp;

class GameItemRecord {
    public int itemID;
    public ItemType itemType;
    public double usageMonitor;
    public int statusID;
    public Timestamp created;
    public Timestamp deleted;

}
