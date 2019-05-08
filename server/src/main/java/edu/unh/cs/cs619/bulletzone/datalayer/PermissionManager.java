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
import java.util.HashSet;

public class PermissionManager {
    BulletZoneData data;
    GameItemRepository itemRepo;
    GameUserRepository userRepo;
    public class AccessibleItems {
        final int maxPermissions = 4;
        public boolean hasPermission(int itemID, Permission p) {
            if (itemPermissions.containsKey(itemID))
                return itemPermissions.get(itemID).contains(p);
            else
                return false;
        }

        public Collection<GameItemContainer> getItems() {
            HashSet<GameItemContainer> items = new HashSet<>();
            for (int itemID: itemPermissions.keySet()) {
                items.add(itemRepo.getContainer(itemID));
            }
            return items;
        }

        public Collection<Permission> getPermissionsOnItem(int itemID) {
            if (itemPermissions.containsKey(itemID))
                return itemPermissions.get(itemID);
            else
                return new HashSet<Permission>();
        }

        //----Package-level methods---
        void addPermission(int itemID, Permission p) {
            if (!itemPermissions.containsKey(itemID))
                itemPermissions.put(itemID, new HashSet<>(maxPermissions));
            itemPermissions.get(itemID).add(p);
        }
        void removePermission(int itemID, Permission p) {
            if (itemPermissions.containsKey(itemID))
                itemPermissions.get(itemID).remove(p);
        }
        private HashMap<Integer, HashSet<Permission>> itemPermissions = new HashMap<>();
    }
    HashMap<Integer, AccessibleItems> permissions = new HashMap<>();

    public void setOwner(GameItemContainer item, GameUser user) {
        setOwner(item.itemID, user.userID);
    }

    public void removeOwner(GameItemContainer item) {
        removeOwner(item.itemID, item.getOwner().userID);
    }

    public boolean check(GameItemContainer item, GameUser user, Permission p) {
        return check(item.itemID, user.userID, p);
    }

    public boolean grant(GameItemContainer item, GameUser user, Permission p) {
        return grant(item.itemID, user.userID, p);
    }

    public boolean revoke(GameItemContainer item, GameUser user, Permission p) {
        return revoke(item.itemID, user.userID, p);
    }

    public AccessibleItems getUserPermissions(GameUser user) {
        return getUserPermissions(user.userID);
    }

    public AccessibleItems getUserPermissions(int userID) {
        if (!permissions.containsKey(userID))
            permissions.put(userID, new AccessibleItems());
        return permissions.get(userID);
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

        if (!deletePermission(itemID, oldUserID, Permission.Owner))
            return false;

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

        if (!insertPermission(itemID, userID, Permission.Owner))
            return false;

        item.setOwner(user);
        user.addItem(item);
        return true;
    }

    /**
     * Determines whether or not the passed user has specified permission on the passed item.
     * @param itemID    ID of the item being checked
     * @param userID    ID of the user being checked
     * @param p         Permission being verified
     * @return  true if the user has the permission, false otherwise.
     */
    public boolean check(int itemID, int userID, Permission p){
        if (p == Permission.Owner){
            GameItemContainer item = itemRepo.getContainer(itemID);
            if (item == null)
                return false;
            return (item.getOwner().userID == userID);
        }
        if (permissions.containsKey(userID)) {
            return permissions.get(userID).hasPermission(itemID, p);
        }
        return false;
    }

    /**
     * Grants the passed user the specified permission for the specified item
     * @param itemID    ID of the item whose permissions are being changed
     * @param userID    ID of the user who is being given the permission
     * @param p         Permission being added
     * @return  true if the permission was granted, false otherwise (including if the
     *          permission was already active)
     */
    public boolean grant(int itemID, int userID, Permission p) {
        if (check(itemID, userID, p)) //if the permission is already active, do nothing
            return false;
        if (p == Permission.Owner){
            return setOwner(itemID, userID);
        }

        if (!insertPermission(itemID, userID, p))
            return false;

        return true;
    }

