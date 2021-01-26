package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

public class BankAccountRepository implements PermissionTargetRepository {
    HashMap<Integer, BankAccount> accountMap = new HashMap<Integer, BankAccount>();
    BulletZoneData data;

    /**
     * Return the BankAccount associated with the passed internal ID
     * @param accountID    ID for the bank account being requested
     * @return  BankAccount corresponding to the passed accountID
     */
    public BankAccount getAccount(int accountID) { return accountMap.get(accountID); }
    public PermissionTarget getTarget(int id) { return getAccount(id); }

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
            PreparedStatement insertStatement = dataConnection.prepareStatement(
                    rec.getInsertString(), Statement.RETURN_GENERATED_KEYS);
            int affectedRows = insertStatement.executeUpdate();
            if (affectedRows == 0)
                throw new SQLException("Creating BankAccount failed.");

            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                rec.bankAccountID = generatedKeys.getInt(1);
            }
            else {
                throw new SQLException("Created BankAccount but failed to obtain ID.");
            }

            newAccount = new BankAccount(rec);
            accountMap.put(rec.bankAccountID, newAccount);
            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error while creating item!", e);
        }
        System.out.println("New BankAccount added with ID " + rec.bankAccountID);
        return newAccount;
    }

    /**
     * Deletes the referenced account from the in-memory representation and
     * marks it as deleted in the database.
     * @param account    BankAccount to be marked as deleted
     * @return  true if the operation was successful, and false otherwise.
     */
    public boolean delete(BankAccount account) {
        return delete(account.accountID);
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

        Date date = new Date();

        try {
            PreparedStatement updateStatement = dataConnection.prepareStatement(
                    " UPDATE BankAccount SET StatusID=" + Status.Deleted.ordinal() +
                            ", Deleted='" + new Timestamp(date.getTime()) +
                            "' WHERE BankAccountID=" + accountID + "; ");
            if (updateStatement.executeUpdate() == 0) {
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
                    "SELECT * FROM BankAccount a WHERE StatusID != " + Status.Deleted.ordinal());
            while (itemResult.next()) {
                BankAccountRecord rec = new BankAccountRecord(itemResult);
                accountMap.put(rec.bankAccountID, new BankAccount(rec));
            }

            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot read static info!", e);
        }
    }

}
