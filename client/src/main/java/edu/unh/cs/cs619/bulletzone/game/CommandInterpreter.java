package edu.unh.cs.cs619.bulletzone.game;


import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.util.LinkedList;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.events.DestroyTankEvent;
import edu.unh.cs.cs619.bulletzone.game.events.DestroyWallEvent;
import edu.unh.cs.cs619.bulletzone.game.events.ExecutableEvent;
import edu.unh.cs.cs619.bulletzone.game.events.FireEvent;
import edu.unh.cs.cs619.bulletzone.game.events.GridEvent;
import edu.unh.cs.cs619.bulletzone.game.events.MoveBulletEvent;
import edu.unh.cs.cs619.bulletzone.game.events.MoveTankEvent;
import edu.unh.cs.cs619.bulletzone.game.events.TurnEvent;
import edu.unh.cs.cs619.bulletzone.rest.HistoryUpdateEvent;
import edu.unh.cs.cs619.bulletzone.util.EventWrapper;

@EBean
public class CommandInterpreter {

    private static volatile CommandInterpreter INSTANCE = null;

    // Injected object
    BusProvider busProvider;

    public EventWrapper ew;

    /**
     * Command interpreter
     */
    public CommandInterpreter() {

    }

    /**
     *
     * @return Gets command interpreter
     */
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

    /**
     *
     * @param event event
     */
    private void updateBoard(HistoryUpdateEvent event) {
        this.ew = event.getHw();
        LinkedList<GridEvent> history = ew.getUpdate();

        for (int i = 0; i < history.size(); i++) {
            GridEvent currEvent = history.get(i);
            interpret(currEvent).execute();

        }
    }

     private ExecutableEvent interpret(GridEvent currEvent) {
         ExecutableEvent event;
        switch (currEvent.getType()) {
             case "moveTank":
                 event = new MoveTankEvent(currEvent);
                 break;
             case "moveBullet":
                 event = new MoveBulletEvent(currEvent);
                 break;
             case "destroyTank":
                 event = new DestroyTankEvent(currEvent);
                 break;
             case "destroyWall":
                 event = new DestroyWallEvent(currEvent);
                 break;
             case "fire":
                 event = new FireEvent(currEvent);
                 break;
             case "turnEvent":
                 event = new TurnEvent(currEvent);
                 break;
            default:
                event = new ExecutableEvent(currEvent);
         }

        return event;
     }
}
