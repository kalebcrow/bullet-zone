package edu.unh.cs.cs619.bulletzone.datalayer.permission;

import edu.unh.cs.cs619.bulletzone.datalayer.Entity;
import edu.unh.cs.cs619.bulletzone.datalayer.EntityRecord;
import edu.unh.cs.cs619.bulletzone.datalayer.GameUser;

public class OwnableEntity extends Entity {
    protected GameUser owner;

    public OwnableEntity(EntityRecord rec) {
        super(rec);
    }

    public void setOwner(GameUser user)  { owner = user; }
    public GameUser getOwner() { return owner; }
}
