/**
 * This is the main interaction point for the datalayer package.
 * <p/>
 * Each instance of this class corresponds to an instance of a game database. In most cases,
 * you will just want a single instance (and it could be problematic to have more than one
 * instance). However, since it's possible that one application may need to be able to access
 * more than one game database at a time, this class is not a singleton.
 * It is important to remember to close the database connection when you're done with the
 * BulletZoneData object, so that you don't open so many connections that the server becomes
 * inaccessible (the connections will otherwise time out eventually, but it may not be as
 * early as you like).
 *
 */
package edu.unh.cs.cs619.bulletzone.datalayer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class BulletZoneData {
    private String _url;
    private String _username;
    private String _password;
    public ItemTypeRepository types = new ItemTypeRepository();
    public GameItemRepository items = new GameItemRepository();
    public GameUserRepository users = new GameUserRepository();
    public PermissionManager permissions = new PermissionManager();

    /**
     * Opens the database at the specified URL and initializes internal structures
     * @param url       URL for the database, usually in the form "jdbc:mysql://server-name.com:3306/database-name"
     * @param username  Username with appropriate access to the database specified by url
     * @param password  Password for the user specified by username
     */
    public BulletZoneData(String url, String username, String password)
    {
        _url = url;
        _username = username;
        _password = password;
        Connection dataConnection = getConnection();
        if (dataConnection == null)
            return;

        try {
            Statement statement = dataConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("show tables");
            if (!resultSet.next()) //no next row means we don't have any tables
                initializeData();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot access tables!", e);
        }

        try {
            types.readStaticInfo(dataConnection);
            items.refresh(this, types);
            users.refresh(this, items);
            permissions.refresh(this , items, users);
        } catch (IllegalStateException e) {
            System.out.println("Unable to read initial data. Exception listed below, but coninuing...");
            System.out.println(e.toString());
        }
    }

    /**
     * Lists the names of all tables in the currently open database to System.out, one per line.
     * This is intended to be a debugging method only.
     */
    public void listTables() {
        try {
            Connection dataConnection = getConnection();
            if (dataConnection == null)
                return;
            Statement statement = dataConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("show tables");
            System.out.println("Database tables:");
            while (resultSet.next())
                System.out.println(resultSet.getString(1));
            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot show tables!", e);
        }
    }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------

    /**
     * Package-level method for getting an appropriate database connection
     * @return Connection to SQL database that can be used for further queries. Note that
     *         the caller should close the connection when finished.
     */
    Connection getConnection() {
        Connection dataConnection;
        try {
            dataConnection = DriverManager.getConnection(_url, _username, _password);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database!", e);
        }
        return dataConnection;
    }

    /**
     * Assuming an empty database, creates all the appropriate tables and then populates
     * them with appropriate data (especially for enumeration tables).
     */
    private void initializeData() {
        executeScript("/CreateTables.sql", "Cannot create tables!");
        executeScript("/PopulateTables.sql", "Cannot populate tables!");
    }

    /**
     * Drops all tables (which destroys all existing data!!), then rebuilds it by calling the
     * initializeData method.
     */
    public void rebuildData() {
        executeScript("/DropTables.sql", "Cannot drop all tables!");
        initializeData();
    }

    /**
     * Executes the passed SQL script on the current database (it assumes the account given
     * at construction time has the appropriate permissions to execute whatever is in the script).
     * @param resourceName  Name of the Java resource file, usually of the form "/SomeFile.sql"
     * @param errorMessage  String to be printed
     * @return  A SQL ResultSet with the raw result of the query.
     */
    ResultSet executeScript(String resourceName, String errorMessage)
    {
        Connection dataConnection = getConnection();
        ResultSet resultSet = null;

        if (dataConnection == null)
            return resultSet;

        try {
            Statement statement = dataConnection.createStatement();
            InputStream in = getClass().getResourceAsStream(resourceName);
            if (in == null)
                System.err.println("missing resource file");
            else {
                String createQuery = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
                resultSet = statement.executeQuery(createQuery);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(errorMessage, e);
        }
        return resultSet;
    }

    public static void main(String[] args) {
        String url = "jdbc:mysql://stman1.cs.unh.edu:3306/cs6195";
        String username = "mdp";
        String password = "Drag56kes";

        BulletZoneData d = new BulletZoneData(url, username, password);
        //d.rebuildData();
        //d.listTables();

        GameItemContainer bay = d.items.createContainer("Garage bay");
        GameItemContainer tank1 = d.items.createContainer("Standard tank frame");
        GameItemContainer tank2 = d.items.createContainer("Standard tank frame");
        d.items.addItemToContainer(tank1, bay);
        d.items.addItemToContainer(tank2, bay);
        d.items.removeAllFromContainer(bay);
        d.items.delete(tank1.itemID);
        if (d.items.getItemOrContainer(tank1.itemID) == null)
            System.out.println("Successfully deleted tank1");
        GameUser user = d.users.createUser("Test User", "testuser", "testPass");
        GameUser user2 = d.users.validateLogin("testuser", "testPass");
        if (user == user2 && user2 != null)
            System.out.println("User creation/validation successful");
        d.permissions.setOwner(tank2, user2);
        GameUser user3 = d.users.createUser("Second User", "user2", "xyzzy");
        user3 = d.users.validateLogin("user2", "xyzzy");
        if (!d.permissions.grant(tank2, user3, Permission.Add))
            System.out.println("Add permission not granted");
        if (!d.permissions.check(tank2, user3, Permission.Add))
            System.out.println("Add permission granted but doesn't check");
        if (!d.permissions.grant(tank2, user3, Permission.Remove))
            System.out.println("Remove permission not granted");
        if (!d.permissions.check(tank2, user3, Permission.Remove))
            System.out.println("Remove permission granted but doesn't check");
        if (!d.permissions.grant(tank2, user3, Permission.Use))
            System.out.println("Use permission not granted");
        if (!d.permissions.check(tank2, user3, Permission.Use))
            System.out.println("Use permission granted but doesn't check");
        if (d.permissions.grant(tank2, user3, Permission.Use))
            System.out.println("Use permission granted twice");
        if (!d.permissions.revoke(tank2, user3, Permission.Remove))
            System.out.println("Remove permission not revoked");
        if (d.permissions.check(tank2, user3, Permission.Transfer))
            System.out.println("Transfer permission never granted but somehow checks");
        if (d.permissions.revoke(tank2, user3, Permission.Transfer))
            System.out.println("Nonexistent transfer permission somehow revoked");
        GameUser user4 = d.users.createUser("UserF", "userF", "xyzzy");
        //d.permissions.setOwner(bay, user4);
        for (GameUser u : d.users.getUsers()) {
            PermissionManager.AccessibleItems ip = d.permissions.getUserPermissions(u);
            for (GameItemContainer container : ip.getItems()) {
                System.out.print(u.name + " has permissions for " + container.name + " (ID=" + container.itemID + "): ");
                for (Permission p : ip.getPermissionsOnItem(container.itemID)) {
                    System.out.print(p.name() + " ");
                }
                System.out.println();
            }
        }
        for (Permission p : Permission.values()) {
            System.out.println(user4.name + " Permission check for " + p.name() + ": " + d.permissions.check(tank2, user4, p));
            System.out.println(user4.name + " Permission ID check for " + p.name() + ": " + d.permissions.check(tank2.itemID, user4.userID, p));
        }
        //d.permissions.removeOwner(tank2);
        for (ItemType t : d.types.getTypes())
        {
            System.out.println("Type " + t.getName() + " has type ID " + t.getID());
        }
        System.out.println("Type 13 is " + d.types.getType(13).getName());

    }
}
