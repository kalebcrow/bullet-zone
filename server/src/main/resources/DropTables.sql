-- BEGIN NOT ATOMIC
    SET FOREIGN_KEY_CHECKS = 0;
    DROP TABLE IF EXISTS AccountTransferHistory;
    DROP TABLE IF EXISTS BankAccount;
    DROP TABLE IF EXISTS BankAccount_User_Permissions;
    DROP TABLE IF EXISTS Common;
    DROP TABLE IF EXISTS Item;
    DROP TABLE IF EXISTS ItemCategory;
    DROP TABLE IF EXISTS ItemContainer;
    DROP TABLE IF EXISTS ItemContainer_Item;
    DROP TABLE IF EXISTS ItemContainer_User_Permissions;
    DROP TABLE IF EXISTS ItemProperty;
    DROP TABLE IF EXISTS ItemPropertyType;
    DROP TABLE IF EXISTS ItemType;
    DROP TABLE IF EXISTS Permission;
    DROP TABLE IF EXISTS Status;
    DROP TABLE IF EXISTS TerrainType;
    DROP TABLE IF EXISTS User;
    SET FOREIGN_KEY_CHECKS = 1;
-- END