package edu.unh.cs.cs619.bulletzone.game;

import android.util.Log;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.EBean;

import edu.unh.cs.cs619.bulletzone.game.tiles.BlankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;

@EBean
public class BoardView {

    /**
     *
     * @return the tiles
     */
    public BlankTile[] getTiles() {
        return tiles;
    }

    /**
     *
     * @param tiles tiles
     */
    public void setTiles(BlankTile[] tiles) {
        this.tiles = tiles;
    }

    public BlankTile[] tiles;
    public int[][] tileInput;
    public TileFactory tileFactory;

    /**
     *
     * @return return gridAdapter
     */
    public GridAdapter getGridAdapter() {
        return gridAdapter;
    }

    /**
     *
     * @param gridAdapter setGridAdapter
     */
    public void setGridAdapter(GridAdapter gridAdapter) {
        this.gridAdapter = gridAdapter;
    }

    public GridAdapter gridAdapter;

    /**
     * Create boardview
     */
    public BoardView() {
        tileFactory = TileFactory.getFactory();
        tiles = new BlankTile[256];

    }

    /**
     *
     * @param index index to get tile
     * @return get build
     */
    public BlankTile getTile(int index) {
        return tiles[index];
    }

    /**
     *
     * @return 16
     */
    public int getNumRows() {
        return 16;
    }

    /**
     *
     * @return 16
     */
    public int getNumCols() {
        return 16;
    }

    /**
     *
     * @param index index
     * @param cell cell
     */
    public void setCell(int index, BlankTile cell) {
        tiles[index] = cell;
    }

    /**
     *
     * @return size of view
     */
    public int size() {
        return 256;
    }

    /**
     *
     * @param arr array to set the value
     */
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

    /**
     * Subscribes to update
     */
    private Object tileEventHandler = new Object()
    {
        @Subscribe
        public void onTileUpdate(TileUpdateEvent event) {
            updateTile(event);
        }
    };

    /**
     *
     * @param event update specific tile
     */
    private void updateTile(TileUpdateEvent event) {
        tiles[event.location] = event.movedTile;
        gridAdapter.updateList(tiles);

    }


}
