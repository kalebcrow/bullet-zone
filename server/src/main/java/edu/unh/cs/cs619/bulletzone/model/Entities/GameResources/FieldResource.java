package edu.unh.cs.cs619.bulletzone.model.Entities.GameResources;

import java.util.concurrent.ConcurrentMap;

import edu.unh.cs.cs619.bulletzone.events.DestroyResourceEvent;
import edu.unh.cs.cs619.bulletzone.events.EventManager;
import edu.unh.cs.cs619.bulletzone.model.Entities.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Miscellaneous.FieldHolder;

public abstract class FieldResource extends FieldEntity {
    //protected static final EventBus eventBus = new EventBus();
    protected FieldHolder parent;
    private static ConcurrentMap<Integer, FieldResource> itemsOnGrid;
    public static void setItemsOnGrid(ConcurrentMap<Integer, FieldResource> i){
        itemsOnGrid = i;
    }
    private EventManager eventManager = EventManager.getInstance();

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

    /*public static final void registerEventBusListener(Object listener) {
        checkNotNull(listener);
        eventBus.register(listener);
    }

    public static final void unregisterEventBusListener(Object listener) {
        checkNotNull(listener);
        eventBus.unregister(listener);
    }*/
}
