package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankAccountRecord extends EntityRecord {
    double credits;

    BankAccountRecord() {
        super(EntityType.BankAccount);
        credits = 0;
    }

    BankAccountRecord(ResultSet itemResult){
        super(itemResult);
        try {
            credits = itemResult.getDouble("Credits");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to extract data from item result set", e);
        }
    }

    String getInsertString() {
        return " INSERT INTO BankAccount ( EntityID, Credits )\n" +
                "    VALUES (" + entityID + ", " + credits + "); ";
    }

    @Override
    void insertInto(Connection dataConnection) throws SQLException {
        super.insertInto(dataConnection);
        PreparedStatement accountStatement = dataConnection.prepareStatement(getInsertString());

        int affectedRows = accountStatement.executeUpdate();
        if (affectedRows == 0)
            throw new SQLException("Creating BankAccount record failed.");
    }
}
