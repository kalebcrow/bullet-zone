package edu.unh.cs.cs619.bulletzone.game;

import android.util.Log;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.EBean;

import edu.unh.cs.cs619.bulletzone.game.tiles.BlankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;

@EBean
public class BoardView {
    public BlankTile[] getTiles() {
        return tiles;
    }

    public void setTiles(BlankTile[] tiles) {
        this.tiles = tiles;
    }

    public BlankTile[] tiles;
    public int[][] tileInput;
    public TileFactory tileFactory;

    public GridAdapter getGridAdapter() {
        return gridAdapter;
    }

    public void setGridAdapter(GridAdapter gridAdapter) {
        this.gridAdapter = gridAdapter;
    }

    public GridAdapter gridAdapter;
    

    public BoardView() {
        tileFactory = TileFactory.getFactory();
        tiles = new BlankTile[256];

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

    private Object tileEventHandler = new Object()
    {
        @Subscribe
        public void onTileUpdate(TileUpdateEvent event) {
            updateTile(event);
        }
    };

    private void updateTile(TileUpdateEvent event) {
        tiles[event.location] = event.movedTile;
        gridAdapter.updateList(tiles);

    }


}
