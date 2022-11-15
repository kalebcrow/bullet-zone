package edu.unh.cs.cs619.bulletzone.repository;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.events.MineEvent;
import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.BuildingDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.InvalidResourceTileType;
import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Road;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankController;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;
import edu.unh.cs.cs619.bulletzone.events.AddTankEvent;
import edu.unh.cs.cs619.bulletzone.events.DestroyBulletEvent;
import edu.unh.cs.cs619.bulletzone.events.DestroyTankEvent;
import edu.unh.cs.cs619.bulletzone.events.DestroyWallEvent;
import edu.unh.cs.cs619.bulletzone.events.FireEvent;
import edu.unh.cs.cs619.bulletzone.events.GridEvent;
import edu.unh.cs.cs619.bulletzone.events.MoveBulletEvent;
import edu.unh.cs.cs619.bulletzone.events.MoveTankEvent;
import edu.unh.cs.cs619.bulletzone.events.TurnEvent;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.unh.cs.cs619.bulletzone.model.Direction.toByte;

@Component
public class InMemoryGameRepository implements GameRepository {

    /**
     * Field dimensions
     */
    private static final int FIELD_DIM = 16;

    /**
     * Bullet step time in milliseconds
     */
    private static final int BULLET_PERIOD = 200;

    /**
     * Bullet's impact effect [life]
     */
    private static final int BULLET_DAMAGE = 1;

    /**
     * Tank's default life [life]
     */
    boolean mine = false;
    private static final int TANK_LIFE = 100;
    private final Timer timer = new Timer();
    private final AtomicLong idGenerator = new AtomicLong();
    private final Object monitor = new Object();
    private Game game = null;
    private int bulletDamage[]={10,30,50};
    private int trackActiveBullets[]={0,0,0,0};

    /**
     * Allows a new tank to join the game
     * @param ip holds players ip string from join request
     * @return A new player tank
     */
    @Override
    public Tank[] join(String ip) {
        synchronized (this.monitor) {

            if (game == null) {
                if(ip == "test") this.testCreate();
                else this.create();
            }

            Tank[] tanks = new Tank[3];

            if(game.getTanks(ip) == null) {
                Long tankId = this.idGenerator.getAndIncrement();
                Long minerID = this.idGenerator.getAndIncrement();
                Long builderID = this.idGenerator.getAndIncrement();

                tanks[0] = new Tank(tankId, Direction.Up, ip, 0);
                tanks[1] = new Tank(minerID, Direction.Up, ip, 1);
                tanks[2] = new Tank(builderID, Direction.Up, ip, 2);

                game.addTank(ip, tanks[0], "tank");
                game.addTank(ip, tanks[1], "miner");
                game.addTank(ip, tanks[2], "builder");

                Random random = new Random();
                int x;
                int y;

                // This may run for forever.. If there is no free space. XXX
                for (; ; ) {
                    x = random.nextInt(FIELD_DIM);
                    y = random.nextInt(FIELD_DIM);
                    FieldHolder fieldElement = game.getHolderGrid().get(x * FIELD_DIM + y);
                    if (!fieldElement.isPresent()) {
                        fieldElement.setFieldEntity(tanks[0]);
                        tanks[0].setParent(fieldElement);
                        break;
                    }
                }
                game.addEvent(new AddTankEvent(x, y, tankId));
                for (; ; ) {
                    x = random.nextInt(FIELD_DIM);
                    y = random.nextInt(FIELD_DIM);
                    FieldHolder fieldElement = game.getHolderGrid().get(x * FIELD_DIM + y);
                    if (!fieldElement.isPresent()) {
                        fieldElement.setFieldEntity(tanks[1]);
                        tanks[1].setParent(fieldElement);
                        break;
                    }
                }
                game.addEvent(new AddTankEvent(x, y, minerID));
                for (; ; ) {
                    x = random.nextInt(FIELD_DIM);
                    y = random.nextInt(FIELD_DIM);
                    FieldHolder fieldElement = game.getHolderGrid().get(x * FIELD_DIM + y);
                    if (!fieldElement.isPresent()) {
                        fieldElement.setFieldEntity(tanks[2]);
                        tanks[2].setParent(fieldElement);
                        break;
                    }
                }
                game.addEvent(new AddTankEvent(x, y, builderID));

            } else {
                HashMap<String,Long> map = game.getTanks(ip);
                tanks[0] = game.getTank(map.get("tank"));
                tanks[1] = game.getTank(map.get("miner"));
                tanks[2] = game.getTank(map.get("builder"));
            }

            return tanks;
        }
    }


    /**
     * Returns games current board
     * @return returns the current games grid in a 2d array
     */
    @Override
    public int[][] getGrid() {
        synchronized (this.monitor) {
            if (game == null) {
                this.create();
            }
        }
        return game.getGrid2D();
    }

