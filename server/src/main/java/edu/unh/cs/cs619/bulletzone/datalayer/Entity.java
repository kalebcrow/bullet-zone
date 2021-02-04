package edu.unh.cs.cs619.bulletzone.datalayer;

public class Entity {
    protected int entityID;
    protected int statusID;
    protected final EntityType entityType;

    public Entity(EntityType et) { entityType = et; }

    public Entity(EntityRecord rec) {
        entityType = rec.entityType;
        entityID = rec.entityID;
        statusID = rec.statusID;
    }

    public EntityType getEntityType() { return entityType; }

    public int getId() { return entityID; }

    @Override
    public String toString() { return entityType.name() + " (ID: " + entityID + ")"; }

}
