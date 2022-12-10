package edu.unh.cs.cs619.bulletzone.game;

import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;

public class Board {

    public GroundTile[][] tiles;
    public TileFactory tileFactory;

    /**
     * Create boardview
     */
    public Board() {
        tileFactory = TileFactory.getFactory();
        tiles = new GroundTile[256][3]; // represents [terrain][entity][road]
    }

    /**
     *
     * @param arr array to set the value
     */
    public void setUsingJSON(int[][][] arr, int value) {
        int value1 = 0;
        for (int i = 0; i < 16; i++) {
            for (int ii = 0; ii < 16; ii++) {
                this.tiles[value1][0] = this.tileFactory.makeTile(arr[i][ii][0], value); // terrain
                this.tiles[value1][1] = this.tileFactory.makeTile(arr[i][ii][1], value); // entity
                this.tiles[value1][2] = this.tileFactory.makeTile((Integer) arr[i][ii][2], value); // road
                value1++;
                value++;
            }
        }
    }



}
