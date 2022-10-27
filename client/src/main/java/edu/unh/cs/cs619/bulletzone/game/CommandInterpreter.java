package edu.unh.cs.cs619.bulletzone.game;


import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.UiThread;

import java.util.LinkedList;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.events.GridEvent;
import edu.unh.cs.cs619.bulletzone.rest.HistoryUpdateEvent;
import edu.unh.cs.cs619.bulletzone.util.EventWrapper;

public class CommandInterpreter {

    private static volatile CommandInterpreter INSTANCE = null;

    // Injected object
    BusProvider busProvider;

    public EventWrapper ew;

    private CommandInterpreter() {

    }

    public static CommandInterpreter getCommandInterpreter() {
        if(INSTANCE == null) {
            synchronized (CommandInterpreter.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CommandInterpreter();
                }
            }
        }
        return INSTANCE;
    }

    private Object tileEventHandler = new Object()
    {
        @Subscribe
        public void onHistoryUpdateEvent(HistoryUpdateEvent event) {
            updateBoard(event);
        }
    };

    private void updateBoard(HistoryUpdateEvent event) {
        this.ew = event.getHw();
        LinkedList<GridEvent> history = ew.getUpdate();

        for (int i = 0; i < history.size(); i++) {
            GridEvent currEvent = history.get(i);
        }
    }
}
