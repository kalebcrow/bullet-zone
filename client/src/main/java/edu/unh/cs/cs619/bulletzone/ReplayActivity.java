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
import android.widget.Toast;

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
    GridAdapter mGridAdapter;

    @ViewById
    protected GridView gridView;

    @ViewById
    protected TextView textViewGarage;
    protected TextView health;

    @Bean
    BusProvider busProvider;

    @Bean
    BoardView boardView;

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
    }

    @Override
    protected void onDestroy() {
        boardView.deRegister();
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
        setGarageTextView();
        if (historyReader.array == null) {
            CharSequence text = "No Replay";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        } else {
            // TODO array[1] refers to the entities only (not terrain)
            boardView.setUsingJSON(historyReader.array);
            boardView.setGarageText(findViewById(R.id.textViewGarage));
            boardView.setHealthText(findViewById(R.id.HealthText));
            mGridAdapter.updateList(boardView.getTiles());
            historyInterpreter.setEventHistory(historyReader.history);
            running = 0;
        }

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
                setGarageTextView();
            }
        }
    }

    /**
     * Set the garage text view with the user balance and garage.
     *
     * @param data The intent data
     */
    private void setGarageTextView() {
        String message =
                "Rock: " + 0 + "\n" +
                        "Iron: " + 0 + "\n" +
                        "Clay: " + 0;

        textViewGarage.setText(message);
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
        boardView.setGridAdapter(mGridAdapter);
        if (running == 1) {
            historyInterpreter.Resume();
        } else {
            historyInterpreter.setPaused(false);
            historyInterpreter.start(); //<-- put your code in here.
            running =1;
        }
    }

    /**
     * onButtonFire: Sends REST request to server upon the user
     * pressing the fire button
     */
    @Click(R.id.buttonSpeedUp)
    protected void onButtonSpeedUp() {
        historyInterpreter.speedUp();
    }

    /**
     * onButtonFire: Sends REST request to server upon the user
     * pressing the fire button
     */
    @Click(R.id.buttonSlowDown)
    protected void onButtonSlowDown() {
        historyInterpreter.speedDown();
    }


}
