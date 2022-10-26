package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.annotations.RestService;
import org.androidannotations.rest.spring.api.RestClientHeaders;
import org.androidannotations.api.BackgroundExecutor;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.BoardView;
import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.rest.BZRestErrorhandler;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.rest.GridPollerTask;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

@EActivity(R.layout.activity_client)
public class ClientActivity extends Activity {

    private static final String TAG = "ClientActivity";

    @Bean
    protected GridAdapter mGridAdapter;

    @ViewById
    protected GridView gridView;

    @Bean
    TankController tankController;

    @Bean
    BusProvider busProvider;

    @NonConfigurationInstance
    @Bean
    GridPollerTask gridPollTask;


    @Bean
    BoardView boardView;

    /**
     * Remote tank identifier
     */
    //private long tankId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tankController.passContext(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        busProvider.getEventBus().unregister(gridEventHandler);
    }

    /**
     * Otto has a limitation (as per design) that it will only find
     * methods on the immediate class type. As a result, if at runtime this instance
     * actually points to a subclass implementation, the methods registered in this class will
     * not be found. This immediately becomes a problem when using the AndroidAnnotations
     * framework as it always produces a subclass of annotated classes.
     *
     * To get around the class hierarchy limitation, one can use a separate anonymous class to
     * handle the events.
     */
    private Object gridEventHandler = new Object()
    {
        @Subscribe
        public void onUpdateGrid(GridUpdateEvent event) {
            updateGrid(event.gw);
        }
    };

    @AfterViews
    protected void afterViewInjection() {
        joinAsync();
        SystemClock.sleep(500);
        gridView.setAdapter(mGridAdapter);
        //tankController.setRestClient(restClient);
    }

    @AfterInject
    void afterInject() {
        tankController.afterInject();
        busProvider.getEventBus().register(gridEventHandler);
    }

    @Background
    void joinAsync() {
        tankController.joinGame();
        gridPollTask.doPoll();
    }

    public void updateGrid(GridWrapper gw) {
        boardView.setUsingJSON(gw.getGrid());
        mGridAdapter.updateList(boardView.getTiles());
        boardView.setGridAdapter(mGridAdapter);
    }

    @Click({R.id.buttonUp, R.id.buttonDown, R.id.buttonLeft, R.id.buttonRight})
    protected void onButtonMove(View view) {
        final int viewId = view.getId();
        byte direction = 0;

        switch (viewId) {
            case R.id.buttonUp:
                direction = 0;
                break;
            case R.id.buttonDown:
                direction = 4;
                break;
            case R.id.buttonLeft:
                direction = 6;
                break;
            case R.id.buttonRight:
                direction = 2;
                break;
            default:
                Log.e(TAG, "Unknown movement button id: " + viewId);
                break;
        }
        tankController.move(direction);
    }

    @Background
    void moveAsync(byte direction) {
        tankController.move(direction);
    }

    @Background
    void turnAsync(byte direction) {
        tankController.move(direction);
    }

    @Click(R.id.buttonFire)
    @Background
    protected void onButtonFire() {
        //restClient.fire(tankId);
        tankController.fire();
    }

    @Click(R.id.buttonLeave)
    @Background
    void leaveGame() {
        BackgroundExecutor.cancelAll("grid_poller_task", true);
        //restClient.leave(tankId);
        tankController.leaveGame();
    }

    @Click(R.id.buttonLogin)
    void login() {
        Intent intent = new Intent(this, AuthenticateActivity_.class);
        startActivity(intent);
    }

    @Background
    void leaveAsync() {
        BackgroundExecutor.cancelAll("grid_poller_task", true);
        //restClient.leave(tankId);
        tankController.leaveGame();
    }
}
