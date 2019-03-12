package edu.unh.cs.cs619.bulletzone.datalayer;

enum ItemType {
    GarageBay(1), StorageContainer(2),
    StandardTankFrame(11), StandardTruckFrame(12), StandardBattleSuit(13),
    StandardTankCannon(21), PlasmaCannon(22), ParticleBeamGun(23),
    StandardTankGenerator(31), StandardTruckGenerator(32), PortableGenerator(33),
    StandardTankEnigne(41), StandardTruckEngine(42), Battle_suitPowerConverter(43),
    StandardTankDriveTracks(51), StandardTruckDriveWheels(52), Battle_suitLegAssists(53),
    Grav_assist(111), FusionGenerator(121), DeflectorShield(131), AutomatedRepairKit(132)
    ;

    private final int itemTypeID;

    private final int minContainer = 0;
    private final int minWeapon = 20;
    private final int minGenerator = 30;
    private final int minEngine = 40;
    private final int minDrive = 50;
    private final int minReserved = 60;
    private final int minPowerup = 100;

    ItemType(int type) {
        itemTypeID = type;
    }

    public boolean isContainer() {
        return (minContainer < itemTypeID && itemTypeID < minWeapon);
    }

    public boolean isWeapon() {
        return (minWeapon <= itemTypeID && itemTypeID < minGenerator);
    }

    public boolean isGenerator() {
        return (minGenerator <= itemTypeID && itemTypeID < minEngine);
    }

    public boolean isEngine() {
        return (minEngine <= itemTypeID && itemTypeID < minDrive);
    }

    public boolean isDrive() {
        return (minDrive <= itemTypeID && itemTypeID < minReserved);
    }

    public boolean isPowerup() {
        return (minPowerup <= itemTypeID);
    }

    public String niceName() {
        String r = this.name().replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
            );
        return r.replaceAll("_", "-");
    }
}
