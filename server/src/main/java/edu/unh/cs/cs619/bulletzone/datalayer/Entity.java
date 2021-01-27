package edu.unh.cs.cs619.bulletzone.datalayer;

class Entity {
    protected int entityID;
    protected int statusID;
    protected final EntityType entityType;

    Entity(EntityType et) { entityType = et; }

    Entity(EntityRecord rec) {
        entityType = rec.entityType;
        entityID = rec.entityID;
        statusID = rec.statusID;
    }

    EntityType getEntityType() { return entityType; }

    int getId() { return entityID; }

    @Override
    public String toString() { return entityType.name() + " (ID: " + entityID + ")"; }

}
