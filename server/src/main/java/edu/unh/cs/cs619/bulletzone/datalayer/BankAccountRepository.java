package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;

public class BankAccountRepository implements OwnableEntityRepository {
    HashMap<Integer, BankAccount> accountMap = new HashMap<Integer, BankAccount>();
    BulletZoneData data;

    /**
     * Return the BankAccount associated with the passed internal ID
     * @param accountID    ID for the bank account being requested
     * @return  BankAccount corresponding to the passed accountID
     */
    public BankAccount getAccount(int accountID) { return accountMap.get(accountID); }
    public OwnableEntity getTarget(int id) { return getAccount(id); }

    /**
     * Return a collection of all bank accounts that are not deleted
     *
     * @return  Collection of all BankAccounts that do not have a "Deleted" status
     */
    public Collection<BankAccount> getAccounts() {
        return accountMap.values();
    }

    /**
     * Create a new empty BankAccount and insert it into the database and the appropriate
     * hashmap.
     * @return  BankAccount representation of the account that was inserted into the database.
     * @throws IllegalStateException for any database errors encountered.
     */
    public BankAccount create() {
        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return null;

        BankAccountRecord rec = new BankAccountRecord();
        BankAccount newAccount;
        try {
            // Create base item
            rec.insertInto(dataConnection);
            newAccount = new BankAccount(rec);
            accountMap.put(rec.entityID, newAccount);
            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error while creating bank account!", e);
        }
        System.out.println("New BankAccount added with ID " + rec.entityID);
        return newAccount;
    }

    /**
     * Deletes the referenced account from the in-memory representation and
     * marks it as deleted in the database.
     * @param account    BankAccount to be marked as deleted
     * @return  true if the operation was successful, and false otherwise.
     */
    public boolean delete(BankAccount account) {
        return delete(account.entityID);
    }

    /**
     * Deletes the referenced account from the in-memory representation and
     * marks it as deleted in the database.
     * NOTE: this method does not remove the item from its container in the in-memory representation.
     * @param accountID    ID of the account to be marked as deleted
     * @return  true if the operation was successful, and false otherwise.
     */
    public boolean delete(int accountID) {
        if (!accountMap.containsKey(accountID))
            return false;
        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return false;

        try {
            if (!EntityRecord.markDeleted(accountID, dataConnection)) {
                dataConnection.close();
                return false; //nothing deleted
            }
            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error while deleting item.", e);
        }

        accountMap.remove(accountID);
        return true;
    }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------

    /**
     * Reads the database and fills the HashMaps as appropriate. Intended to be called once
     * at time of initialization.
     *
     * @param bzData        reference to BulletZoneData class to use for SQL queries
    */
    void refresh(BulletZoneData bzData) {
        data = bzData;
        accountMap.clear();
        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return;
        try {
            Statement statement = dataConnection.createStatement();

            // Read accounts that aren't deleted
            ResultSet itemResult = statement.executeQuery(
                    "SELECT * FROM BankAccount a, Entity e WHERE a.entityID = e.entityID AND e.StatusID != " + Status.Deleted.ordinal());
            while (itemResult.next()) {
                BankAccountRecord rec = new BankAccountRecord(itemResult);
                accountMap.put(rec.entityID, new BankAccount(rec));
            }

            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot read static info!", e);
        }
    }

}
