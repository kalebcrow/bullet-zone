package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import edu.unh.cs.cs619.bulletzone.datalayer.itemType.ItemType;
import edu.unh.cs.cs619.bulletzone.datalayer.itemType.ItemTypeRepository;

public class EntityRecord {
    int entityID;
    EntityType entityType;
    int statusID;
    Timestamp created;
    Timestamp deleted;

    EntityRecord(EntityType eType) {
        entityType = eType;
        statusID = Status.Active.ordinal();
        Date date = new Date();
        created = new Timestamp(date.getTime());
    }

    EntityRecord(ResultSet itemResult){
        try {
            entityID = itemResult.getInt("EntityID");
            entityType = EntityType.values()[itemResult.getInt("EntityTypeID")];
            statusID = itemResult.getInt("StatusID");
            created = itemResult.getTimestamp("Created");
            deleted = itemResult.getTimestamp("Deleted");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to extract data from item result set", e);
        }
    }

    private String getEntityInsertString() {
        return " INSERT INTO Entity ( EntityTypeID, StatusID, Created )\n" +
                "    VALUES (" + entityType.ordinal() + ", "
                + statusID + ", '"
                + created + "'); ";
    }

    void insertInto(Connection dataConnection) throws SQLException {
        PreparedStatement insertStatement = dataConnection.prepareStatement(
                getEntityInsertString(), Statement.RETURN_GENERATED_KEYS);
        int affectedRows = insertStatement.executeUpdate();
        if (affectedRows == 0)
            throw new SQLException("Creating Entity of type " + entityType.name() + " failed.");

        ResultSet generatedKeys = insertStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            entityID = generatedKeys.getInt(1);
        }
        else {
            throw new SQLException("Created Entity of type " + entityType.name() + " but failed to obtain ID.");
        }
    }

    static boolean markDeleted(int entityID, Connection dataConnection) throws SQLException {
        Date date = new Date();
        PreparedStatement updateStatement = dataConnection.prepareStatement(
            " UPDATE Entity SET StatusID=" + Status.Deleted.ordinal() +
                    ", Deleted='" + new Timestamp(date.getTime()) +
                    "' WHERE EntityID=" + entityID + "; ");
        return (updateStatement.executeUpdate() > 0);
    }

}
