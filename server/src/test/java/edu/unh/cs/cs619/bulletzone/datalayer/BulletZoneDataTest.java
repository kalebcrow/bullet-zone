package edu.unh.cs.cs619.bulletzone.datalayer;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

public class BulletZoneDataTest {

    @Test
    public void items_createUser_validatesLogin()
    {
        BulletZoneData d = new BulletZoneData();
        d.rebuildData();
        GameUser user = d.users.createUser("Test User", "testuser", "testPass");
        GameUser user2 = d.users.validateLogin("testuser", "testPass");
        assertThat(user2, is(notNullValue()));
    }
}