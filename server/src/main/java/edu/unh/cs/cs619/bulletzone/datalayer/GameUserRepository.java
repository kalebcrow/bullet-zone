package edu.unh.cs.cs619.bulletzone.datalayer;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class GameUserRepository {
    HashMap<Integer, GameUser> userMap = new HashMap<>();
    HashMap<String, GameUser> usernameToUserMap = new HashMap<>();
    BulletZoneData data;

    final int iterations = 65536;
    final int keySize = 128;
    final int saltSize = 16;

    /**
     * @return A collection of all ItemCategories in the database
     */
    public Collection<GameUser> getUsers() { return userMap.values(); }

    /**
     * @param userID    ID of the user to get
     * @return  GameUser corresponding to passed ID
     */
    public GameUser getUser(int userID) { return userMap.get(userID); }

    public GameUser getUser(String username) { return usernameToUserMap.get(username); }

    /**
     * Returns a new user, or null if an active user with the passed username already exists.
     * @param name  New user's screen name
     * @param username  User's username for the purpose of logging-in/authorizing
     * @param password  User's password for the purpose of logging-in/authorizing
     * @return  New GameUser object corresponding to the newly created user, or null if already
     *          exists. Any database errors result in exceptions.
     */
    public GameUser createUser(String name, String username, String password) {
        if (getUser(username) != null)
            return null;

        GameUserRecord newRecord = new GameUserRecord(name, username);
        GameUser newUser = null;

        //The following is adapted from https://www.baeldung.com/java-password-hashing
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltSize];
        random.nextBytes(salt);

        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keySize);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            newRecord.passwordHash = hash;
            newRecord.passwordSalt = salt;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Unable to attempt password creation!", e);
        }

        try {
            Connection dataConnection = data.getConnection();
            if (dataConnection == null)
                return null;

            // Create base item
            PreparedStatement insertStatement = dataConnection.prepareStatement(
                    newRecord.getInsertString(), Statement.RETURN_GENERATED_KEYS);
            int affectedRows = insertStatement.executeUpdate();
            if (affectedRows == 0)
                throw new SQLException("Creating User " + newRecord.username + " failed.");

            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                newRecord.userID = generatedKeys.getInt(1);
            }
            else {
                throw new SQLException("Created user " + newRecord.username + " but failed to obtain ID.");
            }

            dataConnection.close();
            newUser = new GameUser(newRecord);
            userMap.put(newRecord.userID, newUser);
            usernameToUserMap.put(newRecord.username, newUser);
        } catch (SQLException e) {
            throw new IllegalStateException("Error while creating item!", e);
        }
        System.out.println("New user " + username + " added with ID " + newRecord.userID);
        return newUser;
    }

    /**
     * Returns the GameUser associated with a given username if the password matches
     * @param username  Username for the desired user
     * @param password  Password for the desired user
     * @return  GameUser corresponding to the username/password, or
     *          null if not found or wrong password
     */
    public GameUser validateLogin(String username, String password) {
        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return null;

        GameUserRecord userRecord = null;
        try {
            Statement statement = dataConnection.createStatement();
            // Read users that aren't deleted
            ResultSet userResult = statement.executeQuery(
                    "SELECT * FROM User u WHERE StatusID != " + Status.Deleted.ordinal()
                            + " AND u.Username = '" + username + "'");
            if (userResult.next()) //else, is empty result list
            {
                userRecord = new GameUserRecord(userResult);
            }
            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to access user table for password validation!", e);
        }
        if (userRecord == null)
            return null;

        //The following is adapted from https://www.baeldung.com/java-password-hashing
        try {
            byte[] salt = userRecord.passwordSalt;
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keySize);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            if (Arrays.equals(hash, userRecord.passwordHash))
                return getUser(userRecord.userID); //matches!
            //else fall through
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Unable to attempt password validation!", e);
        }

        return null;
    }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------

    /**
     * Reads the database and fills the HashMaps as appropriate. Intended to be called once
     * at time of initialization.
     * @param bzData        reference to BulletZoneData class to use for SQL queries
     * @param gameItemRepo  reference to already-initialized GameItemRepository
     */
    void refresh(BulletZoneData bzData, GameItemRepository gameItemRepo) {
        data = bzData;
        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return;

        try {
            Statement statement = dataConnection.createStatement();
            // Read users that aren't deleted
            ResultSet userResult = statement.executeQuery(
                    "SELECT * FROM User u WHERE StatusID != " + Status.Deleted.ordinal());
            while (userResult.next()) {
                GameUserRecord rec = new GameUserRecord(userResult);
                GameUser user = new GameUser(rec);
                userMap.put(rec.userID, user);
                usernameToUserMap.put(rec.username, user);
            }
            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot read static info!", e);
        }
    }

}
