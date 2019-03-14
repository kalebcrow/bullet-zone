BEGIN NOT ATOMIC
    CREATE TABLE IF NOT EXISTS User
    (
      UserID INT NOT NULL,
      Name VARCHAR(40) NOT NULL,
      PRIMARY KEY (UserID)
    );

    CREATE TABLE IF NOT EXISTS Permission
    (
      PermissionID INT NOT NULL,
      Name VARCHAR(40) NOT NULL,
      PRIMARY KEY (PermissionID)
    );

    #CREATE TABLE IF NOT EXISTS Location/Map
    #(
    #);

    CREATE TABLE IF NOT EXISTS ItemPropertyType
    (
      ItemPropertyTypeID INT NOT NULL,
      Name VARCHAR(40) NOT NULL,
      PRIMARY KEY (ItemPropertyTypeID)
    );

    CREATE TABLE IF NOT EXISTS ItemContainer_User_Permissions
    (
      ItemID INT NOT NULL,
      UserID INT NOT NULL,
      PermissionID INT NOT NULL,
      FOREIGN KEY (ItemID) REFERENCES ItemContainer(ItemID)
      FOREIGN KEY (UserID) REFERENCES User(UserID),
      FOREIGN KEY (PermissionID) REFERENCES Permission(PermissionID)
    );

    CREATE TABLE IF NOT EXISTS BankAccount
    (
      BankAccountID INT NOT NULL,
      Credits FLOAT NOT NULL,
      PRIMARY KEY (BankAccountID)
    );

    CREATE TABLE IF NOT EXISTS BankAccount_User_Permissions
    (
      BankAccountID INT NOT NULL,
      UserID INT NOT NULL,
      PermissionID INT NOT NULL,
      FOREIGN KEY (BankAccountID) REFERENCES BankAccount(BankAccountID),
      FOREIGN KEY (UserID) REFERENCES User(UserID),
      FOREIGN KEY (PermissionID) REFERENCES Permission(PermissionID)
    );

    CREATE TABLE IF NOT EXISTS ItemProperty
    (
      ItemPropertyID INT NOT NULL,
      Name VARCHAR(40) NOT NULL,
      ItemPropertyTypeID INT NOT NULL,
      PRIMARY KEY (ItemPropertyID),
      FOREIGN KEY (ItemPropertyTypeID) REFERENCES ItemPropertyType(ItemPropertyTypeID)
    );

    CREATE TABLE IF NOT EXISTS ItemCategory
    (
      ItemCategoryID INT NOT NULL,
      Name VARCHAR(40) NOT NULL,
      ItemPropertyID1 INT NOT NULL,
      ItemPropertyID2 INT NOT NULL,
      ItemPropertyID3 INT NOT NULL,
      PRIMARY KEY (ItemCategoryID),
      FOREIGN KEY (ItemPropertyID1) REFERENCES ItemProperty(ItemPropertyID),
      FOREIGN KEY (ItemPropertyID2) REFERENCES ItemProperty(ItemPropertyID),
      FOREIGN KEY (ItemPropertyID3) REFERENCES ItemProperty(ItemPropertyID)
    );

    CREATE TABLE ItemType
    (
      ItemTypeID INT NOT NULL,
      Name VARCHAR(40) NOT NULL,
      ItemCategoryID INT NOT NULL,
      Size FLOAT NOT NULL,
      Weight FLOAT NOT NULL,
      Price FLOAT NOT NULL,
      PropertyVal1 FLOAT NOT NULL,
      PropertyVal2 FLOAT NOT NULL,
      PropertyVal3 FLOAT NOT NULL,
      PRIMARY KEY (ItemTypeID),
      FOREIGN KEY (ItemCategoryID) REFERENCES ItemCategory(ItemCategoryID)
    );

    CREATE TABLE Item
    (
      ItemID INT NOT NULL,
      ItemTypeID INT NOT NULL,
      UsageMonitor FLOAT NOT NULL,
      PRIMARY KEY (ItemID),
      FOREIGN KEY (ItemTypeID) REFERENCES ItemType(ItemTypeID)
    );

    CREATE TABLE IF NOT EXISTS ItemContainer
    (
      Name VARCHAR(40) NOT NULL,
      ItemID INT NOT NULL,
      PRIMARY KEY (ItemID),
      FOREIGN KEY (ItemID) REFERENCES Item(ItemID)
    );

    CREATE TABLE IF NOT EXISTS ItemContainer_Item
    (
      ItemID INT NOT NULL,
      Container_ItemID INT NOT NULL,
      StartSlot INT NOT NULL,
      EndSlot INT NOT NULL,
      Modifier INT NOT NULL,
      FOREIGN KEY (ItemID) REFERENCES Item(ItemID),
      FOREIGN KEY (Container_ItemID) REFERENCES ItemContainer(ItemID)
    );

END