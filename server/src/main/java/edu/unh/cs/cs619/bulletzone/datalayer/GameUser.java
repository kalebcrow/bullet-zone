package edu.unh.cs.cs619.bulletzone.datalayer;

import java.util.Set;

public class GameUser {
    protected int userID;
    protected String name;
    protected String username;
    //password info not stored in game
    protected int statusID;
    protected Set<GameItemContainer> ownedItems;
    //protected Set<BankAccount> ownedAccounts;

    GameUser(GameUserRecord rec) {
        userID = rec.userID;
        name = rec.name;
        statusID = rec.statusID;
    }

    void addItem(GameItemContainer item) { ownedItems.add(item); }

    //void addAccount(BankAccount account) { ownedAccounts.add(account); }
}