    /**
     * Revokes from the passed user the specified permission for the specified item
     * @param itemID    ID of the item whose permissions are being changed
     * @param userID    ID of the user who is losing the permission
     * @param p         Permission being removed
     * @return  true if the permission was granted, false otherwise (including if the
     *          permission was already active)
     */
    public boolean revoke(int itemID, int userID, Permission p) {
        if (!check(itemID, userID, p)) //if the permission is already inactive, do nothing
            return false;
        if (p == Permission.Owner){
            return removeOwner(itemID, userID);
        }

        if (!deletePermission(itemID, userID, p))
            return false;

        return true;
    }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------

    /**
     * Update internal structure to account for a new permission
     * @param itemID    ID of the item whose permissions are being changed
     * @param userID    ID of the user who is being given the permission
     * @param p         Permission being added
     */
    void addPermission(int itemID, int userID, Permission p) {
        if (!permissions.containsKey(userID))
            permissions.put(userID, new AccessibleItems());
        permissions.get(userID).addPermission(itemID, p);
    }

    /**
     * Update internal structure to account for a revoked permission
     * @param itemID    ID of the item whose permissions are being changed
     * @param userID    ID of the user who is having the permission revoked
     * @param p         Permission being removed
     */
    void removePermission(int itemID, int userID, Permission p) {
        if (permissions.containsKey(userID))
            permissions.get(userID).removePermission(itemID, p);
    }

    /**
     * Insert a new permission record into the database
     * @param itemID    ID of the item whose permissions are being changed
     * @param userID    ID of the user who is being given the permission
     * @param p         Permission being added
     */
    boolean insertPermission(int itemID, int userID, Permission p) {
        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return false;

        ItemPermissionRecord rec = new ItemPermissionRecord();
        rec.itemID = itemID;
        rec.userID = userID;
        rec.permissionID = p.ordinal();
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
            if (insertStatement.executeUpdate() == 0) {
                dataConnection.close();
                return false;
            }
            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error while permission " + p.name() + ".", e);
        }
        addPermission(itemID, userID, p);
        return true;
    }

    /**
     * Mark a permission record in the database as deleted
     * @param itemID    ID of the item whose permissions are being changed
     * @param userID    ID of the user who is having the permission revoked
     * @param p         Permission being revoked
     */
    boolean deletePermission(int itemID, int userID, Permission p) {
        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return false;
        Date date = new Date();

        try {
            PreparedStatement updateStatement = dataConnection.prepareStatement(
                    " UPDATE ItemContainer_User_Permissions SET StatusID=" + Status.Deleted.ordinal() +
                            ", Deleted='" + new Timestamp(date.getTime()) +
                            "' WHERE ItemID=" + itemID + " AND UserID=" + userID +
                            " AND PermissionID=" + p.ordinal() + "; ");
            if (updateStatement.executeUpdate() == 0) {
                dataConnection.close();
                return false; //nothing deleted
            }
            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error while removing ownership.", e);
        }
        removePermission(itemID, userID, p);
        return true;
    }

    /**
     * Reads the database and fills the HashMaps as appropriate. Intended to be called once
     * at time of initialization.
     *
     * @param bzData       reference to BulletZoneData for making further SQL queries
     * @param gameItemRepo repository of items that can be owned
     * @param gameUserRepo repository of users that can own things
     */
    void refresh(BulletZoneData bzData, GameItemRepository gameItemRepo, GameUserRepository gameUserRepo) {
        itemRepo = gameItemRepo;
        userRepo = gameUserRepo;
        data = bzData;
        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return;
        try {
            Statement statement = dataConnection.createStatement();

            // Read mapping of users to items that they own
            ResultSet mappingResult = statement.executeQuery(
                    "SELECT * FROM ItemContainer_User_Permissions WHERE StatusID != "
                            + Status.Deleted.ordinal());
            Permission perms[] = Permission.values();
            while (mappingResult.next()) {
                int itemID = mappingResult.getInt("ItemID");
                int userID = mappingResult.getInt("UserID");
                Permission permission = perms[mappingResult.getInt("PermissionID")];

                if (permission == Permission.Owner) {
                    // not worrying about StartSlot, EndSlot, or Modifier right now...
                    GameItemContainer container = itemRepo.getContainer(itemID);
                    GameUser user = userRepo.getUser(userID);
                    user.addItem(container);
                    container.setOwner(user);
                }
                addPermission(itemID, userID, permission);
            }
            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot read static info!", e);
        }
    }

}
