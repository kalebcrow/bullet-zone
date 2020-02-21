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
    //protected Set<BankAccount> ownedAccounts;

    int getUserID() { return userID; }

    String getName() { return name; }

    String getUsername() { return username; }

    Collection<GameItemContainer> getOwnedItems() { return ownedItems; }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------

    GameUser(GameUserRecord rec) {
        userID = rec.userID;
        name = rec.name;
        username = rec.username;
        statusID = rec.statusID;
    }

    void addItem(GameItemContainer item) { ownedItems.add(item); }

    void removeItem(GameItemContainer item) { ownedItems.remove(item); }

    //void addAccount(BankAccount account) { ownedAccounts.add(account); }
}
