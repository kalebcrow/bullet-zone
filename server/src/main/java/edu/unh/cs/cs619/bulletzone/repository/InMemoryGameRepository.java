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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccount;
import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccountRepository;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUserRepository;
import edu.unh.cs.cs619.bulletzone.Command;
import edu.unh.cs.cs619.bulletzone.CommandInterpreter;
import edu.unh.cs.cs619.bulletzone.MoveCommand;
import edu.unh.cs.cs619.bulletzone.TurnCommand;
import edu.unh.cs.cs619.bulletzone.events.AddResourceEvent;
import edu.unh.cs.cs619.bulletzone.events.BuildEvent;
import edu.unh.cs.cs619.bulletzone.events.DamageEvent;
import edu.unh.cs.cs619.bulletzone.events.DestroyResourceEvent;
import edu.unh.cs.cs619.bulletzone.events.DismantleEvent;
import edu.unh.cs.cs619.bulletzone.events.EventManager;
import edu.unh.cs.cs619.bulletzone.events.MineEvent;
import edu.unh.cs.cs619.bulletzone.events.balanceEvent;
import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Clay;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.BuildingDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.InvalidResourceTileType;
import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.FieldResource;
import edu.unh.cs.cs619.bulletzone.model.FieldTerrain;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Iron;
import edu.unh.cs.cs619.bulletzone.model.Road;
import edu.unh.cs.cs619.bulletzone.model.Rock;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankController;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Thingamajig;
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
import jdk.internal.org.jline.utils.Log;

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
    private TankController tc = new TankController();
    private EventManager eventManager = EventManager.getInstance();
    boolean mine = false;
    private static final int TANK_LIFE = 100;
    private final Timer timer = new Timer();
    private final AtomicLong idGenerator = new AtomicLong();
    private final Object monitor = new Object();
    private Game game = null;
    private int bulletDamage[]={30,5,10};
    private int trackActiveBullets[]={0,0,0,0};
    private DataRepository data = new DataRepository();

    private ConcurrentMap<Integer, FieldResource> itemsOnGrid = new ConcurrentHashMap<>();

    private static final Logger log = LoggerFactory.getLogger(InMemoryGameRepository.class);

    GameUserRepository users = new GameUserRepository();


    /**
     * Allows a new tank to join the game
     * @param ip holds players ip string from join request
     * @return A new player tank
     */
    @Override
    public Tank[] join(String username, String ip) {
        synchronized (this.monitor) {

            if (game == null) {
                if(ip == "test") this.testCreate();
                else this.create();
                FieldResource.setItemsOnGrid(itemsOnGrid);
                // since its creating the game also start spawning resources
                getRandomResources();
            }
            Tank[] tanks = new Tank[3];

            if(game.getTanks(ip) == null) {
                Long tankId = this.idGenerator.getAndIncrement();
                Long minerID = this.idGenerator.getAndIncrement();
                Long builderID = this.idGenerator.getAndIncrement();

                tanks[0] = new Tank(username, tankId, Direction.Up, ip, 0);
                tanks[1] = new Tank(username, minerID, Direction.Up, ip, 1);
                tanks[2] = new Tank(username, builderID, Direction.Up, ip, 2);

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
                    eventManager.addEvent(new AddTankEvent(x, y, tankId));
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
                    eventManager.addEvent(new AddTankEvent(x, y, minerID));
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
                    eventManager.addEvent(new AddTankEvent(x, y, builderID));
                }
            } else {
                HashMap<String,Long> map = game.getTanks(ip);
                tanks[0] = game.getTank(map.get("tank"));
                tanks[1] = game.getTank(map.get("miner"));
                tanks[2] = game.getTank(map.get("builder"));
            }
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    eventManager.addEvent(new balanceEvent(data.getUserAccountBalance(tanks[0].getUsername()), tanks[0].getId()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            return tanks;
        }
    }


    /**
     * Returns games current board
     * @return returns the current games grid in a 3d array
     */
    @Override
    public int[][][] getGrid() {
        synchronized (this.monitor) {
            if (game == null) {
                this.create();
            }
        }
        return game.getGrid3D();
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
            checkNotNull(direction);

            // Find user
            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                //Log.i(TAG, "Cannot find user with id: " + tankId);
                throw new TankDoesNotExistException(tankId);
            }
            if (tank.getTypeIndex() == 1) {
                mine = false;
            }

            if (!tc.turn(tank, direction)) {
                System.out.println("TankController request denied!");
                return false;
            }

            /*try {
                Thread.sleep(500);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }*/

            tank.setDirection(direction);
            eventManager.addEvent(new TurnEvent(tankId, toByte(direction)));

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
            // Find tank
            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                throw new TankDoesNotExistException(tankId);
            }
            log.debug("--------------------------------------terrain: " + tank.getParent().getTerrain().toString() + ", index: " + tank.getTypeIndex());

            if (tank.getTypeIndex() == 1) {
                mine = false;
            }

            FieldHolder parent = tank.getParent();
            FieldHolder nextField = parent.getNeighbor(direction);
            checkNotNull(parent.getNeighbor(direction), "Neighbor is not available");

            if (!tc.move(tank, direction)) {
                return false;
            }

            return tank.moveTank(direction);
        }
    }

    /**
     * check if the next field has a resource on it
     *
     * @param nextField the next field
     * @return if resource available
     */
    private boolean isResource(FieldHolder nextField) {
        if (nextField.isEntityPresent()) {
            FieldEntity fr = nextField.getEntity();
            log.debug("-----------------tried to MOVE onto a resource entity with int value: " + fr.getIntValue());
            return fr.getIntValue() == 501 || fr.getIntValue() == 502 ||
                    fr.getIntValue() == 503 || fr.getIntValue() == 7;

        }
        return false;
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
            // Find tank
            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                throw new TankDoesNotExistException(tankId);
            }
            if (tank.getTypeIndex() == 1) {
                mine = false;
            }

            if (!tc.fire(tank, bulletType)) return false;

            //Log.i(TAG, "Cannot find user with id: " + tankId);
            Direction direction = tank.getDirection();
            FieldHolder parent = tank.getParent();
            tank.setNumberOfBullets(tank.getNumberOfBullets() + 1);
            int bulletId = tank.getNumberOfBullets()-1;

            // Create a new bullet to fire
            final Bullet bullet = new Bullet(tankId, direction, bulletDamage[bulletType]);
            // Set the same parent for the bullet.
            // This should be only a one way reference.
            bullet.setBulletId(bulletId);
            bullet.travel(tank);

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

            HashMap<String, Long> map = game.getTanks(game.getTank(tankId).getIp());

            Tank miner = game.getTank(map.get("miner"));
            Tank builder = game.getTank(map.get("builder"));
            Tank tank = game.getTank(map.get("tank"));


            System.out.println("leave() called, tank ID: " + tank.getId());
            System.out.println("leave() called, tank ID: " + miner.getId());
            System.out.println("leave() called, tank ID: " + builder.getId());

            double amount = (miner.getResourcesByResource(0) * 25) + (miner.getResourcesByResource(1) * 78) + (miner.getResourcesByResource(2) * 16);
            System.out.println("AMOUNT: " + amount);
            data.modifyAccountBalance(tank.getUsername(), amount);
            eventManager.addEvent(new balanceEvent(data.getUserAccountBalance(tank.getUsername()), tankId));
            System.out.println("AMOUNT balance: " + data.getUserAccountBalance(tank.getUsername()));
            miner.subtractBundleOfResources(0, miner.getResourcesByResource(0));
            miner.subtractBundleOfResources(1, miner.getResourcesByResource(1));
            miner.subtractBundleOfResources(2, miner.getResourcesByResource(2));
            FieldHolder parent = tank.getParent();
            parent.clearField();
            if(tank.getLife() > 0) {
                eventManager.addEvent(new DestroyTankEvent(tank.getId()));
            }
            game.removeTank(tank.getId());

            parent = miner.getParent();
            parent.clearField();
            if(tank.getLife() > 0) {
                eventManager.addEvent(new DestroyTankEvent(miner.getId()));
            }
            game.removeTank(miner.getId());

            parent = builder.getParent();
            parent.clearField();
            if(tank.getLife() > 0) {
                eventManager.addEvent(new DestroyTankEvent(builder.getId()));
            }
            game.removeTank(builder.getId());
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
                            eventManager.addEvent(new BuildEvent(tankId,miner.getAllResources(),1,behind.getPos()));
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
                            eventManager.addEvent(new BuildEvent(tankId,miner.getAllResources(),2, behind.getPos()));
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
                            eventManager.addEvent(new BuildEvent(tankId,miner.getAllResources(),3, behind.getPos()));

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
                eventManager.addEvent(new DismantleEvent(tankId,miner.getAllResources(),behind.getPos(),2));
                return true;
            }
            else if(structure.toString() == "IW")
            {
                miner.addBundleOfResources(0,3);
                miner.addBundleOfResources(2,3);
                miner.addBundleOfResources(1,3);
                behind.clearField();
                eventManager.addEvent(new DismantleEvent(tankId,miner.getAllResources(),behind.getPos(),3));
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
                behind.clearImprovement();
                eventManager.addEvent(new DismantleEvent(tankId, miner.getAllResources(), behind.getPos(),1));
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
            GameBoardDirector gbd = new GameBoardDirector();
            ConcreteGameBoardBuilder gbb = new ConcreteGameBoardBuilder(game);
            gbd.ConstructGameBoard(gbb);
            //GameBoardBuilder boardBuilder = new GameBoardBuilder(game);
            //boardBuilder.create();
        }
    }

    public void testCreate() {
        if(game != null){
            return;
        }
        synchronized (this.monitor){
            this.game = new Game();
            GameBoardDirector gbd = new GameBoardDirector();
            ConcreteGameBoardBuilder gbb = new ConcreteGameBoardBuilder(game);
            gbd.ConstructGameBoard(gbb);

            //GameBoardBuilder boardBuilder = new GameBoardBuilder(game);
            //boardBuilder.testCreate();
        }
    }

    //makes good faith effort to arrive at requested location by moving vertically then horizontally
    //stops if it detects it will hit an entity
    public boolean moveTo(long tankId, int desiredLocation) throws TankDoesNotExistException {

        // Find tank
        Tank tank = game.getTank(tankId);

        tank.getParent().getPos();
        if (tank == null) {
            //Log.i(TAG, "Cannot find user with id: " + tankId);
            //return false;
            throw new TankDoesNotExistException(tankId);
        }

        //find the current location of the tank
        int[][][] grid3d = game.getGrid3D();
        int currentLocation = -1;
        int test = -1;
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                if(grid3d[i][j][1] >= 10000000 && grid3d[i][j][1] < 20000000){

                    System.out.println(grid3d[i][j][1]);

                    test = grid3d[i][j][1];
                    test = test % 10000000;
                    test = test / 10000;

                    if(tankId == (long)test){

                        currentLocation = i*16 + j;
                        break;

                    }

                    System.out.println(test);

                }

            }

            if(currentLocation != -1){
                break;
            }

        }

        System.out.println("tank.getParent().getPos(): " + tank.getParent().getPos());

        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid3d[i][j][1] + "\t");
            }
            System.out.println();
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

        System.out.println("Tank ID: " + tankId + "\tcurrent location: " + currentLocation + "\t desired location: " + desiredLocation);

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
        long placeholderDelay = 3000; //longest possible delay for moving
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
        return eventManager.getEvents(time);
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
                                eventManager.addEvent(new MineEvent(tankId, miner.getAllResources()));
                                break;
                            case 1:
                                if (!miner.addBundleOfResources(0, 1)) {
                                    System.out.println("Failed to add clay resource rock to stash");
                                    cancel();
                                }
                                System.out.println("Finished mining process, adding rock to stash");
                                eventManager.addEvent(new MineEvent(tankId, miner.getAllResources()));
                                break;
                            case 2:
                                if (!miner.addBundleOfResources(1, 1)) {
                                    System.out.println("Failed to add clay resource iron to stash");
                                    cancel();
                                }
                                System.out.println("Finished mining process, adding iron to stash");
                                eventManager.addEvent(new MineEvent(tankId, miner.getAllResources()));
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

    /**
     * Gets random resources every 1 sec
     */
    private void getRandomResources() {
        // making it wait a second before starting so it doesn't crash
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // do something
                setRandomResources();
            }
        }, 1000, 1000);
    }

    /**
     * Sets the random resources every one second based on probability
     */
    private void setRandomResources() {
        double numPlayers = (double) game.getPlayersIP().size();
        ArrayList<FieldHolder> holderGrid = game.getHolderGrid();
        synchronized (holderGrid) {
            double prob = 0.25 * (double) (numPlayers / (itemsOnGrid.size() + 1));
            boolean addingRandomResource = false;
            FieldResource fr;
            Random r = new Random();
            double randomValue = r.nextDouble(); // TODO this will be every second
            if (randomValue <= prob) {
                // add a random resource
                addingRandomResource = true;
                double itemType = (Math.random() * (4));
                if (itemType >= 0 && itemType < 1) {
                    fr = new Clay();
                } else if (itemType >= 1 && itemType < 2) {
                    fr = new Iron();
                } else if (itemType >= 2 && itemType < 3) {
                    fr = new Rock();
                } else {
                    fr = new Thingamajig();
                }

                boolean added = false;
                while (!added) {
                    int location = (int) (Math.random() * (256));
                    if (!holderGrid.get(location).isEntityPresent()) {
                        holderGrid.get(location).setFieldEntity(fr);
                        itemsOnGrid.put(location + 1, fr);
                        eventManager.addEvent(new AddResourceEvent(location + 1, fr.toString()));
                        added = true;
                    }
                }
                added = false;
            }
        }
    }
}
