package edu.unh.cs.cs619.bulletzone.rest;

import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;

/**
 * Created by simon on 10/3/14.
 */
public class TileUpdateEvent {
    public Integer location;
    public GroundTile movedTile;

    public TileUpdateEvent(Integer location, GroundTile movedTile) {
        this.location = location;
        this.movedTile = movedTile;
    }
}