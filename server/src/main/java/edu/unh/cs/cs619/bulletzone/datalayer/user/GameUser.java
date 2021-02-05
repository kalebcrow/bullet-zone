package edu.unh.cs.cs619.bulletzone.datalayer.user;

import java.util.Collection;
import java.util.HashSet;

import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccount;
import edu.unh.cs.cs619.bulletzone.datalayer.core.Entity;
import edu.unh.cs.cs619.bulletzone.datalayer.core.EntityType;
import edu.unh.cs.cs619.bulletzone.datalayer.item.GameItemContainer;

public class GameUser extends Entity {
    protected String name;
    protected String username;
    //password info not stored in game
    protected HashSet<GameItemContainer> ownedItems = new HashSet<GameItemContainer>();
    protected HashSet<BankAccount> ownedAccounts = new HashSet<BankAccount>();

    public String getName() { return name; }

    public String getUsername() { return username; }

    public Collection<GameItemContainer> getOwnedItems() { return ownedItems; }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------

    GameUser(GameUserRecord rec) {
        super(rec);
        name = rec.name;
        username = rec.username;
    }

    void addPermissionTarget(Entity pt) {
        if (pt.getEntityType() == EntityType.BankAccount)
            ownedAccounts.add((BankAccount)pt);
        else
            ownedItems.add((GameItemContainer)pt);
    }

    void removePermissionTarget(Entity pt) {
        if (pt.getEntityType() == EntityType.BankAccount)
            ownedAccounts.remove((BankAccount)pt);
        else
            ownedItems.remove((GameItemContainer)pt);

    }
}
