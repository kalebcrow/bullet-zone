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

    @ViewById
    protected TextView textViewGarage;

    @Bean
    TankController tankController;

    @Bean
    BusProvider busProvider;

    @NonConfigurationInstance
    @Bean
    GridPollerTask gridPollTask;

    @RestService
    BulletZoneRestClient restClient;

    @Bean
    BZRestErrorhandler bzRestErrorhandler;

    @Bean
    BoardView boardView;


    /**
     * Remote tank identifier
     */
    private long tankId = -1;

    /**
     * User logged in status
     */
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Shake implementation from: https://demonuts.com/android-shake-detection/
        Intent intent = new Intent(this, ShakeService.class);
        startService(intent);
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


    protected void afterViewInjection() {
        joinAsync();
        SystemClock.sleep(500);
        gridView.setAdapter(mGridAdapter);
        tankController.setRestClient(restClient);
    }

    @AfterInject
    void afterInject() {
        restClient.setRestErrorHandler(bzRestErrorhandler);
        busProvider.getEventBus().register(gridEventHandler);
    }

    @Background
    void joinAsync() {
        try {
            tankId = restClient.join().getResult();
            tankController.setTankID(tankId);

            gridPollTask.doPoll();
        } catch (Exception e) {
        }
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
    void moveAsync(long tankId, byte direction) {
        restClient.move(tankId, direction);
    }

    @Background
    void turnAsync(long tankId, byte direction) {
        restClient.turn(tankId, direction);
    }

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
        buttonRespawn.setVisibility(View.VISIBLE);
        buttonLeft.setVisibility(View.VISIBLE);
        buttonFire.setVisibility(View.VISIBLE);
        buttonUp.setVisibility(View.VISIBLE);
        buttonDown.setVisibility(View.VISIBLE);
        buttonRight.setVisibility(View.VISIBLE);
        buttonJoin.setVisibility(View.INVISIBLE);

        //R.id.buttonLeft
        //tankId = restClient.join().getResult();
        //tankController.setTankID(tankId);
        //gridPollTask.doPoll();
    }

    @Click(R.id.buttonRespawn)
    protected void onButtonRespawn(){
        afterViewInjection();
    }

    @Click(R.id.buttonFire)
    @Background
    protected void onButtonFire() {
        restClient.fire(tankId);
    }



    @Click(R.id.buttonLeave)
    @Background
    void leaveGame() {
        String message = "leaveGame() called, tank ID: "+tankId;

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
                System.out.println("leaveGame() called, tank ID: "+tankId);
                BackgroundExecutor.cancelAll("grid_poller_task", true);
                restClient.leave(tankId);
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
                loggedIn = true;
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

    @Background
    void leaveAsync(long tankId) {
        String message = "leaveAsync() called, tank ID: "+tankId;

        // ensures user wants to leave before leaving
        leaveDialog(message);
    }
}