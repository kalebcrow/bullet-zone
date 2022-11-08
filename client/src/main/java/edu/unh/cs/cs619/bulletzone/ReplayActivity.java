package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import edu.unh.cs.cs619.bulletzone.replay.HistoryInterpreter;
import edu.unh.cs.cs619.bulletzone.replay.HistoryReader;
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

    @Bean
    HistoryInterpreter historyInterpreter;

    int running;

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
    }

    /**
     * afterViewInjection: Sets up REST client and links gridview to gridAdapter
     */
    @AfterViews
    void afterViews() {
        SystemClock.sleep(500);
        HistoryReader historyReader = new HistoryReader(this);
        gridView.setAdapter(mGridAdapter);
        boardView.setUsingJSON(historyReader.array);
        mGridAdapter.updateList(boardView.getTiles());
        boardView.setGridAdapter(mGridAdapter);
        historyInterpreter.setEventHistory(historyReader.history);
        running = 0;

    }

    /**
     * afterInject: Registers gridEventHandler to evenBus and
     * sets an errorHandler for REST client
     */
    @AfterInject
    void afterInject() {
        tankController.afterInject();
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

    /**
     * onButtonFire: Sends REST request to server upon the user
     * pressing the fire button
     */
    @Click(R.id.buttonPause)
    protected void onButtonPause() {
        historyInterpreter.pause();
    }

    /**
     * onButtonFire: Sends REST request to server upon the user
     * pressing the fire button
     */
    @Click(R.id.buttonResume)
    protected void onButtonResume() {
        Button button = (Button)findViewById(R.id.buttonResume);
        button.setText("resume");
        if (running == 1) {
            historyInterpreter.Resume();
        } else {
            historyInterpreter.setPaused(false);
            historyInterpreter.start(); //<-- put your code in here.
            running =1;
        }
    }

}
