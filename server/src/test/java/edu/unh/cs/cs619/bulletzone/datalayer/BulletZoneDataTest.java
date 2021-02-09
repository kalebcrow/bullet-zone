package edu.unh.cs.cs619.bulletzone.datalayer;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.datalayer.item.GameItem;
import edu.unh.cs.cs619.bulletzone.datalayer.item.GameItemContainer;
import edu.unh.cs.cs619.bulletzone.datalayer.itemType.ItemCategory;
import edu.unh.cs.cs619.bulletzone.datalayer.itemType.ItemType;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;

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
        types.add(db.types.TransportFrame);
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
        typeCategories.put(db.types.Trifle, db.categories.Artifact);
        typeCategories.put(db.types.Trinket, db.categories.Artifact);
        typeCategories.put(db.types.Thingamajig, db.categories.Artifact);
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

    @Test
    public void items$delete_newlyCreatedItem_removesItemFromRepository() {
        GameItem item = db.items.create(db.types.TankCannon);
        db.items.delete(item);
        assertThat(db.items.getItem(item.getId()), is(nullValue()));
    }

    @Test
    public void items$delete_newlyCreatedItemContainer_removesContainerFromRepository() {
        GameItemContainer item = db.items.createContainer(db.types.TankFrame);
        db.items.delete(item);
        assertThat(db.items.getItemOrContainer(item.getId()), is(nullValue()));
    }

    @Test
    public void items$renameContainer_validNameGiven_PersistsInAnotherInstance() {
        String name = "Freddie";
        GameItemContainer truck = db.items.createContainer(db.types.TruckFrame);
        db.items.renameContainer(truck, name);
        db.refresh();
        GameItemContainer check = db.items.getContainer(truck.getId());
        assertThat(check.getName(), is(name));
    }

    @Test
    public void items$renameContainer_invalidNameGiven_Fails() {
        String name = "'injection attack!";
        GameItemContainer truck = db.items.createContainer(db.types.TruckFrame);
        assertThat(db.items.renameContainer(truck, name), is(false));
    }

    @Test
    public void items$artifactWithNameAndValue_AfterCreated_PersistsInAnotherInstance(){
        String name = "Coupon for a tank frame";
        double value = 350.0;
        GameItem artifact = db.items.create(db.types.Trifle, name, value);
        db.refresh();
        GameItem check = db.items.getItem(artifact.getId());
        assertThat(check.getOriginalName(), is(name));
        assertThat(check.getPrice(), is(value));
    }

    @Test
    public void BulletZoneData_ItemsCreatedInOneInstance_PersistInAnotherInstance() {
        GameUser user1 = db.users.createUser("PersistTest", "PersistTest", "password");
        GameItemContainer tank = db.items.createContainer(db.types.TankFrame);
        final int numItems = 5;
        ItemType types[] = {db.types.TankCannon, db.types.TankEngine, db.types.TankDriveTracks, db.types.TankGenerator, db.types.DeflectorShield};
        GameItem items[] = new GameItem[types.length];
        ArrayList<Integer> itemIDs = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            items[i] = db.items.create(types[i]);
            itemIDs.add(items[i].getId());
            db.items.addItemToContainer(items[i], tank);
        }
        db.permissions.setOwner(tank, user1);

        db.refresh();
        GameUser user2 = db.users.getUser("PersistTest");
        System.out.println("Found user " + user2);
        for (GameItemContainer c : user2.getOwnedItems()) {
            System.out.println("Found container " + c);
            assertThat(c.getId(), is(tank.getId()));
            int i = 0;
            for (GameItem item : c.getItems()) {
                System.out.println("Found item " + item);
                assertThat(itemIDs, hasItem(item.getId()));
            }
        }
    }

}