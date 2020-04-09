package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

class GameUserRecord {
    int userID;
    String name;
    String username;
    byte[] passwordHash;
    byte[] passwordSalt;
    int statusID;
    Timestamp created;
    Timestamp deleted;

    GameUserRecord(String guName, String guUsername) {
        name = guName;
        username = guUsername;
        statusID = Status.Active.ordinal();
        Date date = new Date();
        created = new Timestamp(date.getTime());
    }

    GameUserRecord(ResultSet userResult) {
        try {
            //ResultSetMetaData rsmd = userResult.getMetaData();
            //String firstColumnName = rsmd.getColumnName(1);
            //System.out.println(firstColumnName);
            userID = userResult.getInt("UserID");
            name = userResult.getString("Name");
            username = userResult.getString("Username");
            passwordHash = decocdeBytesAsHex(userResult.getString("PasswordHash"));
            passwordSalt = decocdeBytesAsHex(userResult.getString("PasswordSalt"));
            statusID = userResult.getInt("StatusID");
            created = userResult.getTimestamp("Created");
            deleted = userResult.getTimestamp("Deleted");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to extract data from user result set", e);
        }
    }

    String getInsertString() {
        return " INSERT INTO User ( Name, Username, PasswordHash, PasswordSalt, StatusID, Created )\n" +
                "    VALUES ('" + name + "', '"
                + username + "', '"
                + encodeBytesAsHex(passwordHash) + "', '"
                + encodeBytesAsHex(passwordSalt) + "', "
                + statusID + ", '"
                + created + "'); ";
    }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------

    /**
     * Converts a byte array to a string of uppercase hexadecimal numbers with no spaces
     * @param bytes The byte array to be encoded
     * @return  A string of upper-case hexadecimal characters without spaces between them
     */
    private String encodeBytesAsHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }

    /**
     * Converts a hexadecimal string without spaces to a sequence of bytes
     * @param hex   A string of hexadecimal characters without spaces between them
     * @return  The corresponding byte array
     */
    private byte[] decocdeBytesAsHex(String hex) {
        int length = hex.length();
        byte[] result = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            result[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) +
                    (Character.digit(hex.charAt(i+1), 16)));
        }
        return result;
    }
}
