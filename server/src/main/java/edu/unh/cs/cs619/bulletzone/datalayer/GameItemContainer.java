/**
 * Public class for accessing data for individual items in the ItemContainer table
 */

package edu.unh.cs.cs619.bulletzone.datalayer;

import java.util.Collection;
import java.util.Set;

public class GameItemContainer extends GameItem {
    protected String name;
    protected double capacity;
    protected double armor;
    protected Set<GameItem> containedItems;

    public GameItemContainer(GameItemRecord rec, String containerName) {
        super(rec);
        name = containerName;
        FrameType frameType = (FrameType)this.itemType;
        capacity = frameType.getCapacity();
        armor = frameType.getArmor();
    }

    public void addItem(GameItem child)
    {
        containedItems.add(child);
    }

    public void removeItem(GameItem child)
    {
        containedItems.remove(child);
    }

    public Collection<GameItem> getItems() { return containedItems;}
}
