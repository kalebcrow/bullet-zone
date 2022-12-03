package edu.unh.cs.cs619.bulletzone.model.Entities;

import static com.google.common.base.Preconditions.checkNotNull;

import edu.unh.cs.cs619.bulletzone.model.Entities.Tanks.Tank;
import edu.unh.cs.cs619.bulletzone.model.Miscellaneous.FieldHolder;

public abstract class FieldEntity {
    //protected static final EventBus eventBus = new EventBus();
    protected FieldHolder parent;

    /**
     * Serializes the current {@link FieldEntity} instance.
     *
     * @return Integer representation of the current {@link FieldEntity}
     */
    public abstract int getIntValue();

    public FieldHolder getParent() {
        return parent;
    }

    public void setParent(FieldHolder parent) {
        this.parent = parent;
    }

    public abstract FieldEntity copy();

    public void hit(int damage) {
    }

    public boolean gather(Tank tank){
        return false;
    }

    public int getLife(){return 0;}

    /*public static final void registerEventBusListener(Object listener) {
        checkNotNull(listener);
        eventBus.register(listener);
    }

    public static final void unregisterEventBusListener(Object listener) {
        checkNotNull(listener);
        eventBus.unregister(listener);
    }*/
}
