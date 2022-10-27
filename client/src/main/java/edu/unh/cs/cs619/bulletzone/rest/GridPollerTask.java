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

/**
 * Created by simon on 10/3/14.
 */
@EBean
public class GridPollerTask {
    private static final String TAG = "PollServer";

    // Injected object
    @Bean
    BusProvider busProvider;

    @RestService
    BulletZoneRestClient restClient;

    @Background(id = "grid_poller_task")
    // TODO: disable trace
    // @Trace(tag="CustomTag", level=Log.WARN)
    public void doPoll() {
        long timestamp;
        GridWrapper gw = restClient.grid();
        onGridUpdate(restClient.grid());
        timestamp = gw.getTimeStamp();
        while (true) {

            EventWrapper hw = restClient.getHistory(timestamp);
            timestamp = hw.getTimeStamp();
            onCommandHistoryUpdate(hw);
            // poll server every 100ms
            SystemClock.sleep(100);
        }
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
