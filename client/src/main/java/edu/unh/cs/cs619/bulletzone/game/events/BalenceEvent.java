package edu.unh.cs.cs619.bulletzone.game.events;

import android.util.Log;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.rest.BalenceUpdate;
import edu.unh.cs.cs619.bulletzone.rest.ResourceEvent;
import edu.unh.cs.cs619.bulletzone.util.IntArayWrapper;

public class BalenceEvent extends ExecutableEvent {

    public BalenceEvent(GridEvent event) {
        super(event);
    }

    @Override
    public void execute(Bus bus) {
        Log.d("Balance", String.valueOf(pos));
        if (TankController.getTankController().containsTankID(ID.longValue()))
        {
            bus.post(new BalenceUpdate(pos));
        }
    }
}
