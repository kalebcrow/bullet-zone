package edu.unh.cs.cs619.bulletzone.game;

import android.util.Log;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.Arrays;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.BalenceUpdate;
import edu.unh.cs.cs619.bulletzone.rest.BoardUpdate;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;
import edu.unh.cs.cs619.bulletzone.rest.ResourceEvent;
import edu.unh.cs.cs619.bulletzone.rest.RoadUpdateEvent;
import edu.unh.cs.cs619.bulletzone.rest.TerrainUpdateEvent;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

@EBean
public class BoardView {

    @Bean
    BusProvider busProvider;

    /**
     *
     * @return the tiles
     */
    public Board[] getTiles() {
        return boards;
    }

    /**
     *
     * @param boards tiles
     */
    public void setTiles(Board[] boards) {
        this.boards = boards;
    }

    public Board[] boards;
    public int[][][] tileInput;
    public TileFactory tileFactory;
    public int[] resources; //rock iron clay
    public boolean paused;
    public int currentBoard;
    public boolean history;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String username;


    public TextView getGarageText() {
        return garageText;
    }

    public void setGarageText(TextView garageText) {
        this.garageText = garageText;
    }

    public TextView garageText;

    public TextView getHealthText() {
        return healthText;
    }

    public void setHealthText(TextView healthText) {
        this.healthText = healthText;
    }

    public TextView healthText;

    public TextView getUserText() {
        return userText;
    }

    public void setUserText(TextView userText) {
        this.userText = userText;
    }

    public TextView userText;

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
        resources = new int[4];
        boards = new Board[3]; // represents [terrain][entity][road]
        for (int i = 0; i < 3; i++) {
            boards[i] = new Board();
        }
        currentBoard = 0;
        history = false;
    }

    @AfterInject
    public void setBusProvider(){
        this.reRegister();
    }

    /**
     *
     * @param index index to get tile
     * @return get build
     */
    public GroundTile getTile(int index) {
        return boards[currentBoard].tiles[index][1]; // defaults to two because only used to test entities?
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
        if (cell.jsonValue == 0 || cell.jsonValue == 1 || cell.jsonValue == 2
                || cell.jsonValue == 3 || cell.jsonValue == 50) {
            boards[currentBoard].tiles[index][0] = cell; // set terrain
        } else {
            boards[currentBoard].tiles[index][1] = cell; // set entity
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
        // get updated grid

        int[][][] newg = new int[16][16][3];
        boards[0].setUsingJSON(Arrays.copyOfRange(arr, 0, 16), 0);
        boards[1].setUsingJSON(Arrays.copyOfRange(arr, 16, 32), 256);
        boards[2].setUsingJSON(Arrays.copyOfRange(arr, 32, 48), 512);
    }

    /**
     * Subscribes to update
     */
    private Object roadEventHandler = new Object()
    {
        @Subscribe
        public void onRoadUpdate(RoadUpdateEvent event) {
            updateRoad(event);
        }
    };

    /**
     *
     * @param event update specific OBSTACLE/VEHICLE tile
     */
    private void updateRoad(RoadUpdateEvent event) {
        boards[event.location/256].tiles[event.location%256][2] = event.movedTile; // entities are now stored in [1]
        gridAdapter.updateList(boards[currentBoard].tiles);
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
        boards[event.location/256].tiles[event.location%256][1] = event.movedTile; // entities are now stored in [1]
        gridAdapter.updateList(boards[currentBoard].tiles);
        updateHealth();
    }

    public void updateHealth() {
        StringBuilder health = new StringBuilder();
        health.append("Health\n");

        TankList tankList = TankList.getTankList();
        TankController tankController = TankController.getTankController();

        for (int i = 0; i < tankController.getTankID().length; i++) {
            TankTile tile = (TankTile) tankList.getLocation(Math.toIntExact(tankController.getTankID()[i]));
            if (tile != null) {
                health.append("TankID: ").append(tile.getID()).append(" Health ").append(tile.health).append("\n");
            }
        }

        if (healthText != null) {
            healthText.setText(health.toString());
        }
    }

    /**
     * Subscribes to update
     */
    private Object terrainEventHandler = new Object()
    {
        @Subscribe
        public void onTerrainUpdate(TerrainUpdateEvent event) {
            updateTerrain(event);
        }
    };

    private void updateTerrain(TerrainUpdateEvent event) {
        boards[event.location/256].tiles[event.location%256][0] = event.movedTile; // entities are now stored in [1]
        gridAdapter.updateList(boards[currentBoard].tiles);
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
     * Subscribes to update
     */
    private Object resourceEventHandler = new Object()
    {
        @Subscribe
        public void onUpdateResource(ResourceEvent event) {
            updateResource(event);
        }
    };

    /**
     * Subscribes to update
     */
    private Object balanceEventHandler = new Object()
    {
        @Subscribe
        public void onUpdateBalence(BalenceUpdate event) {
            updateBalence(event);
        }
    };

    private void updateBalence(BalenceUpdate event) {
        String message = "User ID: " + username + "\n" +
                "Balance: " + event.balence + "\n";

        if (userText != null) {
            userText.setText(message);
        }
    }

    /**
     * Subscribes to update
     */
    private Object updateBoard = new Object()
    {
        @Subscribe
        public void onBoardUpdate(BoardUpdate event) {
            setCurrentBoard(event.balence);
        }
    };


    /**
     *
     * @param event update specific OBSTACLE/VEHICLE tile
     */
    private void updateResource( ResourceEvent event) {
        resources = event.resources;
        String message =
                "Rock: " + this.resources[0] + "\n" +
                "Iron: " + this.resources[1] + "\n" +
                "Clay: " + this.resources[2] + "\n" +
                "Wood: " + this.resources[3];

        if (garageText != null) {
            garageText.setText(message);
        }
    }

    /**
     *
     * @param event update specific tile
     */
    private void updateGrid(GridUpdateEvent event) {
        this.setUsingJSON(event.gw.getGrid());
        if (gridAdapter != null) {
            gridAdapter.updateList(boards[currentBoard].tiles);
        }
        updateHealth();
    }

    public void deRegister() {
        busProvider.getEventBus().unregister(tileEventHandler);
        busProvider.getEventBus().unregister(gridEventHandler);
        busProvider.getEventBus().unregister(resourceEventHandler);
        busProvider.getEventBus().unregister(roadEventHandler);
        busProvider.getEventBus().unregister(balanceEventHandler);
        busProvider.getEventBus().unregister(terrainEventHandler);
        busProvider.getEventBus().register(updateBoard);

    }

    /**
     *
     */
    public void reRegister() {
        busProvider.getEventBus().register(tileEventHandler);
        busProvider.getEventBus().register(gridEventHandler);
        busProvider.getEventBus().register(resourceEventHandler);
        busProvider.getEventBus().register(roadEventHandler);
        busProvider.getEventBus().register(balanceEventHandler);
        busProvider.getEventBus().register(terrainEventHandler);
        busProvider.getEventBus().register(updateBoard);

    }

    public void setCurrentBoard(int currentBoard) {
        this.currentBoard = currentBoard;
        if (gridAdapter != null) {
            gridAdapter.updateList(boards[currentBoard].tiles);
        }
    }
}
