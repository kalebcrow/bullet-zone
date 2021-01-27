package edu.unh.cs.cs619.bulletzone.datalayer;

public class OwnableEntity extends Entity {
    protected GameUser owner;

    OwnableEntity(EntityRecord rec) {
        super(rec);
    }

    void setOwner(GameUser user)  { owner = user; }
    GameUser getOwner() { return owner; }
}
