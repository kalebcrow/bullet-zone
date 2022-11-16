package edu.unh.cs.cs619.bulletzone.game;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;
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
    public GroundTile[][] getTiles() {
        return tiles;
    }

    /**
     *
     * @param tiles tiles
     */
    public void setTiles(GroundTile[][] tiles) {
        this.tiles = tiles;
    }

    public GroundTile[][] tiles;
    public int[][][] tileInput;
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
        tiles = new GroundTile[256][2]; // represents [terrain][entity]
    }

    @AfterInject
    public void setBusProvider(){
        busProvider.getEventBus().register(tileEventHandler);
    }

    /**
     *
     * @param index index to get tile
     * @return get build
     */
    public GroundTile getTile(int index) {
        return tiles[index][1]; // defaults to one because only used to test entities?
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
    public void setCell(int index, GroundTile cell) {
        if (cell.jsonValue == 0 || cell.jsonValue == 1 || cell.jsonValue == 2) {
            tiles[index][0] = cell; // set terrain
        } else {
            tiles[index][1] = cell; // set entity
        }
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
    public void setUsingJSON(int[][][] arr) {
        this.tileInput = arr;
        int value = 0;
        for (int i = 0; i < 16; i++) {
            for (int ii = 0; ii < 16; ii++) {
                this.tiles[value][0] = this.tileFactory.makeTile(arr[i][ii][0], value); // terrain
                this.tiles[value][1] = this.tileFactory.makeTile(arr[i][ii][1], value); // entity
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
     * @param event update specific OBSTACLE/VEHICLE tile
     */
    private void updateTile(TileUpdateEvent event) {
        tiles[event.location][1] = event.movedTile;
        gridAdapter.updateList(tiles);

    }


}
