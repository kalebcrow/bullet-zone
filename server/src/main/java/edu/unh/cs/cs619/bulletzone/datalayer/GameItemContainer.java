/**
 * Public class for accessing data for individual items in the ItemContainer table
 */

package edu.unh.cs.cs619.bulletzone.datalayer;

import java.util.Collection;
import java.util.HashSet;

public class GameItemContainer extends GameItem {
    protected String name;
    protected double capacity;
    protected double armor;
    protected HashSet<GameItem> containedItems = new HashSet<>();

    public GameItemContainer(GameItemRecord rec, String containerName) {
        super(rec);
        name = containerName;
        FrameType frameType = (FrameType)this.itemType;
        capacity = frameType.getCapacity();
        armor = frameType.getArmor();
    }

    public Collection<GameItem> getItems() { return containedItems;}

    /**
     * Package-local version that adds an item to the collection without updating the database
     * @param child Item to be added to the collection
     */
    void addItem(GameItem child)
    {
        containedItems.add(child);
    }

    /**
     * Package-local version that removes an item from the collection without updating the database
     * @param child Item to be added to the collection
     */
    void removeItem(GameItem child)
    {
        containedItems.remove(child);
    }

    /**
     * Package-local version that removes all items from the collection without updating the database
     */
    void removeAll()
    {
        containedItems.clear();
    }

}
