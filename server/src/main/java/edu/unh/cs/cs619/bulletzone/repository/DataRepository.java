package edu.unh.cs.cs619.bulletzone.repository;

import org.springframework.stereotype.Component;

import edu.unh.cs.cs619.bulletzone.datalayer.BulletZoneData;
import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccount;
import edu.unh.cs.cs619.bulletzone.datalayer.item.GameItem;
import edu.unh.cs.cs619.bulletzone.datalayer.item.GameItemContainer;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class provides tailored access to objects that are needed by the REST API/Controller
 * classes. The idea is that it will interface with a BulletZoneData instance as well as
 * any other objects it needs to answer requests having to do with users, items, accounts,
 * permissions, and other things that are related to what is stored in the database.
 *
 * The convention is that actual objects will be returned by the DataRepository so that internal
 * objects can make effective use of the results as well as the Controllers. This means that
 * all API/Controller classes will need to translate these objects into the strings they need
 * to communicate information back to the caller.
 */
//Note that the @Component annotation below causes an instance of a DataRepository to be
//created and used for the Controller classes in the "web" package.
@Component
public class DataRepository {
    private BulletZoneData bzdata;

    DataRepository() {
        String url = "jdbc:mysql://stman1.cs.unh.edu:3306/cs61902dev";
        String username = "baryte";
        String password = "kle#tOwy5p";

        bzdata = new BulletZoneData(url, username, password);
    }

    public DataRepository(boolean test) {
        String url = "jdbc:mysql://stman1.cs.unh.edu:3306/cs61902dev";
        String username = "baryte";
        String password = "kle#tOwy5p";

        bzdata = new BulletZoneData(url, username, password);
    }

    /**
     * Stub for a method that would create a user or validate the user. [You don't have
     * to do it this way--feel free to make other methods if you like!]
     * @param username Username for the user to create or validate
     * @param password Password for the user
     * @param create true if the user should be created, or false otherwise
     * @return GameUser corresponding to the username/password if successful, null otherwise
     */
    public GameUser validateUser(String username, String password, boolean create) {
        GameUser user;
        if (create) {
            // create/verify user credentials
            user = bzdata.users.createUser("test", username, password);
            GameUser validateUser = bzdata.users.validateLogin(username, password);
            if (user == validateUser && validateUser != null) {
                // once the user is legit give them a garage with a tank and a bank account
                GameItemContainer garage = bzdata.items.createContainer(bzdata.types.GarageBay);
                GameItem tank = bzdata.items.create(bzdata.types.TankFrame);
                bzdata.items.addItemToContainer(tank, garage);
                bzdata.permissions.setOwner(tank, user);
                // create a bank account for the user
                BankAccount account = bzdata.accounts.create();
                bzdata.accounts.modifyBalance(account, 1000);
                bzdata.permissions.setOwner(account, user);
            }
        } else {
            user = bzdata.users.getUser(username);
        }
        return user;
    }

    /**
     * Method to return a specific users bank account balance
     * @param username Username for the user
     * @return GameUser bank account balance, null otherwise
     */
    public double getUserAccountBalance(String username) {
        GameUser user = bzdata.users.getUser(username);
        if (user != null) {
            BankAccount bankAccount = user.getOwnedAccounts().iterator().next();
            return bankAccount.getBalance();
        } else {
            return 0;
        }
    }

    public boolean modifyAccountBalance(String username, double amount) {
        GameUser user = bzdata.users.getUser(username);
        if (user != null) {
            BankAccount account = user.getOwnedAccounts().iterator().next();
            bzdata.accounts.modifyBalance(account, amount);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to return a specific users objects
     * @param username Username for the user to create or validate
     * @return the one tank that the user owns at this time
     */
    public String getUserItem(String username) {
        GameUser user = bzdata.users.getUser(username);
        if (user != null) {
            StringBuilder itemList = new StringBuilder();
            for (GameItemContainer c : user.getOwnedContainerItems()) {
                itemList.append(c.getType()).append(": ").append(c.getId()).append("\n");
            }
            return itemList.toString();
        }
        return "empty";
    }
}
