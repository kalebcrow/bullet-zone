package edu.unh.cs.cs619.bulletzone.game.events;

import android.util.Log;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.tiles.ItemTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.ResourceTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.ResourceEvent;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;
import edu.unh.cs.cs619.bulletzone.util.IntArayWrapper;

public class AddResourceEvent extends ExecutableEvent {
    public AddResourceEvent(GridEvent event) {
        super(event);
    }

    @Override
    public void execute(Bus bus) {
        int jsonVal = convert(resource);
        Log.d("Yeah dog", resource);

        if(jsonVal > 3000){
            bus.post(new TileUpdateEvent(pos, new ItemTile(jsonVal, pos)));
        }else{
            bus.post(new TileUpdateEvent(pos, new ResourceTile(jsonVal, pos)));
        }

    }

    private int convert(String resource) {
        switch (resource) {
            case "CB":
                return 501;
            case "TB":
                return 7;
            case "IB":
                return 503;
            case "WB":
                return 504;
            case "RB":
                return 502;
            case "FG":
                return 3121;
            case "GA":
                return 3111;
            case "DS":
                return 3122;
            case "RK":
                return 3123;
        }
        return -1;
    }
}
