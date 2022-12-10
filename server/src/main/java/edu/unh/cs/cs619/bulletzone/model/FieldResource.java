package edu.unh.cs.cs619.bulletzone.model;

import java.util.concurrent.ConcurrentMap;

import edu.unh.cs.cs619.bulletzone.events.DestroyResourceEvent;
import edu.unh.cs.cs619.bulletzone.events.EventManager;

public abstract class FieldResource extends FieldEntity {
    //protected static final EventBus eventBus = new EventBus();
    protected FieldHolder parent;
    private static ConcurrentMap<Integer, FieldResource> itemsOnGrid;
    public static void setItemsOnGrid(ConcurrentMap<Integer, FieldResource> i){
        itemsOnGrid = i;
    }
    protected static Game game;
    public static void setGame(Game g){game = g;}
    protected EventManager eventManager = EventManager.getInstance();

    /**
     * Serializes the current {@link FieldResource} instance.
     *
     * @return Integer representation of the current {@link FieldResource}
     */
    public abstract int getIntValue();

    public FieldHolder getParent() {
        return parent;
    }

    public void setParent(FieldHolder parent) {
        this.parent = parent;
    }

    public abstract FieldResource copy();

    public void hit(int damage) {
        parent.clearField();
        itemsOnGrid.remove(parent.getPos());
        eventManager.addEvent(new DestroyResourceEvent(parent.getPos(),""));
    }

    @Override
    public abstract boolean gather(Tank tank);

}
