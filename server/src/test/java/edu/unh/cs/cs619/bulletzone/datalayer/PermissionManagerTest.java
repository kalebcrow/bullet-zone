package edu.unh.cs.cs619.bulletzone.datalayer;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class PermissionManagerTest {

    static BulletZoneData db;
    static GameUser basicUser;

    @BeforeClass
    static public void setup() {
        db = new BulletZoneData();
        db.rebuildData();
        basicUser = db.users.createUser("BasicUser", "BasicUser", "password");
    }

    @Test
    public void setOwner_onValidItem_updatesInternalStructures() {
        GameItemContainer tank = db.items.createContainer(db.types.TankFrame);
        db.permissions.setOwner(tank, basicUser);
        assertThat(basicUser.getOwnedItems().contains(tank), is(true));

        db.permissions.removeOwner(tank);
    }

    @Test
    public void removeOwner_onValidItem_updatesInternalStructures() {
        GameItemContainer tank = db.items.createContainer(db.types.TankFrame);
        db.permissions.setOwner(tank, basicUser);

        db.permissions.removeOwner(tank);
        assertThat(basicUser.getOwnedItems().contains(tank), is(false));
        assertThat(db.permissions.getUserPermissions(basicUser).getItems().size(), is(0));
    }

    @Test
    public void grant_OnValidPermission_updatesInternalStructures() {
        GameItemContainer tank = db.items.createContainer(db.types.TankFrame);
        db.permissions.grant(tank, basicUser, Permission.Add);
        assertThat(db.permissions.check(tank, basicUser, Permission.Add), is(true));
        db.permissions.revoke(tank, basicUser, Permission.Add);

    }

    @Test
    public void revoke_OnValidPermission_updatesInternalStructures() {
        GameItemContainer tank = db.items.createContainer(db.types.TankFrame);
        db.permissions.grant(tank, basicUser, Permission.Add);

        db.permissions.revoke(tank, basicUser, Permission.Add);
        assertThat(db.permissions.check(tank, basicUser, Permission.Add), is(false));

        assertThat(db.permissions.getUserPermissions(basicUser).getItems().size(), is(0));
    }
}