package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.BackgroundExecutor;

import java.util.Objects;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.BoardView;
import edu.unh.cs.cs619.bulletzone.game.BulletList;
import edu.unh.cs.cs619.bulletzone.game.CommandInterpreter;
import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.replay.HistoryWriter;
import edu.unh.cs.cs619.bulletzone.rest.GridPollerTask;
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

    private Spinner mBoardSpinner;

    @ViewById
    protected TextView textViewGarage;

    protected TextView textViewMoveTo;

    @Bean
    TankController tankController;

    @Bean
    BusProvider busProvider;

    @NonConfigurationInstance
    @Bean
    GridPollerTask gridPollTask;

    private int selectedCoordinates = -1;

    @Bean
    BoardView boardView;

    @Bean
    CommandInterpreter commandInterpreter;

    Button buttonAction;

    boolean loggedIn = false;

    /**
     * Remote tank identifier
     */
    //private long tankId = -1;

    /**
     * User identifier
     */
    private String username = "";
    // TODO make work
    boolean testing = true;

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

    @Override
    protected void onStop() {
        super.onStop();
        commandInterpreter.pause();
        if (commandInterpreter.getEventHistory().size() != 0) {
            // TODO array[1] refers to the entities only (not terrain)
            HistoryWriter historyWriter = new HistoryWriter(commandInterpreter.getEventHistory(), boardView.tileInput, this);
        }

        BackgroundExecutor.cancelAll("grid_poller_task", true);
        gridPollTask.setPaused(true);
        commandInterpreter.clear();
    }



    /**
     * afterViewInjection: Sets up REST client and links gridview to gridAdapter
     */
    protected void afterViewInjection() {
        boardView.setGarageText(findViewById(R.id.ResourcesText));
        boardView.setHealthText(findViewById(R.id.HealthText));

        gridView.setAdapter(mGridAdapter);
        boardView.setGridAdapter(mGridAdapter);
        joinAsync();
        SystemClock.sleep(500);
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
        tankController.joinGame(username);
        gridPollTask.setPaused(false);
        //gridPollTask.doPoll();
        commandInterpreter.setPaused(false);
    }

    @Override
    protected void onResume() {
        gridPollTask.setPaused(false);
        boardView.reRegister();
        boardView.setGridAdapter(mGridAdapter);
        if (started == 1) {
            gridPollTask.doPoll();
        }
        commandInterpreter.setPaused(false);
        super.onResume();
    }

    /**
     * onButtonMove: Processes User movement requests
     * @param view
     */
    @Click({R.id.buttonUp, R.id.buttonDown, R.id.buttonLeft, R.id.buttonRight})
    protected void onButtonMove(View view) {
        // let tank move if it is on the same board that is showing
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
    void startGame() {
        // this should only work if the user if logged in
        if (!Objects.equals(username, "") || testing) {
            if (testing) {
                // set the garage anyway
                textViewGarage.setText("Using user id: " + username);
                boardView.setUsername(username);
            }
            mBoardSpinner = findViewById(R.id.boardSpinner);
            String[] boards = {"1", "2", "3"};
            ArrayAdapter bb = new ArrayAdapter(this, android.R.layout.simple_spinner_item, boards);
            bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mBoardSpinner.setAdapter(bb);

            afterViewInjection();
            Button buttonTurnLeft = findViewById(R.id.buttonTurnLeft);
            Button buttonTurnRight = findViewById(R.id.buttonTurnRight);
            buttonTurnRight.setVisibility(View.VISIBLE);
            buttonTurnLeft.setVisibility(View.VISIBLE);
            Button buttonFire = findViewById(R.id.buttonFire);
            Button buttonLeft = findViewById(R.id.buttonLeft);
            Button buttonUp = findViewById(R.id.buttonUp);
            Button buttonDown = findViewById(R.id.buttonDown);
            Button buttonRight = findViewById(R.id.buttonRight);
            //Button buttonJoin = findViewById(R.id.buttonJoin);
            Button buttonRespawn = findViewById(R.id.buttonRespawn);
            Button buttonReplay = findViewById(R.id.buttonReplay);
            Button buttonReplay1 = findViewById(R.id.buttonReplay1);
            Button testButton = findViewById(R.id.buttonTest);
            Button buttonDestroyTank = findViewById(R.id.buttonDestroyTank);
            TextView health = findViewById(R.id.HealthText);
            TextView textViewResources = findViewById(R.id.ResourcesText);
            boardView.setGarageText(textViewResources);
            boardView.setUserText(textViewGarage);
            health.setVisibility(View.VISIBLE);
            textViewResources.setVisibility(View.VISIBLE);
            buttonAction = findViewById(R.id.buttonAction);
            textViewMoveTo = findViewById(R.id.moveToTextView);
            Button moveToButton = (Button) findViewById(R.id.moveToButton);
            Spinner vehicleSpinner = (Spinner) findViewById(R.id.vehicle_spinner);
            buttonRespawn.setVisibility(View.VISIBLE);
            buttonLeft.setVisibility(View.VISIBLE);
            buttonFire.setVisibility(View.VISIBLE);
            buttonUp.setVisibility(View.VISIBLE);
            buttonDown.setVisibility(View.VISIBLE);
            buttonRight.setVisibility(View.VISIBLE);
            buttonAction.setVisibility(View.VISIBLE);
            testButton.setVisibility(View.VISIBLE);
            //buttonJoin.setVisibility(View.INVISIBLE);
            buttonReplay.setVisibility(View.VISIBLE);
            buttonReplay1.setVisibility(View.INVISIBLE);
            textViewMoveTo.setVisibility(View.VISIBLE);
            moveToButton.setVisibility(View.VISIBLE);
            buttonDestroyTank.setVisibility(View.VISIBLE);
            started = 1;

            vehicleSpinner.setVisibility(View.VISIBLE);
            String[] vehicles = {"Tank", "Miner", "Builder"};
            ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, vehicles);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            vehicleSpinner.setAdapter(aa);

            loggedIn = true;
        } else {
            textViewGarage.setText(R.string.LogInBeforePlayingMessage);
        }

    }

    /**
     * changeBoard: Changes to a selected board
     */
    @ItemSelect(R.id.boardSpinner)
    void changeBoard(boolean selected, int position){
        // once portals get implemented, something will have to change the board the tank is on
        // tankController.setBoardTankOn(gridnum);
        boardView.setCurrentBoard(position);
    }

    /**
     * onButtonRespawn: Resets client on the death of user
     */
    @Click(R.id.buttonReplay1)
    protected void onButtonReplay1(){
        commandInterpreter.pause();
        gridPollTask.setPaused(true);
        boardView.deRegister();
        commandInterpreter.clear();
        Intent intent = new Intent(this, ReplayActivity_.class);
        startActivityForResult(intent, 1);
    }

    /**
     * onButtonRespawn: Resets client on the death of user
     */
    @Click(R.id.buttonRespawn)
    protected void onButtonRespawn(){
        tankController.respawn();
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
        if (!loggedIn) {
            gridPollTask.setPaused(true);
            commandInterpreter.setPaused(false);
            BackgroundExecutor.cancelAll("PollServer", true);
            Intent intent = new Intent(this, AuthenticateActivity_.class);
            startActivityForResult(intent, 1);
            testing = false; // for some reason is not loading right now
            loggedIn = true;
            //startGame();
        }
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
                // set the text view with user info
                // also set the user id so you know if logged in or not
                Bundle bundle = data.getExtras();
                username = bundle.getString("userID");
                boardView.setUsername(username);
                setGarageTextView(bundle);
                startGame();
            }
        }
    }

    /**
     * Set the garage text view with the user balance and garage.
     *
     * @param bundle The intent data converted to a bundle
     */
    private void setGarageTextView(Bundle bundle) {
        long bankAccountBalance = bundle.getLong("bankAccountBalance");
        String tank = bundle.getString("items");
        String message = "User ID: " + username + "\n" +
                "Balance: " + bankAccountBalance + "\n";
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

    @Click(R.id.buttonAction)
    void vehicleAction(){

        if(tankController.getCurrentVehicle() == TankController.Vehicle.MINER){
            tankController.mine();
        }
        else if(tankController.getCurrentVehicle() == TankController.Vehicle.BUILDER){

            BuilderFragment myBuilderFragment = new BuilderFragment();
            myBuilderFragment.setContext(this);
            myBuilderFragment.show(this.getFragmentManager(), "MyFragment");

        }

    }

    @ItemSelect(R.id.vehicle_spinner)
    void changeTank(boolean selected, int position){

        switch (position) {
            case 0:
                tankController.setCurrentVehicle(TankController.Vehicle.TANK);
                buttonAction.setText("ACTION");
                buttonAction.setClickable(false);
                buttonAction.setAlpha(.5f);
                mBoardSpinner.setSelection(tankController.getBoardTankOn());
                break;
            case 1:
                tankController.setCurrentVehicle(TankController.Vehicle.MINER);
                buttonAction.setText("MINE");
                buttonAction.setClickable(true);
                buttonAction.setAlpha(1);
                mBoardSpinner.setSelection(tankController.getBoardTankOn());
                break;
            case 2:
                tankController.setCurrentVehicle(TankController.Vehicle.BUILDER);
                buttonAction.setText("BUILDER MENU");
                buttonAction.setClickable(true);
                buttonAction.setAlpha(1);
                mBoardSpinner.setSelection(tankController.getBoardTankOn());
                break;
        }


    }

    @ItemClick(R.id.gridView)
    void gridSelection(int position){

        selectedCoordinates = position;
        //textViewMoveTo.setText("Selected Position: [" + position/16 + ", " + position%16 + "]");
        textViewMoveTo.setText("Selected Position: " + selectedCoordinates);
        Log.d(TAG, "Grid Selection of " + position);

    }

    @Click(R.id.moveToButton)
    void moveToLocation(){
        if(selectedCoordinates == -1){
            Toast.makeText(this, "Please Select a Grid Location First!", Toast.LENGTH_LONG).show();
        }
        else{
            tankController.moveTo(selectedCoordinates);
        }
    }

    @Click(R.id.buttonTest)
    void requestTestResources(){
        tankController.requestTestResources();
    }

    @Click(R.id.buttonTurnLeft)
    void turnLeft(){
        tankController.turnLeft();
    }

    @Click(R.id.buttonTurnRight)
    void turnRight(){
        tankController.turnRight();
    }

    @Click(R.id.buttonDestroyTank)
    void destroyTank(){
        tankController.destroyTank();
    }

}
