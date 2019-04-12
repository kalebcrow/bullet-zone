BEGIN NOT ATOMIC
    INSERT INTO Common
        ( Version)
    VALUES
        ( '0.1');

    INSERT INTO Status
        ( StatusID, Name)
    VALUES
        ( 1, 'Active'),
        ( 2, 'Disabled'),
        ( 3, 'Deleted');

    INSERT INTO Permission
        ( PermissionID, Name)
    VALUES
        ( 1, 'Owner'),
        ( 2, 'Add'),
        ( 3, 'Remove'),
        ( 4, 'Use'),
        ( 5, 'Transfer');

    INSERT INTO ItemPropertyType
        ( ItemPropertyTypeID, Name)
    VALUES
        (0, 'Null'),
        (1, 'Additive'),
        (2, 'Average'),
        (3, 'Multiplicative'),
        (4, 'Restorative');

    INSERT INTO ItemProperty
        ( ItemPropertyID, Name, ItemPropertyTypeID)
    VALUES
        (0, 'None', 0),
        (1, 'Capacity', 1),
        (2, 'Armor', 1),
        (3, 'Damage', 1),
        (4, 'Instance Limit', 2),
        (5, 'Electric Power', 1),
        (6, 'Electric Power Used', 1),
        (7, 'Drive Power', 1),
        (8, 'Drive Power Used', 1),
        (9, 'Movement-to-weight ratio', 1),
        (10, 'Weight modifier', 3),
        (11, 'Armor modifier', 4),
        (12, 'Terrain-obstacle sensitivity', 2);

    INSERT INTO ItemCategory
        ( ItemCategoryID, Name, ItemPropertyID1, ItemPropertyID2, ItemPropertyID3)
    VALUES
        (1, 'Frame', 1, 2, 0),
        (2, 'Weapon', 3, 6, 4),
        (3, 'Generator', 5, 0, 0),
        (4, 'Engine', 7, 6, 0),
        (5, 'Drive', 8, 9, 12),
        -- Power-ups
        (11, 'Anti-gravity', 6, 10, 0),
        (12, 'Weapon-boosting generator', 5, 4, 0),
        (13, 'Shield/Repair', 5, 2, 11);

    INSERT INTO ItemType
        ( ItemTypeID, Name, ItemCategoryID, Size, Weight, Price, PropertyVal1, PropertyVal2, PropertyVal3)
    VALUES
        -- ** In-town items **
        (1, 'Garage bay', 1, 80.0, 10.0, 500.0, 80.0, 50.0, 0.0),
        (2, 'Storage container', 1, 5.1, 1.0, 50.0, 5.0, 5.0, 0.0),
        -- ** "Vehicle" items **
        -- Frames
        (11, 'Standard tank frame', 1, 6.0, 6.0, 350.0, 16.0, 100.0, 0.0),
        (12, 'Standard truck frame', 1, 4.0, 1.0, 150.0, 4.0, 15.0, 0.0),
        (13, 'Standard battle suit', 1, 0.0, 0.1, 40.0, 0.5, 6.0, 0.0),
        -- Weapons
        (21, 'Standard tank cannon', 2, 3.0, 4.0, 200.0, 30.0, 4.0, 2),
        (22, 'Plasma cannon', 2, 1.0, 1.0, 120.0, 5.0, 1.0, 4),
        (23, 'Particle beam gun', 2, 0.1, 0.1, 50.0, 2.0, 0.1, 6),
        -- Generators
        (31, 'Standard tank generator', 3, 4.0, 6.0, 150.0, 4.0, 0.0, 0.0),
        (32, 'Standard truck generator', 3, 1.0, 1.0, 40.0, 1.0, 0.0, 0.0),
        (33, 'Portable generator', 3, 0.1, 0.2, 5.0, 0.2, 0.0, 0.0),
        -- Engines
        (41, 'Standard tank enigne', 4, 4.0, 6.0, 100.0, 4.0, 0.0, 0.0),
        (42, 'Standard truck engine', 4, 2.0, 2.0, 60.0, 2.0, 0.0, 0.0),
        (43, 'Battle-suit power converter', 4, 0.1, 0.1, 7.0, 0.1, 0.1, 0.0),
        -- Drives
        (51, 'Standard tank drive tracks', 5, 4.0, 3.0, 60.0, 4.0, 25.0, 1.0),
        (52, 'Standard truck drive wheels', 5, 1.0, 1.0, 20.0, 2.0, 12.0, 10.0),
        (53, 'Battle-suit leg assists', 5, 0.1, 0.1, 7.0, 0.1, 0.5, 2.0),
        -- ** Power-ups **
        (111, 'Grav-assist', 11, 1.0, 0.0, 300.0, 0.4, 0.5, 0.0),
        (121, 'Fusion generator', 12, 1.0, 2.0, 400.0, 2.0, 10.0, 0.0),
        (131, 'Deflector shield', 13, 1.0, 2.0, 300.0, 1.0, 100.0, 0.5),
        (132, 'Automated repair kit', 13, 1.0, 1.0, 200.0, 0.5, 0.0, 1.0);
END