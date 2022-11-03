package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

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

import java.io.Serializable;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.BoardView;
import edu.unh.cs.cs619.bulletzone.game.CommandInterpreter;
import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.rest.BZRestErrorhandler;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.rest.GridPollerTask;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

@EActivity(R.layout.activity_replay2)
public class ReplayActivity extends Activity {

    private static final String TAG = "ClientActivity";

    @Bean
    protected GridAdapter mGridAdapter;

    @ViewById
    protected GridView gridView;

    @ViewById
    protected TextView textViewGarage;

    @Bean
    TankController tankController;

    @Bean
    BusProvider busProvider;

    @NonConfigurationInstance
    @Bean
    GridPollerTask gridPollTask;


    @Bean
    BoardView boardView;

    @Bean
    CommandInterpreter commandInterpreter;

    /**
     * Remote tank identifier
     */
    //private long tankId = -1;

    /**
     * Creates the instance, and starts the shake service.
     *
     * @param savedInstanceState the saved instance state
     */
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

    /**
     * afterViewInjection: Sets up REST client and links gridview to gridAdapter
     */
    protected void afterViewInjection() {
        joinAsync();
        SystemClock.sleep(500);
        gridView.setAdapter(mGridAdapter);
        //tankController.setRestClient(restClient);
    }

    /**
     * afterInject: Registers gridEventHandler to evenBus and
     * sets an errorHandler for REST client
     */
    @AfterInject
    void afterInject() {
        tankController.afterInject();
        busProvider.getEventBus().register(gridEventHandler);
    }

    /**
     * joinAsync: Sends the join request to server and saves returning tankID
     */
    @Background
    void joinAsync() {
        tankController.joinGame();
        gridPollTask.doPoll();
    }

    /**
     * updateGrid: Updates the local grid using grid from
     * argument gridWrapper
     * @param gw
     */
    public void updateGrid(GridWrapper gw) {
        boardView.setUsingJSON(gw.getGrid());
        mGridAdapter.updateList(boardView.getTiles());
        boardView.setGridAdapter(mGridAdapter);
    }

    /**
     * moveAsync: Background movement request
     * @param direction
     */
    @Background
    void moveAsync(byte direction) {
        tankController.move(direction);
    }

    /**
     * turnAsync: Background turn request
     * @param direction
     */
    @Background
    void turnAsync(byte direction) {
        tankController.move(direction);
    }


    /**
     * login: Opens login activity via "login" button press
     */
    @Click(R.id.buttonLogin)
    void login() {
        Intent intent = new Intent(this, AuthenticateActivity_.class);
        startActivityForResult(intent, 1);
    }

    /**
     * Get output from closing authenticate activity and logging in
     *
     * @param requestCode The request code
     * @param resultCode The result code
     * @param data The intent data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // https://stackoverflow.com/questions/14292398/how-to-pass-data-from-2nd-activity-to-1st-activity-when-pressed-back-android
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                setGarageTextView(data);
            }
        }
    }

    /**
     * Set the garage text view with the user balance and garage.
     *
     * @param data The intent data
     */
    private void setGarageTextView(Intent data) {
        Bundle bundle = data.getExtras();
        long userID = bundle.getLong("userID");
        long bankAccountBalance = bundle.getLong("bankAccountBalance");
        String tank = bundle.getString("items");
        String message = "User ID: " + userID + "\n" +
                "Balance: " + bankAccountBalance + "\n" +
                "Garage: " + tank;
        textViewGarage.setText(message);
        Log.d("MESSAGE", message);
    }
}
