package edu.unh.cs.cs619.bulletzone.rest;

import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;

public class TerrainUpdateEvent {
    public Integer location;
    public GroundTile movedTile;

    public TerrainUpdateEvent(Integer location, GroundTile movedTile) {
        this.location = location;
        this.movedTile = movedTile;
    }
}
