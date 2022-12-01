package edu.unh.cs.cs619.bulletzone.model.Entities.Tanks;

import static edu.unh.cs.cs619.bulletzone.model.Miscellaneous.Direction.toByte;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.events.DamageEvent;
import edu.unh.cs.cs619.bulletzone.events.DestroyTankEvent;
import edu.unh.cs.cs619.bulletzone.events.MineEvent;
import edu.unh.cs.cs619.bulletzone.events.MoveTankEvent;
import edu.unh.cs.cs619.bulletzone.events.balanceEvent;
import edu.unh.cs.cs619.bulletzone.model.Entities.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Entities.GameResources.FieldResource;
import edu.unh.cs.cs619.bulletzone.model.Entities.GameResources.Thingamajig;
import edu.unh.cs.cs619.bulletzone.model.Miscellaneous.Direction;
import edu.unh.cs.cs619.bulletzone.model.Miscellaneous.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Miscellaneous.Game;

public class BaseTank extends Tank {
    // typeIndex 0 for tank, 1 for miner, 2 for builder
    private final int[] allowedMoveIntervals = {500,800,1000};
    private final int[] allowedTurnIntervals = {500,800,300};
    private final int[] allowedFireIntervals = {1500,200,1000};
    private final int[] allowedNumbersOfBullets = {2,4,6};
    private final int[] healths = {100,300,80};

    public BaseTank(String username, long id, Direction direction, String ip, int typeIndex) {
        this.id = id;
        this.username = username;
        this.direction = direction;
        this.ip = ip;
        this.typeIndex = typeIndex;
        this.life = healths[typeIndex];
        if (typeIndex == 1) {
            resources = new int[]{0,0,0};
        }
    }

    public BaseTank(){
        ip = null;
        id = 0;
        typeIndex = -1;
    }

    @Override
    public FieldEntity copy() {
        return new BaseTank(username, id, direction, ip, typeIndex);
    }
    public long getAllowedMoveInterval() { return allowedMoveIntervals[typeIndex]; }
    public long getAllowedTurnInterval() { return allowedTurnIntervals[typeIndex]; }
    public long getAllowedFireInterval() { return allowedFireIntervals[typeIndex]; }
    public int getAllowedNumberOfBullets() { return allowedNumbersOfBullets[typeIndex]; }

    @Override
    public Tank strip(){
        return this;
    }

}

