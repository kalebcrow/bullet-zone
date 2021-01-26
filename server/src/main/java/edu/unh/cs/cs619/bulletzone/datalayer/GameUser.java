package edu.unh.cs.cs619.bulletzone.datalayer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GameUser {
    protected int userID;
    protected String name;
    protected String username;
    //password info not stored in game
    protected int statusID;
    protected HashSet<GameItemContainer> ownedItems = new HashSet<GameItemContainer>();
    protected HashSet<BankAccount> ownedAccounts = new HashSet<BankAccount>();

    public int getUserID() { return userID; }

    public String getName() { return name; }

    public String getUsername() { return username; }

    public Collection<GameItemContainer> getOwnedItems() { return ownedItems; }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------

    GameUser(GameUserRecord rec) {
        userID = rec.userID;
        name = rec.name;
        username = rec.username;
        statusID = rec.statusID;
    }

    void addPermissionTarget(PermissionTarget pt) {
        if (pt.getPermissionType() == PermissionTargetType.BankAccount)
            ownedAccounts.add((BankAccount)pt);
        else
            ownedItems.add((GameItemContainer)pt);
    }

    void removePermissionTarget(PermissionTarget pt) {
        if (pt.getPermissionType() == PermissionTargetType.BankAccount)
            ownedAccounts.remove((BankAccount)pt);
        else
            ownedItems.remove((GameItemContainer)pt);

    }
}
