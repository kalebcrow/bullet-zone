package edu.unh.cs.cs619.bulletzone.datalayer;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class GameItemContainerTest {

    static BulletZoneData db;

    @BeforeClass
    static public void setup() {
        db = new BulletZoneData();
        db.rebuildData();
    }

    @Test
    public void getProperty_capacityWhenContainsContainers_returnsOverallCapactiy() {
        double totalCapacity = 0.0;
        final ItemProperty Capacity = db.properties.Capacity;
        GameItemContainer garage = db.items.createContainer(db.types.GarageBay);
        totalCapacity += garage.getProperty(Capacity);
        for (int i = 0; i < 3; i++)
        {
            GameItemContainer tank = db.items.createContainer(db.types.TankFrame);
            totalCapacity += tank.getProperty(Capacity);
            db.items.addItemToContainer(tank, garage);
        }
        assertThat(garage.getProperty(db.properties.Capacity), is(totalCapacity));
    }

    @Test
    public void getLocalProperty_capacityWhenContainsContainers_returnsOnlyContainerCapactiy() {
        final ItemProperty Capacity = db.properties.Capacity;
        GameItemContainer garage = db.items.createContainer(db.types.GarageBay);
        double garageCapacity = garage.getProperty(Capacity);
        for (int i = 0; i < 3; i++)
        {
            GameItemContainer tank = db.items.createContainer(db.types.TankFrame);
            db.items.addItemToContainer(tank, garage);
        }
        assertThat(garage.getLocalProperty(db.properties.Capacity), is(garageCapacity));
    }

    @Test
    public void getProperty_SizeWhenContainsContainers_returnsAggregateSize() {
        final ItemProperty Size = db.properties.Size;
        GameItemContainer tank = db.items.createContainer(db.types.TankFrame);
        double tankSize = tank.getSize();
        for (int i = 0; i < 3; i++)
        {
            GameItemContainer expand = db.items.createContainer(db.types.VehicleExpansionFrame);
            db.items.addItemToContainer(expand, tank);
            tankSize += expand.getSize();
            GameItem weapon = db.items.create(db.types.TankCannon);
            db.items.addItemToContainer(weapon, expand);
            tankSize += weapon.getSize();
        }
        assertThat(tank.getProperty(db.properties.Size), is(tankSize));
    }

    @Test
    public void getProperty_WeightModifierWhenContainsGravAssist_returnsAggregateWeightModifier() {
        final ItemProperty prop = db.properties.WeightModifier;
        GameItemContainer tank = db.items.createContainer(db.types.TankFrame);
        double propVal = tank.getProperty(prop);
        for (int i = 0; i < 3; i++)
        {
            GameItem add = db.items.create(db.types.GravAssist);
            db.items.addItemToContainer(add, tank);
            propVal *= add.getProperty(prop);
        }
        assertThat(tank.getProperty(prop), is(propVal));
    }

}