package edu.unh.cs.cs619.bulletzone.rest;

import android.os.SystemClock;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.rest.spring.annotations.RestService;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.EventWrapper;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by simon on 10/3/14.
 */
@EBean
public class GridPollerTask {
    private static final String TAG = "PollServer";

    // Injected object
    @Bean
    BusProvider busProvider;

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    boolean paused = false;

    @RestService
    BulletZoneRestClient restClient;

    GridWrapper gw;
    long timestamp;

    @Background(id = "grid_poller_task")
    // TODO: disable trace
    // @Trace(tag="CustomTag", level=Log.WARN)
    public void doPoll() {
        long timestamp;
        GridWrapper gw = restClient.grid();
        if (!paused) {
            onGridUpdate(restClient.grid());
        }
        timestamp = gw.getTimeStamp();
        while (!paused) {
            EventWrapper hw = restClient.event(timestamp);
            timestamp = hw.getTimeStamp();
            onCommandHistoryUpdate(hw);
            // poll server every 500ms
            SystemClock.sleep(500);
        }
    }

    /**
     * Change the board to the requested board
     *
     * @param numBoard the number board requested
     */
    @Background(id = "grid_poller_change_board")
    public void changeBoard(int numBoard) {
        // get updated grid
        GridWrapper newGW = restClient.grid();
        int[][][] g = newGW.getGrid();

        // update the grid to show the different indices of it
        int[][][] newg = new int[16][16][3];
        if (numBoard == 0) {
            newg = Arrays.copyOfRange(g, 0, 16);
        } else if (numBoard == 1) {
            newg = Arrays.copyOfRange(g, 16, 32);
        } else if (numBoard == 2) {
            newg = Arrays.copyOfRange(g, 32, 48);
        }

        newGW.setGrid(newg);
        onGridUpdate(newGW);
    }

    @UiThread
    public void onGridUpdate(GridWrapper gw) {
        busProvider.getEventBus().post(new GridUpdateEvent(gw));
    }

    @UiThread
    public void onCommandHistoryUpdate(EventWrapper hw) {
        busProvider.getEventBus().post(new HistoryUpdateEvent(hw));
    }
}
