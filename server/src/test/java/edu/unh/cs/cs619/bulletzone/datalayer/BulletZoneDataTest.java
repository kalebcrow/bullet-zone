package edu.unh.cs.cs619.bulletzone.datalayer;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
}