    /**
     * Allows turning of the tank
     * @param tankId id number of tank for turn
     * @param direction direction in which the tank will turn
     * @return a boolean describing if the tank moves or not
     * @throws TankDoesNotExistException if turning tank does not exist
     * @throws IllegalTransitionException if turning tank tries to move more than 90 degrees
     * @throws LimitExceededException if tank tries to turn faster than allowed
     */
    @Override
    public boolean turn(long tankId, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException {
        synchronized (this.monitor) {
            mine = false;
            checkNotNull(direction);

            // Find user
            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                //Log.i(TAG, "Cannot find user with id: " + tankId);
                throw new TankDoesNotExistException(tankId);
            }

            TankController tc = new TankController();
            if (!tc.turn(tank, direction)) {
                return false;
            }

            /*try {
                Thread.sleep(500);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }*/

            tank.setDirection(direction);
            game.addEvent(new TurnEvent(tankId, toByte(direction)));

            return true; // TODO check
        }
    }

    /**
     * Allows movement of a tank
     * @param tankId moving tank's id
     * @param direction direction in which the tank will move
     * @return a boolean for whether or not tank moves
     * @throws TankDoesNotExistException throws if moving tank does not exist
     * @throws IllegalTransitionException throws if moving faster than allowed
     * @throws LimitExceededException throws if moving faster than allowed
     */
    @Override
    public boolean move(long tankId, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException {
        synchronized (this.monitor) {
            mine = false;
            // Find tank
            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                //Log.i(TAG, "Cannot find user with id: " + tankId);
                //return false;
                throw new TankDoesNotExistException(tankId);
            }
            TankController tc = new TankController();
            if (!tc.move(tank, direction)) {
                return false;
            }

            FieldHolder parent = tank.getParent();

            FieldHolder nextField = parent.getNeighbor(direction);
            checkNotNull(parent.getNeighbor(direction), "Neighbor is not available");

            boolean isCompleted;
            if (!nextField.isPresent()) {
                // If the next field is empty move the user

                /*try {
                    Thread.sleep(500);
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }*/

                parent.clearField();
                nextField.setFieldEntity(tank);
                tank.setParent(nextField);
                game.addEvent(new MoveTankEvent(tankId, toByte(direction)));


                isCompleted = true;
            } else {
                isCompleted = false;
            }

            return isCompleted;
        }
    }

