package edu.unh.cs.cs619.bulletzone.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccount;
import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccountRepository;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUserRepository;
import edu.unh.cs.cs619.bulletzone.Command;
import edu.unh.cs.cs619.bulletzone.CommandInterpreter;
import edu.unh.cs.cs619.bulletzone.MoveCommand;
import edu.unh.cs.cs619.bulletzone.TurnCommand;
import edu.unh.cs.cs619.bulletzone.events.BuildEvent;
import edu.unh.cs.cs619.bulletzone.events.DamageEvent;
import edu.unh.cs.cs619.bulletzone.events.DismantleEvent;
import edu.unh.cs.cs619.bulletzone.events.MineEvent;
import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.BuildingDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.InvalidResourceTileType;
import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.FieldTerrain;
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
import static edu.unh.cs.cs619.bulletzone.model.Direction.Down;
import static edu.unh.cs.cs619.bulletzone.model.Direction.Left;
import static edu.unh.cs.cs619.bulletzone.model.Direction.Right;
import static edu.unh.cs.cs619.bulletzone.model.Direction.Up;
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

    private static final Logger log = LoggerFactory.getLogger(InMemoryGameRepository.class);


    /**
     * Allows a new tank to join the game
     * @param ip holds players ip string from join request
     * @return A new player tank
     */
    @Override
    public Tank[] join(long userID, String ip) {
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

                tanks[0] = new Tank(userID, tankId, Direction.Up, ip, 0);
                tanks[1] = new Tank(userID, minerID, Direction.Up, ip, 1);
                tanks[2] = new Tank(userID, builderID, Direction.Up, ip, 2);

                game.addTank(ip, tanks[0], "tank");
                game.addTank(ip, tanks[1], "miner");
                game.addTank(ip, tanks[2], "builder");

                if(ip == ""){
                    FieldHolder place = game.getHolderGrid().get(16);
                    place.setFieldEntity(tanks[0]);
                    tanks[0].setParent(place);
                    place = game.getHolderGrid().get(33);
                    place.setFieldEntity(tanks[1]);
                    tanks[1].setParent(place);
                    place = game.getHolderGrid().get(251);
                    place.setFieldEntity(tanks[2]);
                    tanks[2].setParent(place);
                } else {

                    Random random = new Random();
                    int x;
                    int y;

                    // This may run for forever.. If there is no free space. XXX
                    for (; ; ) {
                        x = random.nextInt(FIELD_DIM);
                        y = random.nextInt(FIELD_DIM);
                        FieldHolder fieldElement = game.getHolderGrid().get(x * FIELD_DIM + y);
                        if (!fieldElement.isEntityPresent()) {
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
                        if (!fieldElement.isEntityPresent()) {
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
                        if (!fieldElement.isEntityPresent()) {
                            fieldElement.setFieldEntity(tanks[2]);
                            tanks[2].setParent(fieldElement);
                            break;
                        }
                    }
                    game.addEvent(new AddTankEvent(x, y, builderID));
                }
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
    public int[][][] getGrid() {
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

            FieldHolder parent = tank.getParent();

            FieldHolder nextField = parent.getNeighbor(direction);
            checkNotNull(parent.getNeighbor(direction), "Neighbor is not available");

            double speed = 0;
                // adding a check for field type (blank, hilly, or rocky) for speed purposes
                if (nextField.getTerrain().toString().equals("R")) {
                    // rocky
                    speed = tank.getAllowedMoveInterval() * 2;
                } else if (nextField.getTerrain().toString().equals("H")) {
                    // hilly
                    speed = tank.getAllowedMoveInterval() * 1.5;
                } else {
                    // meadow
                    speed = 0;
                }
            if(nextField.isImprovementPresent())
            {
                if(nextField.getImprovement().toString() == "R")
                {
                    speed = speed/2;
                }
            }


            TankController tc = new TankController();
            if (!tc.move(tank, direction, speed)) {
                return false;
            }

            parent = tank.getParent();

            checkNotNull(parent.getNeighbor(direction), "Neighbor is not available");
            nextField = parent.getNeighbor(direction);
            checkNotNull(parent.getNeighbor(direction), "Neighbor is not available");


            boolean isCompleted;
            if (!nextField.isEntityPresent()) {
                // If the next field is empty move the user

                /*try {
                    Thread.sleep(500);
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }*/
                parent.clearField();
                nextField.setFieldEntity(tank);
                tank.setParent(nextField);

                log.debug("---------------MOVING TANK from " + parent.getTerrain().toString() + " to " + nextField.getTerrain().toString());
                game.addEvent(new MoveTankEvent(tankId, toByte(direction), parent.getTerrain().toString()));

                isCompleted = true;
            } else {
                isCompleted = false;
                FieldEntity ent = nextField.getEntity();
                if (ent.toString().equals("R") || ent.toString().equals("IW") || ent.toString().equals("B")) {
                    return false;
                }
                ent.hit((int)Math.ceil(tank.getLife() / tank.giveDamageModifier()));
                if (ent.toString().equals("T")) {
                    Tank newTank = (Tank) ent;
                    tank.hit((int)Math.floor(newTank.getLife() / tank.getDamageModifier()));
                    game.addEvent(new DamageEvent(Math.toIntExact(tank.getId()), tank.getLife()));
                    game.addEvent(new DamageEvent(Math.toIntExact(newTank.getId()), newTank.getLife()));
                    if (tank.getLife() <= 0 ){
                        String terrain = tank.getParent().getTerrain().toString();
                        game.addEvent(new DestroyTankEvent(tank.getId(), terrain));
                        tank.getParent().clearField();
                        tank.setParent(null);
                    }
                    if (newTank.getLife() <= 0){
                        String terrain = tank.getParent().getTerrain().toString();
                        game.addEvent(new DestroyTankEvent(newTank.getId(), terrain));
                        newTank.getParent().clearField();
                        newTank.setParent(null);
                    }
                } else {
                    tank.hit((int)Math.floor(ent.getIntValue() / tank.getDamageModifier()));
                    game.addEvent(new DamageEvent(Math.toIntExact(tank.getId()), tank.getLife()));
                    if (tank.getLife() <= 0 ){
                        String terrain = tank.getParent().getTerrain().toString();
                        game.addEvent(new DestroyTankEvent(tank.getId(), terrain));
                        tank.getParent().clearField();
                        tank.setParent(null);
                    }
                }
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

                        Direction oppDirection = bullet.getDirection();
                        FieldHolder previousField = currentField
                                .getNeighbor(oppDirection);
                        String previousterrain = previousField.getTerrain().toString();
                        String terrain = currentField.getTerrain().toString();


                        // Is the bullet visible on the field?
                        boolean isVisible = currentField.isEntityPresent()
                                && (currentField.getEntity() == bullet);


                            if (nextField.isEntityPresent()) {
                                // Something is there, hit it
                                nextField.getEntity().hit(bullet.getDamage());
                                if(!fireIndicator[0])game.addEvent(new DestroyBulletEvent(finalTankID, finalBulletId, terrain));

                                if ( nextField.getEntity() instanceof  Tank){
                                    Tank t = (Tank) nextField.getEntity();
                                    System.out.println("tank is hit, tank life: " + t.getLife());
                                    game.addEvent(new DamageEvent(Math.toIntExact(t.getId()), t.getLife()));
                                    if (t.getLife() <= 0 ){
                                        game.addEvent(new DestroyTankEvent(t.getId(), terrain));
                                        t.getParent().clearField();
                                        t.setParent(null);
                                    }
                                }
                                else if ( nextField.getEntity() instanceof  Wall){
                                    Wall w = (Wall) nextField.getEntity();
                                    if (w.getIntValue() >1000 && w.getIntValue()<=2000 ){
                                        game.addEvent(new DestroyWallEvent(w.getPos()+1, terrain));
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
                                else game.addEvent(new MoveBulletEvent(finalTankID, finalBulletId, toByte(direction), terrain));
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
    public void leave(long[] tankId)
            throws TankDoesNotExistException {
        synchronized (this.monitor) {
            for (int i = 0; i < 3; i++){
                if (!this.game.getTanks().containsKey(tankId[i])) {
                    throw new TankDoesNotExistException(tankId[i]);
                }

            System.out.println("leave() called, tank ID: " + tankId[i]);

                Tank tank = game.getTanks().get(tankId[i]);

                if (i == 1 && tank.getUserID() != -1) {
                    DataRepository data = new DataRepository();
                    GameUserRepository users = new GameUserRepository();
                    GameUser gu = users.getUser(Math.toIntExact(tank.getUserID()));
                    String username = gu.getUsername();
                    double amount = (tank.getResourcesByResource(0) * 25) + (tank.getResourcesByResource(1) * 78) + (tank.getResourcesByResource(2) * 16);
                    data.modifyAccountBalance(username, amount);
                }

                FieldHolder parent = tank.getParent();
                parent.clearField();
                if(tank.getLife() > 0) {
                    game.addEvent(new DestroyTankEvent(tank.getId(), parent.getTerrain().toString()));
                }
                game.removeTank(tankId[i]);
            }
        }
    }

    @Override
    public boolean build(long tankId, int type) throws TankDoesNotExistException, BuildingDoesNotExistException
    {
        synchronized (this.monitor) {
        /*
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

            if (miner.getTypeIndex() != 1)
                return false;

            final Wall wall = new Wall(1500);
            final Road road = new Road();
            final Wall indestructibleWall = new Wall();
            indestructibleWall.name = "IW";


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

            if (behind.isEntityPresent()) {
                return false;
            }
            if( behind.isImprovementPresent())
            {
                return false;
            }

            long interval = builder.getAllowedMoveInterval();

            try {

                switch (type) {
                    case 1:
                        if (miner.getResourcesByResource(2) >= 3) {
                            builder.allowMovement = false;
                            Thread.sleep(3000);
                            miner.subtractBundleOfResources(2, 3);
                            behind.setImprovementEntity(road);
                            builder.allowMovement = true;
                            game.addEvent(new BuildEvent(tankId,miner.getAllResources(),1,behind.getPos()));
                            return true;
                        }
                        return false;
                    case 2:
                        if (miner.getResourcesByResource(2) >= 1 && miner.getResourcesByResource(0) >= 2) {
                            builder.allowMovement = false;
                            Thread.sleep(3000);
                            miner.subtractBundleOfResources(2, 1);
                            miner.subtractBundleOfResources(0, 2);
                            behind.setFieldEntity(wall);
                            builder.allowMovement = true;
                            game.addEvent(new BuildEvent(tankId,miner.getAllResources(),2, behind.getPos()));
                            return true;
                        }
                        return false;
                    case 3:
                        if (miner.getResourcesByResource(2) >= 3 && miner.getResourcesByResource(0) >= 3 && miner.getResourcesByResource(1) >= 3) {
                            builder.allowMovement = false;
                            Thread.sleep(9000);
                            miner.subtractBundleOfResources(0, 3);
                            miner.subtractBundleOfResources(2, 3);
                            miner.subtractBundleOfResources(1, 3);
                            behind.setFieldEntity(indestructibleWall);
                            builder.allowMovement = true;
                            game.addEvent(new BuildEvent(tankId,miner.getAllResources(),3, behind.getPos()));

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

        if (miner.getTypeIndex() != 1)
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

        if(behind.isEntityPresent())
        {
            FieldEntity structure = behind.getEntity();
            if(structure.toString() == "W")
            {
                miner.addBundleOfResources(0,2);
                miner.addBundleOfResources(2,1);
                behind.clearField();
                game.addEvent(new DismantleEvent(tankId,miner.getAllResources(),behind.getPos(),2));
                return true;
            }
            else if(structure.toString() == "IW")
            {
                miner.addBundleOfResources(0,3);
                miner.addBundleOfResources(2,3);
                miner.addBundleOfResources(1,3);
                behind.clearField();
                game.addEvent(new DismantleEvent(tankId,miner.getAllResources(),behind.getPos(),3));
                return true;
            }
            else
            {
                return false;
            }
        }
        else if(behind.isImprovementPresent())
        {
            FieldEntity structure = behind.getImprovement();
            if(structure.toString() == "R") {
                miner.addBundleOfResources(2, 3);
                behind.clearField();
                game.addEvent(new DismantleEvent(tankId, miner.getAllResources(), behind.getPos(), 1));
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

    //makes good faith effort to arrive at requested location by moving vertically then horizontally
    //stops if it detects it will hit an entity
    public boolean moveTo(long tankId, int desiredLocation) throws TankDoesNotExistException {

        // Find tank
        Tank tank = game.getTanks().get(tankId);
        if (tank == null) {
            //Log.i(TAG, "Cannot find user with id: " + tankId);
            //return false;
            throw new TankDoesNotExistException(tankId);
        }

        //find the current location of the tank
        int[][][] grid3d = game.getGrid2D();
        int currentLocation = -1;
        int test = -1;
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                if(grid3d[i][j][2] >= 10000000 && grid3d[i][j][2] < 20000000){

                    test = grid3d[i][j][2];
                    test = test % 10000000;
                    test = test / 10000;

                    if(tankId == (long)test){

                        currentLocation = i*16 + j;
                        break;

                    }

                }

            }

            if(currentLocation != -1){
                break;
            }

        }

        //determine tank type: 0 = tank, 1 = miner, 2 = builder

        long baseDelay;
        if(test % 3 == 0){
            System.out.println("Tank");
            baseDelay = 500;
        }
        else if(test % 3 == 1){
            System.out.println("Miner");
            baseDelay = 800;
        }
        else{
            System.out.println("Builder");
            baseDelay = 300;
        }

        System.out.println("Tank ID: " + tankId + "\tcurrent location: " + currentLocation);

        //with location of tank on the board and desired location,
        //determine direction of y value movement, north or south
        Direction yValueMovement = null;
        if((currentLocation/16) > (desiredLocation/16)){
            yValueMovement = Direction.Up;
        }
        else if((currentLocation/16) < (desiredLocation/16)){
            yValueMovement = Direction.Down;
        }
        else if((currentLocation/16) == (desiredLocation/16)){
            yValueMovement = null;
        }

        Direction xValueMovement = null;
        if((currentLocation%16) > (desiredLocation%16)){
            xValueMovement = Direction.Left;
        }
        else if((currentLocation%16) < (desiredLocation%16)){
            xValueMovement = Direction.Right;
        }
        else if((currentLocation%16) == (desiredLocation%16)){
            xValueMovement = null;
        }

        System.out.println("XValueMovement: " + xValueMovement + "\tYValueMovement: " + yValueMovement);


        //create list of Move To Commands
        ArrayList<Command> MoveToList = new ArrayList<>();
        long placeholderDelay = 800*2; //longest possible delay for moving
        Direction currentDirection = tank.getDirection();

        //special cases: on same y or x coordinate, or both
        if(xValueMovement == null && yValueMovement == null){
            //trying to move to current coordinates
            return true;
        }
        //must move in both the x and y coordinate
        else if(xValueMovement != null && yValueMovement != null){

            if(yValueMovement == Direction.Up){
                //queue up moves to get the tank to face up
                if(currentDirection == Direction.Down){

                    MoveToList.add(new TurnCommand(tank, Direction.Left, this, placeholderDelay));
                    MoveToList.add(new TurnCommand(tank, Direction.Up, this, placeholderDelay));
                }
                else if(currentDirection == Direction.Left || currentDirection == Direction.Right){
                    MoveToList.add(new TurnCommand(tank, Direction.Up, this, placeholderDelay));
                }
            }
            else if(yValueMovement == Direction.Down){
                //queue up moves to get the tank to face down
                if(currentDirection == Direction.Up){
                    MoveToList.add(new TurnCommand(tank, Direction.Left, this, placeholderDelay));
                    MoveToList.add(new TurnCommand(tank, Direction.Down, this, placeholderDelay));
                }
                else if(currentDirection == Direction.Left || currentDirection == Direction.Right){
                    MoveToList.add(new TurnCommand(tank, Direction.Down, this, placeholderDelay));
                }

            }

            int yValueDistance = Math.abs((currentLocation/16) - (desiredLocation/16));

            //System.out.println("YValueDistance: " + yValueDistance);

            for(int i = 0; i < yValueDistance; i++){
                if(yValueMovement == Down){
                    MoveToList.add(new MoveCommand(tank, Direction.Down, this, placeholderDelay));
                }
                else{
                    MoveToList.add(new MoveCommand(tank, Direction.Up, this, placeholderDelay));
                }
            }

            if(xValueMovement == Left){
                MoveToList.add(new TurnCommand(tank, Direction.Left, this, placeholderDelay));
            }
            else{
                MoveToList.add(new TurnCommand(tank, Direction.Right, this, placeholderDelay));
            }

            int xValueDistance = Math.abs((currentLocation%16) - (desiredLocation%16));
            for(int i = 0; i < xValueDistance; i++){
                if(xValueMovement == Left){
                    MoveToList.add(new MoveCommand(tank, Direction.Left, this, placeholderDelay));
                }
                else{
                    MoveToList.add(new MoveCommand(tank, Direction.Right, this, placeholderDelay));
                }
            }

        }
        //only need to move in the x coordinate
        else if(xValueMovement != null && yValueMovement == null){
            //only moving horizontally

            if(xValueMovement == Left){
                if(currentDirection == Right){
                    MoveToList.add(new TurnCommand(tank, Up, this, placeholderDelay));
                    MoveToList.add(new TurnCommand(tank, Left, this, placeholderDelay));
                }
                else if(currentDirection == Up || currentDirection == Down){
                    MoveToList.add(new TurnCommand(tank, Left, this, placeholderDelay));
                }
            }
            else if(xValueMovement == Right){

                if(xValueMovement == Left){
                    MoveToList.add(new TurnCommand(tank, Up, this, placeholderDelay));
                    MoveToList.add(new TurnCommand(tank, Right, this, placeholderDelay));
                }
                else if(currentDirection == Up || currentDirection == Down){
                    MoveToList.add(new TurnCommand(tank, Right, this, placeholderDelay));
                }

            }

            int xValueDistance = Math.abs((currentLocation%16) - (desiredLocation%16));
            for(int i = 0; i < xValueDistance; i++){
                if(xValueMovement == Left){
                    MoveToList.add(new MoveCommand(tank, Direction.Left, this, placeholderDelay));
                }
                else{
                    MoveToList.add(new MoveCommand(tank, Direction.Right, this, placeholderDelay));
                }
            }

        }
        //only need to move vertically
        else if(xValueMovement == null && yValueMovement != null){
            if(yValueMovement == Up){
                if(currentDirection == Down){
                    MoveToList.add(new TurnCommand(tank, Left, this, placeholderDelay));
                    MoveToList.add(new TurnCommand(tank, Up, this, placeholderDelay));
                }
                else if(currentDirection == Left || currentDirection == Right){
                    MoveToList.add(new TurnCommand(tank, Up, this, placeholderDelay));
                }
            }
            else if(yValueMovement == Down){
                if(currentDirection == Up){
                    MoveToList.add(new TurnCommand(tank, Left, this, placeholderDelay));
                    MoveToList.add(new TurnCommand(tank, Down, this, placeholderDelay));
                }
                else if(currentDirection == Left || currentDirection == Right){
                    MoveToList.add(new TurnCommand(tank, Down, this, placeholderDelay));
                }
            }

            int yValueDistance = Math.abs((currentLocation/16) - (desiredLocation/16));
            for(int i = 0; i < yValueDistance; i++){
                if(yValueMovement == Down){
                    MoveToList.add(new MoveCommand(tank, Direction.Down, this, placeholderDelay));
                }
                else{
                    MoveToList.add(new MoveCommand(tank, Direction.Up, this, placeholderDelay));
                }
            }

        }


        CommandInterpreter commandInterpreter = new CommandInterpreter(MoveToList);
        return commandInterpreter.executeSequence();

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
                                if (!miner.addBundleOfResources(2, 1)) {
                                    System.out.println("Failed to add clay resource type to stash");
                                    cancel();
                                }
                                System.out.println("Finished mining process, adding clay to stash");
                                game.addEvent(new MineEvent(tankId, miner.getAllResources()));
                                break;
                            case 1:
                                if (!miner.addBundleOfResources(0, 1)) {
                                    System.out.println("Failed to add clay resource rock to stash");
                                    cancel();
                                }
                                System.out.println("Finished mining process, adding rock to stash");
                                game.addEvent(new MineEvent(tankId, miner.getAllResources()));
                                break;
                            case 2:
                                if (!miner.addBundleOfResources(1, 1)) {
                                    System.out.println("Failed to add clay resource iron to stash");
                                    cancel();
                                }
                                System.out.println("Finished mining process, adding iron to stash");
                                game.addEvent(new MineEvent(tankId, miner.getAllResources()));
                                break;
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
