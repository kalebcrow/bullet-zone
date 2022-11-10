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
import edu.unh.cs.cs619.bulletzone.replay.HistoryWriter;
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

    public int started = 0;

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
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        commandInterpreter.pause();
        if (commandInterpreter.getEventHistory().size() != 0) {
            HistoryWriter historyWriter = new HistoryWriter(commandInterpreter.getEventHistory(), boardView.tileInput, this);
        }

        gridPollTask.setPaused(true);
        commandInterpreter.clear();
    }

    /**
     * afterViewInjection: Sets up REST client and links gridview to gridAdapter
     */
    protected void afterViewInjection() {
        joinAsync();
        SystemClock.sleep(500);
        gridView.setAdapter(mGridAdapter);
        boardView.setGridAdapter(mGridAdapter);
        commandInterpreter.setPaused(false);
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
     * joinAsync: Sends the join request to server and saves returning tankID
     */
    @Background
    void joinAsync() {
        tankController.joinGame();
        gridPollTask.setPaused(false);
        gridPollTask.doPoll();
        commandInterpreter.setPaused(false);
    }

    @Override
    protected void onRestart() {
        gridPollTask.setPaused(false);
        boardView.reRegister();
        if (started == 1) {
            gridPollTask.doPoll();
        }
        commandInterpreter.setPaused(false);
        super.onRestart();
    }

    /**
     * onButtonMove: Processes User movement requests
     * @param view
     */
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

    /**
     * startGame: Initializes view when join game is selected
     */
    @Click(R.id.buttonJoin)
    void startGame() {
        afterViewInjection();
        Button buttonFire = findViewById(R.id.buttonFire);
        Button buttonLeft = findViewById(R.id.buttonLeft);
        Button buttonUp = findViewById(R.id.buttonUp);
        Button buttonDown = findViewById(R.id.buttonDown);
        Button buttonRight = findViewById(R.id.buttonRight);
        Button buttonJoin = findViewById(R.id.buttonJoin);
        Button buttonRespawn = findViewById(R.id.buttonRespawn);
        Button buttonReplay = findViewById(R.id.buttonReplay);
        Button buttonReplay1 = findViewById(R.id.buttonReplay1);
        buttonRespawn.setVisibility(View.VISIBLE);
        buttonLeft.setVisibility(View.VISIBLE);
        buttonFire.setVisibility(View.VISIBLE);
        buttonUp.setVisibility(View.VISIBLE);
        buttonDown.setVisibility(View.VISIBLE);
        buttonRight.setVisibility(View.VISIBLE);
        buttonJoin.setVisibility(View.INVISIBLE);
        buttonReplay.setVisibility(View.VISIBLE);
        buttonReplay1.setVisibility(View.INVISIBLE);
        started = 1;
        //R.id.buttonLeft
        //tankId = restClient.join().getResult();
        //tankController.setTankID(tankId);
        //gridPollTask.doPoll();
    }

    /**
     * onButtonRespawn: Resets client on the death of user
     */
    @Click(R.id.buttonReplay1)
    protected void onButtonReplay1(){
        boardView.deRegister();
        Intent intent = new Intent(this, ReplayActivity_.class);
        startActivityForResult(intent, 1);
    }

    /**
     * onButtonRespawn: Resets client on the death of user
     */
    @Click(R.id.buttonRespawn)
    protected void onButtonRespawn(){
        afterViewInjection();
    }

    /**
     * onButtonRespawn: Resets client on the death of user
     */
    @Click(R.id.buttonReplay)
    protected void onButtonReplay(){
        commandInterpreter.pause();
        gridPollTask.setPaused(true);
        HistoryWriter historyWriter = new HistoryWriter(commandInterpreter.getEventHistory(), boardView.tileInput, this);
        commandInterpreter.clear();
        Intent intent = new Intent(this, ReplayActivity_.class);
        startActivityForResult(intent, 1);
    }

    /**
     * onButtonFire: Sends REST request to server upon the user
     * pressing the fire button
     */
    @Click(R.id.buttonFire)
    @Background
    protected void onButtonFire() {
        //restClient.fire(tankId);
        tankController.fire();
    }

    /**
     * leaveGame: User-triggered leaveGame request via button that sends
     * via REST
     */
    @Click(R.id.buttonLeave)
    @Background
    void leaveGame() {
        String message = "leaveGame() called";

        // ensures user wants to leave the game before leaving the game
        leaveDialog(message);
    }

    /**
     * Check user wants to leave the game
     *
     * @param message
     */
    private void leaveDialog(String message) {
        // build the alertdialog with yes and no buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //System.out.println("leaveGame() called, tank ID: "+tankId);
                tankController.leaveGame();
                finish();
                //restClient.leave(tankId);
            }
        });
        builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.out.println("leaveGame() cancelled");
            }
        });

        // Create the alertdialog
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = builder.create();
                dialog.setMessage("Are you sure you want to leave the game?");
                dialog.show();
            }
        });
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
     * leaveAsync: Sends background leave game request for client, to
     * server.
     * @param tankId
     */
    @Background
    void leaveAsync(long tankId) {
        String message = "leaveAsync() called, tank ID: "+tankId;

        // ensures user wants to leave before leaving
        leaveDialog(message);
    }
}
