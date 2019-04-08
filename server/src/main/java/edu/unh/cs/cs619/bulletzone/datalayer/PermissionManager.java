package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

public class PermissionManager {
    Connection dataConnection;
    GameItemRepository itemRepo;
    GameUserRepository userRepo;

    public void setOwner(GameItemContainer item, GameUser user) {
        setOwner(item.itemID, user.userID);
    }

    public void removeOwner(GameItemContainer item) {
        removeOwner(item.itemID, item.getOwner().userID);
    }

    /**
     * Deletes the all permission relationships between oldUser and item from the in-memory
     * representation and marks them as deleted in the database.
     * @param itemID    ID of the item to be marked as deleted
     * @param oldUserID ID of the user who no longer owns the item
     * @return  true if the operation was successful, and false otherwise.
     */
    public boolean removeOwner(int itemID, int oldUserID) {
        GameItemContainer item = itemRepo.getContainer(itemID);
        GameUser user = userRepo.getUser(oldUserID);
        if (item == null || user == null)
            return false;

        Date date = new Date();

        try {
            PreparedStatement updateStatement = dataConnection.prepareStatement(
                    " UPDATE ItemContainer_User_Permissions SET StatusID=" + Status.Deleted.ordinal() +
                            ", Deleted='" + new Timestamp(date.getTime()) +
                            "' WHERE ItemID=" + itemID + " AND UserID=" + oldUserID + "; ");
            if (updateStatement.executeUpdate() == 0)
                return false; //nothing deleted
        } catch (SQLException e) {
            throw new IllegalStateException("Error while removing ownership.", e);
        }

        item.setOwner(null);
        user.removeItem(item);
        return true;
    }

    /**
     * Creates the owner-permission relationships between user and item from the in-memory
     * representation and in the database.
     * @param itemID    ID of the item to be marked as deleted
     * @param userID ID of the user who no longer owns the item
     * @return  true if the operation was successful, and false otherwise.
     */
    public boolean setOwner(int itemID, int userID) {
        GameItemContainer item = itemRepo.getContainer(itemID);
        GameUser user = userRepo.getUser(userID);
        if (item == null || user == null)
            return false;
        GameUser oldOwner = item.getOwner();
        if (oldOwner != null)
            removeOwner(itemID, oldOwner.userID);

        ItemPermissionRecord rec = new ItemPermissionRecord();
        rec.itemID = itemID;
        rec.userID = userID;
        rec.permissionID = Permission.Owner.ordinal();
        rec.statusID = Status.Active.ordinal();
        Date date = new Date();
        rec.created = new Timestamp(date.getTime());

        try {
            PreparedStatement insertStatement = dataConnection.prepareStatement(
                    " INSERT INTO ItemContainer_User_Permissions ( ItemID, UserID, PermissionID, StatusID, Created )\n" +
                            "    VALUES (" + rec.itemID + ", "
                            + rec.userID + ", "
                            + rec.permissionID + ", "
                            + rec.statusID + ", '"
                            + rec.created + "'); ");
            if (insertStatement.executeUpdate() == 0)
                return false;
        } catch (SQLException e) {
            throw new IllegalStateException("Error while setting ownership.", e);
        }

        item.setOwner(user);
        user.addItem(item);
        return true;
    }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------

    /**
     * Reads the database and fills the HashMaps as appropriate. Intended to be called once
     * at time of initialization.
     *
     * @param sqlDataConnection connection on which to make all future SQL queries
     * @param gameItemRepo repository of items that can be owned
     * @param gameUserRepo repository of users that can own things
     */
    void refresh(Connection sqlDataConnection, GameItemRepository gameItemRepo, GameUserRepository gameUserRepo) {
        itemRepo = gameItemRepo;
        userRepo = gameUserRepo;
        dataConnection = sqlDataConnection;
        try {
            Statement statement = dataConnection.createStatement();

            // Read mapping of users to items that they own
            ResultSet mappingResult = statement.executeQuery(
                    "SELECT * FROM ItemContainer_User_Permissions WHERE PermissionID = "
                            + Permission.Owner.ordinal());
            while (mappingResult.next()) {
                int itemID = mappingResult.getInt("ItemID");
                int userID = mappingResult.getInt("UserID");

                // not worrying about StartSlot, EndSlot, or Modifier right now...
                GameItemContainer container = itemRepo.getContainer(itemID);
                GameUser user = userRepo.getUser(userID);
                user.addItem(container);
                container.setOwner(user);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot read static info!", e);
        }
    }

}
