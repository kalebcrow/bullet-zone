package edu.unh.cs.cs619.bulletzone.datalayer;

class BankAccountPermissionRecord extends PermissionTargetRecord {

    BankAccountPermissionRecord(int bapItemID, int bapUserID, Permission p) {
       super(bapItemID, bapUserID, p);
    }

    String getInsertString() {
        return " INSERT INTO ItemContainer_User_Permissions ( BankAccountID, UserID, PermissionID, StatusID, Created )\n" +
                "    VALUES (" + targetID + ", "
                + userID + ", "
                + permissionID + ", "
                + statusID + ", '"
                + created + "'); ";
    }
}