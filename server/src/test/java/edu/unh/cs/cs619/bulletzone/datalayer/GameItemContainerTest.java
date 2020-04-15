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

}