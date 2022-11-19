package edu.unh.cs.cs619.bulletzone.rest;

import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;

public class RoadUpdateEvent {
    public Integer location;
    public GroundTile movedTile;

    public RoadUpdateEvent(Integer location, GroundTile movedTile) {
        this.location = location;
        this.movedTile = movedTile;
    }
}
