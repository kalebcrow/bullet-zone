/**
 * Internal package structure for representing data coming out of a SQL query on the ItemType table
 */
package edu.unh.cs.cs619.bulletzone.datalayer;

class ItemTypeRecord {
    public int itemTypeID;
    public String name;
    public ItemCategory category;
    public double val[];
}
