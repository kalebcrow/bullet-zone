package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

public class EntityRecord extends EnumeratedRecord {
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
            throw new IllegalStateException("Unable to extract data from entity result set", e);
        }
    }

    @Override
    int getID() { return entityID; }

    @Override
    void setID(int id) { entityID = id; }

    @Override
    String getRecordInsertString() {
        return " INSERT INTO Entity ( EntityTypeID, StatusID, Created )\n" +
                "    VALUES (" + entityType.ordinal() + ", "
                + statusID + ", '"
                + created + "'); ";
    }

    void insertInto(Connection dataConnection) throws SQLException {
        insertInto(dataConnection, entityType.name());
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
