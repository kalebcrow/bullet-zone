package edu.unh.cs.cs619.bulletzone.datalayer;

interface PermissionTarget {
    PermissionTargetType getPermissionType();

    int getId();

    void setOwningUser(GameUser user);
    GameUser getOwningUser();
}
