package edu.unh.cs.cs619.bulletzone.game.events;

import android.util.Log;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.TankController;

public class RestrictionEvent extends ExecutableEvent{
    /**
     * an event that can be executed
     *
     * @param gridEvent the rest object to derive info from
     */
    public RestrictionEvent(GridEvent gridEvent) {
        super(gridEvent);
    }

    public void execute(Bus bus){

        Log.d("Restriction Event", "Execution");
        Log.d("Restriction Event", "Restrictions: " + restriction[0] + restriction[1] + restriction[2]);
        Log.d("Restriction Event", "Current Orientation: " + TankController.getTankController().getTankOrientation());

        TankController.getTankController().buttonStateHandler(restriction);

    }


}
