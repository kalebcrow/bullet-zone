package edu.unh.cs.cs619.bulletzone.datalayer;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

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
        ArrayList<ItemType> types = new ArrayList<>();
        types.add(db.types.TankCannon);
        types.add(db.types.PlasmaCannon);
        types.add(db.types.ParticleBeamGun);
        types.add(db.types.TankGenerator);
        types.add(db.types.TruckGenerator);
        types.add(db.types.PortableGenerator);
        types.add(db.types.TankEngine);
        types.add(db.types.TruckEngine);
        types.add(db.types.BattleSuitPowerConverter);
        types.add(db.types.TankDriveTracks);
        types.add(db.types.TruckDriveTracks);
        types.add(db.types.BattleSuitLegAssists);
        types.add(db.types.GravAssist);
        types.add(db.types.FusionGenerator);
        types.add(db.types.DeflectorShield);
        types.add(db.types.AutomatedRepairKit);
        for(ItemType t : types) {
            GameItem item = db.items.create(t);
            assertThat(item, is(notNullValue()));
            assertThat(item.getType(), is(t));
        }
    }
}