package edu.unh.cs.cs619.bulletzone.game;

import org.json.JSONArray;

import edu.unh.cs.cs619.bulletzone.game.tiles.BlankTile;

public class BoardView {
    public BlankTile[] tiles;
    public int[][] tileInput;
    public TileFactory tileFactory;
    

    public BoardView() {
        tileFactory = TileFactory.getFactory();
        tiles = new BlankTile[256];

    }

    public BoardView(int[][] input) {
        this();
        this.setUsingJSON(input);
    }

    public BlankTile getTile(int index) {
        return tiles[index];
    }

    public int getNumRows() {
        return 16;
    }

    public int getNumCols() {
        return 16;
    }

    public void setCell(int index, BlankTile cell) {
        tiles[index] = cell;
    }

    public int size() {
        return 256;
    }

    public void setUsingJSON(int[][] arr) {
        this.tileInput = arr;
        int value = 0;
        for (int i = 0; i < 16; i++) {
            for (int ii = 0; ii < 16; ii++) {
                this.tiles[value] = this.tileFactory.makeTile(arr[i][ii], value);
                value++;
            }
        }
    }
}
