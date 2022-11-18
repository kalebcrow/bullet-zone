package edu.unh.cs.cs619.bulletzone.game.events;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.rest.ResourceEvent;
import edu.unh.cs.cs619.bulletzone.util.IntArayWrapper;

public class MineEvent extends ExecutableEvent {

    public MineEvent(GridEvent event) {
        super(event);
    }

    @Override
    public void execute(Bus bus) {
        bus.post(new ResourceEvent(new IntArayWrapper(this.resources)));
    }
}
