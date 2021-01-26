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
import java.util.Set;

public class PermissionManager {
    BulletZoneData data;
    PermissionTargetRepository targetRepo;
    GameUserRepository userRepo;
    public class Accessible<T> {
        final int maxPermissions = 4;
        public boolean hasPermission(int itemID, Permission p) {
            if (itemPermissions.containsKey(itemID))
                return itemPermissions.get(itemID).contains(p);
            else
                return false;
        }

        public Collection<T> getItems() {
            HashSet<T> items = new HashSet<>();
            for (int itemID: itemPermissions.keySet()) {
                PermissionTarget c = targetRepo.getTarget(itemID);
                if (c != null)
                    items.add((T)c);
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
        boolean removePermission(int itemID, Permission p) {
            if (itemPermissions.containsKey(itemID)) {
                HashSet<Permission> permSet = itemPermissions.get(itemID);
                permSet.remove(p);
                if (permSet.isEmpty())
                    itemPermissions.remove(itemID);
                return !itemPermissions.isEmpty(); //"true" indicates non-empty
            }
            return false; //"false" indicates it is empty
        }
        private final HashMap<Integer, HashSet<Permission>> itemPermissions = new HashMap<>();
    }
    HashMap<Integer, Accessible<GameItemContainer>> permissions = new HashMap<>(); //&&&
    private final HashMap<Integer, Set<GameUser>> itemToPermissionHolders = new HashMap<>();

    public void setOwner(PermissionTarget target, GameUser user) {
        setOwner(target.getId(), user.userID);
    }

    public void removeOwner(PermissionTarget target) {
        removeOwner(target.getId(), target.getOwningUser().userID);
    }

    public boolean check(PermissionTarget target, GameUser user, Permission p) {
        return check(target.getId(), user.userID, p);
    }

    public boolean grant(PermissionTarget target, GameUser user, Permission p) {
        return grant(target.getId(), user.userID, p);
    }

    public boolean revoke(PermissionTarget target, GameUser user, Permission p) {
        return revoke(target.getId(), user.userID, p);
    }

    public Accessible getUserPermissions(GameUser user) {
        return getUserPermissions(user.userID);
    }

    public Accessible getUserPermissions(int userID) {
        if (!permissions.containsKey(userID))
            permissions.put(userID, new Accessible<>());
        return permissions.get(userID);
    }

    public Collection<GameUser> getUsersWithPermissionsOn(PermissionTarget target) {
        return getUsersWithPermissionsOn(target.getId());
    }

    public Collection<GameUser> getUsersWithPermissionsOn(int itemID) {
        return itemToPermissionHolders.get(itemID);
    }
    /**
     * Deletes the all permission relationships between oldUser and item from the in-memory
     * representation and marks them as deleted in the database.
     * @param itemID    ID of the item to be marked as deleted
     * @param oldUserID ID of the user who no longer owns the item
     * @return  true if the operation was successful, and false otherwise.
     */
    public boolean removeOwner(int itemID, int oldUserID) {
        PermissionTarget item = targetRepo.getTarget(itemID);
        GameUser user = userRepo.getUser(oldUserID);
        if (item == null || user == null)
            return false;

        if (!deletePermission(itemID, oldUserID, Permission.Owner))
            return false;

        item.setOwningUser(null);
        user.removePermissionTarget(item);
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
        PermissionTarget item = targetRepo.getTarget(itemID);
        GameUser user = userRepo.getUser(userID);
        if (item == null || user == null)
            return false;
        GameUser oldOwner = item.getOwningUser();
        if (oldOwner != null)
            removeOwner(itemID, oldOwner.userID);

        if (!insertPermission(itemID, userID, Permission.Owner))
            return false;

        item.setOwningUser(user);
        user.addPermissionTarget(item);
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
            PermissionTarget item = targetRepo.getTarget(itemID);
            if (item == null)
                return false;
            return (item.getOwningUser().userID == userID);
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
            permissions.put(userID, new Accessible<>());
        permissions.get(userID).addPermission(itemID, p);
        if (!itemToPermissionHolders.containsKey(itemID))
            itemToPermissionHolders.put(itemID, new HashSet<GameUser>());
        itemToPermissionHolders.get(itemID).add(userRepo.getUser(userID));
    }

    /**
     * Update internal structure to account for a revoked permission
     * @param itemID    ID of the item whose permissions are being changed
     * @param userID    ID of the user who is having the permission revoked
     * @param p         Permission being removed
     */
    void removePermission(int itemID, int userID, Permission p) {
        if (permissions.containsKey(userID)) {
            if (permissions.get(userID).removePermission(itemID, p) == false) //indicates empty
                itemToPermissionHolders.get(itemID).remove(userRepo.getUser(userID));
        }
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

        ItemPermissionRecord rec = new ItemPermissionRecord(itemID, userID, p);

        try {
            PreparedStatement insertStatement = dataConnection.prepareStatement(rec.getInsertString());
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
     * @param permissionTargetRepo repository of items that can be owned
     * @param gameUserRepo repository of users that can own things
     */
    void refresh(BulletZoneData bzData, PermissionTargetRepository permissionTargetRepo, GameUserRepository gameUserRepo) {
        targetRepo = permissionTargetRepo;
        userRepo = gameUserRepo;
        data = bzData;
        itemToPermissionHolders.clear();
        permissions.clear();
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
                    PermissionTarget container = targetRepo.getTarget(itemID);
                    GameUser user = userRepo.getUser(userID);
                    if (user == null || container == null) //could be null if user or container were marked as deleted
                        continue; //just skip everything if something is null
                    user.addPermissionTarget(container);
                    container.setOwningUser(user);
                }
                addPermission(itemID, userID, permission);
            }
            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot read static info!", e);
        }
    }

}
