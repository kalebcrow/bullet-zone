package edu.unh.cs.cs619.bulletzone.rest;


import edu.unh.cs.cs619.bulletzone.game.tiles.BlankTile;

/**
 * Created by simon on 10/3/14.
 */
public class TileUpdateEvent {
    public Integer location;
    public BlankTile movedTile;
    public Integer prevLocation;

    public TileUpdateEvent(Integer prevLocation, Integer location, BlankTile movedTile) {
        this.location = location;
        this.prevLocation = prevLocation;
        this.movedTile = movedTile;
    }
}