package edu.unh.cs.cs619.bulletzone.datalayer;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class BulletZoneDataTest {

    static BulletZoneData db;

    @BeforeClass
    static public void setup() {
        db = new BulletZoneData();
        db.rebuildData();
    }

    @Test
    public void users$createUser_userNotPresent_returnsAppropriateGameUser()
    {
        String name = "Test User 1";
        String username = "testuser1";
        String password = "testPass1";
        GameUser user = db.users.createUser(name, username, password);
        assertThat(user, is(notNullValue()));
        assertThat(user.getName(), is(name));
        assertThat(user.getUsername(), is (username));
    }

    @Test
    public void users$validateLogin_userPresent_returnsAppropriateGameUser()
    {
        String name = "Test User 2";
        String username = "testuser2";
        String password = "testPass2";
        GameUser user = db.users.createUser(name, username, password);
        GameUser user2 = db.users.validateLogin(username, password);
        assertThat(user2, is(notNullValue()));
        assertThat(user2.getName(), is(name));
        assertThat(user2.getUsername(), is (username));
    }

    @Test
    public void items$createContainer_hardCodedContainerTypeGiven_returnsAppropriateContainer()
    {
        ArrayList<ItemType> types = new ArrayList<>();
        types.add(db.types.GarageBay);
        types.add(db.types.StorageContainer);
        types.add(db.types.TankFrame);
        types.add(db.types.TruckFrame);
        types.add(db.types.BattleSuit);
        types.add(db.types.ShipFrame);
        types.add(db.types.VehicleExpansionFrame);
        types.add(db.types.BattleSuitExpansionFrame);
        for(ItemType t : types) {
            GameItemContainer item = db.items.createContainer(t);
            assertThat(item, is(notNullValue()));
            assertThat(item.getType(), is(t));
        }
    }

    @Test
    public void items$create_hardCodedItemTypeGiven_returnsAppropriateItem()
    {
        HashMap<ItemType, ItemCategory> typeCategories = new HashMap<>();
        typeCategories.put(db.types.TankCannon, db.categories.Weapon);
        typeCategories.put(db.types.PlasmaCannon, db.categories.Weapon);
        typeCategories.put(db.types.ParticleBeamGun, db.categories.Weapon);
        typeCategories.put(db.types.LargePlasmaCannon, db.categories.Weapon);
        typeCategories.put(db.types.TankGenerator, db.categories.Generator);
        typeCategories.put(db.types.TruckGenerator, db.categories.Generator);
        typeCategories.put(db.types.PortableGenerator, db.categories.Generator);
        typeCategories.put(db.types.ShipGenerator, db.categories.Generator);
        typeCategories.put(db.types.TankEngine, db.categories.Engine);
        typeCategories.put(db.types.TruckEngine, db.categories.Engine);
        typeCategories.put(db.types.BattleSuitPowerConverter, db.categories.Engine);
        typeCategories.put(db.types.ShipEngine, db.categories.Engine);
        typeCategories.put(db.types.TankDriveTracks, db.categories.Drive);
        typeCategories.put(db.types.TruckDriveTracks, db.categories.Drive);
        typeCategories.put(db.types.BattleSuitLegAssists, db.categories.Drive);
        typeCategories.put(db.types.ShipDriveImpellers, db.categories.Drive);
        typeCategories.put(db.types.GravAssist, db.categories.AntiGravity);
        typeCategories.put(db.types.FusionGenerator, db.categories.WeaponBoostingGenerator);
        typeCategories.put(db.types.DeflectorShield, db.categories.ShieldRepair);
        typeCategories.put(db.types.AutomatedRepairKit, db.categories.ShieldRepair);
        for(ItemType t : typeCategories.keySet()) {
            GameItem item = db.items.create(t);
            assertThat(item, is(notNullValue()));
            ItemType it = item.getType();
            assertThat(it, is(t));
            assertThat(it.getCategory(), is(typeCategories.get(t)));
        }
    }
}