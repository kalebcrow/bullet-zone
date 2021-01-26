package edu.unh.cs.cs619.bulletzone.datalayer;

import edu.unh.cs.cs619.bulletzone.datalayer.itemType.ItemProperty;
import edu.unh.cs.cs619.bulletzone.datalayer.itemType.ItemType;

public class BankAccount implements PermissionTarget {
    protected int accountID;
    protected double credits;
    protected int statusID;
    protected GameUser owner;

    @Override
    public PermissionTargetType getPermissionType() {
        return PermissionTargetType.BankAccount;
    }

    @Override
    public int getId() {
        return getAccountID();
    }

    @Override
    public String toString() { return "Bank Account (ID: " + accountID + ")"; }

    public int getAccountID() { return accountID; }

    public GameUser getOwner() { return owner; }

    public double getCredits() { return credits; }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------

    BankAccount(BankAccountRecord rec) {
        accountID = rec.bankAccountID;
        credits = rec.credits;
        statusID = rec.statusID;
    }

    void setOwner(GameUser user) { owner = user; }

    /**
     * DON'T USE THIS METHOD IN YOUR CODE. Intended-to-be package-local method for setting
     * the owner (but doesn't update the database with that information). This method is
     * public so that the interface works properly.
     * @param user
     */
    public void setOwningUser(GameUser user) { setOwner(user); }

    /**
     * DON'T USE THIS METHOD IN YOUR CODE. Intended-to-be package-local method for getting
     * the owner (but doesn't check the database with that information). This method is
     * public so that the interface works properly.
     * @return the user that owns this container
     */
    public GameUser getOwningUser() { return getOwner(); }

}
