package edu.unh.cs.cs619.bulletzone.game;

import android.util.Log;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.tiles.BlankTile;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;

@EBean
public class BoardView {

    @Bean
    BusProvider busProvider;

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
    public boolean paused;

    /**
     *
     * @return return gridAdapter
     */
    public GridAdapter getGridAdapter() {
        TankList.getTankList().clear();
        BulletList.getBulletList().clear();
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

    @AfterInject
    public void setBusProvider(){
        busProvider.getEventBus().register(tileEventHandler);
        busProvider.getEventBus().register(gridEventHandler);
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
        Log.d("TimeDiff", "recieved event: " + System.currentTimeMillis());
        tiles[event.location] = event.movedTile;
        gridAdapter.updateList(tiles);

    }

    /**
     * Subscribes to update
     */
    private Object gridEventHandler = new Object()
    {
        @Subscribe
        public void onUpdateGrid(GridUpdateEvent event) {
            updateGrid(event);
        }
    };

    /**
     *
     * @param event update specific tile
     */
    private void updateGrid(GridUpdateEvent event) {
        this.setUsingJSON(event.gw.getGrid());
        gridAdapter.updateList(tiles);
    }

    public void deRegister() {
        busProvider.getEventBus().unregister(tileEventHandler);
        busProvider.getEventBus().unregister(gridEventHandler);
    }

    /**
     *
     */
    public void reRegister() {
        busProvider.getEventBus().register(tileEventHandler);
        busProvider.getEventBus().register(gridEventHandler);
    }
}
