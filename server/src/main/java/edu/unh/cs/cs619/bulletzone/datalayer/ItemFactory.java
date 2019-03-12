package edu.unh.cs.cs619.bulletzone.datalayer;

public class ItemFactory {

    public GameItem createItem(ItemType itemType) {
        GameItem newItem = null;
        if (itemType.isContainer())
            newItem = new GameItemContainer(5, 5);

        return newItem;
    }
}
