package edu.unh.cs.cs619.bulletzone.datalayer;

public class GameItem {
    protected int itemID;
    protected ItemType itemType;

    String getTypeName(){
        return itemType.niceName();
    }
}
