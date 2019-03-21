package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class GameUserRepository {
    HashMap<Integer, GameUser> userMap = new HashMap<>();
    Connection dataConnection;
    GameItemRepository itemRepo;

    GameUser getUser(int userID) { return userMap.get(userID); }

    /**
     * Reads the database and fills the HashMaps as appropriate. Intended to be called once
     * at time of initialization.
     *
     * @param sqlDataConnection connection on which to make all future SQL queries
     */
    void refresh(Connection sqlDataConnection, GameItemRepository gameItemRepo) {
        itemRepo = gameItemRepo;
        dataConnection = sqlDataConnection;
        try {
            Statement statement = dataConnection.createStatement();
            // Read users that aren't deleted
            ResultSet userResult = statement.executeQuery(
                    "SELECT * FROM User u WHERE StatusID != " + Status.Deleted.ordinal());
            while (userResult.next()) {
                GameUserRecord rec = makeUserRecordFromResultSet(userResult);
                userMap.put(rec.userID, new GameUser(rec));
            }

            // Read mapping of users to items that they own
            ResultSet mappingResult = statement.executeQuery(
                    "SELECT * FROM ItemContainer_User_Permissions WHERE PermissionID = "
                            + Permission.Owner.ordinal());
            while (mappingResult.next()) {
                int itemID = mappingResult.getInt("ItemID");
                int userID = mappingResult.getInt("UserID");

                // not worrying about StartSlot, EndSlot, or Modifier right now...
                GameItemContainer container = itemRepo.getContainer(itemID);
                GameUser user = getUser(userID);
                user.addItem(container);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot read static info!", e);
        }
    }

    /**
     * Converts a ResultSet to a GameUserRecord for further processing. It assumes that the record
     * it should be getting data for was labeled with the name "u".
     * @param userResult    The ResultSet that's the result of an SQL query with item labeled "u"
     * @return GameItemRecord filled with data from the current item in the ResultSet.
     */
    private GameUserRecord makeUserRecordFromResultSet(ResultSet userResult) {
        GameUserRecord rec = new GameUserRecord();
        try {
            rec.userID = userResult.getInt("u.UserID");
            rec.name = userResult.getString("u.Name");
            rec.statusID = userResult.getInt("u.StatusID");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to extract data from user result set", e);
        }
        return rec;
    }

}
