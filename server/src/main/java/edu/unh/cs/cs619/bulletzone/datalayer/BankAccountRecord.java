package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import edu.unh.cs.cs619.bulletzone.datalayer.itemType.ItemType;
import edu.unh.cs.cs619.bulletzone.datalayer.itemType.ItemTypeRepository;

public class BankAccountRecord {
    int bankAccountID;
    double credits;
    int statusID;
    Timestamp created;
    Timestamp deleted;

    BankAccountRecord() {
        credits = 0;
        statusID = Status.Active.ordinal();
        Date date = new Date();
        created = new Timestamp(date.getTime());
    }

    BankAccountRecord(ResultSet itemResult){
        try {
            bankAccountID = itemResult.getInt("BankAccountID");
            credits = itemResult.getDouble("Credits");
            statusID = itemResult.getInt("StatusID");
            created = itemResult.getTimestamp("Created");
            deleted = itemResult.getTimestamp("Deleted");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to extract data from item result set", e);
        }
    }

    String getInsertString() {
        return " INSERT INTO BankAccount ( Credits, StatusID, Created )\n" +
                "    VALUES (" + credits + ", "
                + statusID + ", '"
                + created + "'); ";
    }
}
