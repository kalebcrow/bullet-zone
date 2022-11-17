package edu.unh.cs.cs619.bulletzone.replay;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.game.events.GridEvent;

public class HistoryWriter {

    LinkedList<GridEvent> history;
    Context context;
    int[][][] array;

    public HistoryWriter(LinkedList<GridEvent> history, int[][][] array, Context context) {
        this.array = array;
        this.history = history;
        this.context = context;
        this.WriteHistory();
    }

    public void WriteHistory() {
        Gson gson = new Gson();
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("events.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(gson.toJson(history));
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("tiles.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(gson.toJson(array));
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("tanks.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(TankController.getTankController().getTankID().toString());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