    /**
     * Allows for firing of a bullet from the tank.
     * @param tankId tank's id for firing
     * @param bulletType bullet type that is fired
     * @return boolean for if the bullet is fired or not
     * @throws TankDoesNotExistException throws if tank does not exist
     * @throws LimitExceededException throws if trying to fire more than allowed
     */
    @Override
    public boolean fire(long tankId, int bulletType)
            throws TankDoesNotExistException, LimitExceededException {
        synchronized (this.monitor) {
            mine = false;
            // Find tank
            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                //Log.i(TAG, "Cannot find user with id: " + tankId);
                //return false;
                throw new TankDoesNotExistException(tankId);
            }


            TankController tc = new TankController();
            int temp = tc.fire(tank, bulletType);
            if (temp == -1) {
                return false;
            }

            bulletType = temp;

            //Log.i(TAG, "Cannot find user with id: " + tankId);
            Direction direction = tank.getDirection();
            FieldHolder parent = tank.getParent();
            tank.setNumberOfBullets(tank.getNumberOfBullets() + 1);

            int bulletId = tank.getNumberOfBullets()-1;
            trackActiveBullets[bulletId] = 1;

            // Create a new bullet to fire
            final Bullet bullet = new Bullet(tankId, direction, bulletDamage[bulletType-1]);
            // Set the same parent for the bullet.
            // This should be only a one way reference.
            bullet.setParent(parent);
            bullet.setBulletId(bulletId);
            // TODO make it nicer
            int finalBulletId = bulletId;
            final Long finalTankID = tank.getId();
            final boolean[] fireIndicator = {true};
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (monitor) {
                        System.out.println("Active Bullet: "+tank.getNumberOfBullets()+"---- Bullet ID: "+bullet.getIntValue());
                        FieldHolder currentField = bullet.getParent();
                        Direction direction = bullet.getDirection();
                        FieldHolder nextField = currentField
                                .getNeighbor(direction);

                        // Is the bullet visible on the field?
                        boolean isVisible = currentField.isPresent()
                                && (currentField.getEntity() == bullet);


                            if (nextField.isPresent()) {
                                // Something is there, hit it
                                nextField.getEntity().hit(bullet.getDamage());
                                if(!fireIndicator[0])game.addEvent(new DestroyBulletEvent(finalTankID, finalBulletId));

                                if ( nextField.getEntity() instanceof  Tank){
                                    Tank t = (Tank) nextField.getEntity();
                                    System.out.println("tank is hit, tank life: " + t.getLife());
                                    if (t.getLife() <= 0 ){
                                        game.addEvent(new DestroyTankEvent(t.getId()));
                                        t.getParent().clearField();
                                        t.setParent(null);
                                    }
                                }
                                else if ( nextField.getEntity() instanceof  Wall){
                                    Wall w = (Wall) nextField.getEntity();
                                    if (w.getIntValue() >1000 && w.getIntValue()<=2000 ){
                                        game.addEvent(new DestroyWallEvent(w.getPos()+1));
                                        game.getHolderGrid().get(w.getPos()).clearField();
                                    }
                                }
                            if (isVisible) {
                                // Remove bullet from field
                                currentField.clearField();
                            }
                            trackActiveBullets[bullet.getBulletId()]=0;
                            tank.setNumberOfBullets(tank.getNumberOfBullets()-1);
                            cancel();

                        } else {
                            if (isVisible) {
                                if(fireIndicator[0]){
                                    game.addEvent(new FireEvent(finalTankID, finalBulletId, toByte(direction)));
                                    fireIndicator[0] = false;
                                }
                                else game.addEvent(new MoveBulletEvent(finalTankID, finalBulletId, toByte(direction)));
                                // Remove bullet from field
                                currentField.clearField();
                            }

                            nextField.setFieldEntity(bullet);
                            bullet.setParent(nextField);
                            }
                    }
                }
            }, 0, BULLET_PERIOD);

            return true;
        }
    }

    /**
     * Allows removal of the tank from the game
     * @param tankId id of the tank that is being removed
     * @throws TankDoesNotExistException throws if the specified tank does not exist
     */
    @Override
    public void leave(long tankId)
            throws TankDoesNotExistException {
        synchronized (this.monitor) {
            if (!this.game.getTanks().containsKey(tankId)) {
                throw new TankDoesNotExistException(tankId);
            }

            System.out.println("leave() called, tank ID: " + tankId);

            Tank tank = game.getTanks().get(tankId);
            FieldHolder parent = tank.getParent();
            parent.clearField();
            game.addEvent(new DestroyTankEvent(tank.getId()));
            game.removeTank(tankId);
        }
    }

    @Override
    public boolean build(long tankId, int type) throws TankDoesNotExistException, BuildingDoesNotExistException
    {
        synchronized (this.monitor) {
        /*
        TO DO: timing for build and stop movement while building
        types:
        1 - Road
        2 - Wall
        3 - Indestructible Wall
         */
            Tank builder = game.getTanks().get(tankId);
            Tank miner = new Tank();
            HashMap<String, Long> tanks = game.getTanks(builder.getIp());
            assert tanks != null;
            if (tanks.containsKey("miner"))
                miner = game.getTank(tanks.get("miner"));

            if (miner.getTypeIndex() != 2)
                return false;

            final Wall wall = new Wall();
            final Road road = new Road();
            final Wall indestructiblewall = new Wall(1000);


            if (builder == null) {
                throw new TankDoesNotExistException(tankId);
            }

            TankController tc = new TankController();
            int temp = tc.build(builder, type);
            if (temp == -1) {
                return false;
            }


            Direction direction = builder.getDirection();
            FieldHolder parent = builder.getParent();
            Byte d = Direction.toByte(direction);
            Direction behindtank = Direction.fromByte((byte) ((d + 4) % 8));
            FieldHolder behind = parent.getNeighbor(behindtank);

            if (behind.isPresent()) {
                return false;
            }

            long interval = builder.getAllowedMoveInterval();

            try {

                switch (type) {
                    case 1:
                        if (miner.getResourcesByResource(2) >= 3) {
                            builder.allowMovement = false;
                            Thread.sleep(3000);
                            miner.subtractBundleOfResourcesByAmount("clay", 3);
                            behind.setFieldEntity(road);
                            builder.allowMovement = true;
                            return true;
                        }
                        return false;
                    case 2:
                        if (miner.getResourcesByResource(2) >= 1 && miner.getResourcesByResource(0) >= 2) {
                            builder.allowMovement = false;
                            Thread.sleep(3000);
                            miner.subtractBundleOfResourcesByAmount("clay", 1);
                            miner.subtractBundleOfResourcesByAmount("rock", 2);
                            behind.setFieldEntity(wall);
                            builder.allowMovement = true;
                            return true;
                        }
                        return false;
                    case 3:
                        if (miner.getResourcesByResource(2) >= 3 && miner.getResourcesByResource(0) >= 3 && miner.getResourcesByResource(1) >= 3) {
                            builder.allowMovement = false;
                            Thread.sleep(9000);
                            miner.subtractBundleOfResourcesByAmount("rock", 3);
                            miner.subtractBundleOfResourcesByAmount("clay", 3);
                            miner.subtractBundleOfResourcesByAmount("iron", 3);
                            behind.setFieldEntity(indestructiblewall);
                            builder.allowMovement = true;
                            return true;
                        }
                        return false;
                    default:
                        throw new BuildingDoesNotExistException();
                }
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            //game.addEvent(new BuildwallEvent);
        }
        return false;

    }

    @Override
    public boolean dismantle(long tankId) throws TankDoesNotExistException
    {
        Tank builder = game.getTanks().get(tankId);


        if (builder == null)
        {
            throw new TankDoesNotExistException(tankId);
        }

        Tank miner = new Tank();
        HashMap<String, Long> tanks = game.getTanks(builder.getIp());
        assert tanks != null;
        if (tanks.containsKey("miner"))
            miner = game.getTank(tanks.get("miner"));

        if (miner.getTypeIndex() != 2)
            return false;

        TankController tc = new TankController();
        int temp = tc.dismantle(builder);
        if (temp == -1) {
            return false;
        }

        Direction direction = builder.getDirection();
        FieldHolder parent = builder.getParent();
        Byte d = Direction.toByte(direction);
        Direction behindtank = Direction.fromByte((byte) ((d+4)%8));
        FieldHolder behind = parent.getNeighbor(behindtank);

        if(behind.isPresent())
        {
            FieldEntity structure = behind.getEntity();
            if(structure.toString() == "W")
            {
                if(structure.getIntValue() == 1500)
                {
                    miner.addBundleOfResourcesByAmount("rock",3);
                    miner.addBundleOfResourcesByAmount("clay",3);
                    miner.addBundleOfResourcesByAmount("iron",3);
                    behind.clearField();
                }
                else
                {
                    miner.addBundleOfResourcesByAmount("rock",2);
                    miner.addBundleOfResourcesByAmount("clay",1);
                    behind.clearField();
                }
                return true;
            }
            else if(structure.toString() == "R")
            {
                miner.addBundleOfResourcesByAmount("clay",3);
                behind.clearField();
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }

    }

    /**
     * Creates a new game and generates a new board.
     */
    public void create() {
        if (game != null) {
            return;
        }
        synchronized (this.monitor) {
            this.game = new Game();
            GameBoardBuilder boardBuilder = new GameBoardBuilder(game);
            boardBuilder.create();
        }
    }

    public void testCreate() {
        if(game != null){
            return;
        }
        synchronized (this.monitor){
            this.game = new Game();
            GameBoardBuilder boardBuilder = new GameBoardBuilder(game);
            boardBuilder.testCreate();
        }
    }

    /**
     * Returns the current game
     * @return current active game
     */
    @Override
    public Game getGame() {
        return game;
    }

    public LinkedList<GridEvent> getEvents(Long time) {
        return game.getEvents(time);
    }

    @Override
    public boolean mine(long tankId)
            throws TankDoesNotExistException, LimitExceededException, InvalidResourceTileType {
        synchronized (this.monitor) {
            //If mine is already hit
            if (mine) {
                return false;
            }
            // Find user
            Tank miner = game.getTanks().get(tankId);
            if (miner == null) {
                //Log.i(TAG, "Cannot find user with id: " + tankId);
                throw new TankDoesNotExistException(tankId);
            }

            TankController tc = new TankController();
            if (!tc.mine(miner)) {
                return false;
            }

            FieldHolder parent = miner.getParent();
            FieldTerrain resourceTile = parent.getTerrain();
            if(resourceTile == null)
            {
                return false;
            }
            mine = true;

            //Initial pause for the mining process
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (monitor) {
                        if (!mine) {
                            cancel();
                        }
                        TankController tc = new TankController();
                        try {
                            if (!tc.mine(miner)) { //This means tanks life is at 0
                                cancel();
                            }
                        } catch (LimitExceededException | TankDoesNotExistException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Starting mining process");
                        switch (resourceTile.getIntValue()) {
                            case 0:
                                if (!miner.addBundleOfResources(2)) {
                                    System.out.println("Failed to add clay resource type to stash");
                                    cancel();
                                }
                                System.out.println("Finished mining process, adding clay to stash");
                            case 1:
                                if (!miner.addBundleOfResources(0)) {
                                    System.out.println("Failed to add clay resource rock to stash");
                                    cancel();
                                }
                                System.out.println("Finished mining process, adding rock to stash");
                            case 2:
                                if (!miner.addBundleOfResources(1)) {
                                    System.out.println("Failed to add clay resource iron to stash");
                                    cancel();
                                }
                                System.out.println("Finished mining process, adding iron to stash");
                                game.addEvent(new MineEvent(tankId, miner.getAllResources()));
                            default:
                                try {
                                    throw new InvalidResourceTileType();
                                } catch (InvalidResourceTileType e) {
                                    e.printStackTrace();
                                }
                        }
                    }
                }
            }, 20, 1000);
            return true;
        }
    }
}
