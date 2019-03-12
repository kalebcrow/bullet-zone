package edu.unh.cs.cs619.bulletzone.datalayer;

import java.util.Set;

public class GameItemContainer extends GameItem {
    protected double capacity;
    protected double armor;
    protected Set<GameItem> containedItems;

    public GameItemContainer(double maxCapacity, double currentArmor)
    {
        capacity = maxCapacity;
        armor = currentArmor;
    }

    public void addItem(GameItem child)
    {
        containedItems.add(child);
    }

    public void removeItem(GameItem child)
    {
        containedItems.remove(child);
    }
}
