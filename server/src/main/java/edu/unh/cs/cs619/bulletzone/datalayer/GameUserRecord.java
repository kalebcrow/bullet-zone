package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Timestamp;

class GameUserRecord {
    public int userID;
    public String name;
    public String username;
    public byte[] passwordHash;
    public byte[] passwordSalt;
    public int statusID;
    public Timestamp created;
    public Timestamp deleted;
}
