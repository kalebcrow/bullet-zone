package edu.unh.cs.cs619.bulletzone.repository;

import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankController;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;
import edu.unh.cs.cs619.bulletzone.model.events.AddTankEvent;
import edu.unh.cs.cs619.bulletzone.model.events.DestroyBulletEvent;
import edu.unh.cs.cs619.bulletzone.model.events.DestroyTankEvent;
import edu.unh.cs.cs619.bulletzone.model.events.DestroyWallEvent;
import edu.unh.cs.cs619.bulletzone.model.events.FireEvent;
import edu.unh.cs.cs619.bulletzone.model.events.GridEvent;
import edu.unh.cs.cs619.bulletzone.model.events.MoveBulletEvent;
import edu.unh.cs.cs619.bulletzone.model.events.MoveTankEvent;
import edu.unh.cs.cs619.bulletzone.model.events.TurnEvent;

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
    private static final int TANK_LIFE = 100;
    private final Timer timer = new Timer();
    private final AtomicLong idGenerator = new AtomicLong();
    private final Object monitor = new Object();
    private Game game = null;
    private int bulletDamage[]={10,30,50};
    //private int bulletDelay[]={500,1000,1500};
    private int trackActiveBullets[]={0,0};

    /**
     * Allows a new tank to join the game
     * @param ip holds players ip string from join request
     * @return A new player tank
     */
    @Override
    public Tank join(long userID, String ip) {
        synchronized (this.monitor) {
            Tank tank;
            if (game == null) {
                this.create();
            }

            if( (tank = game.getTank(ip)) != null){
                return tank;
            }

            Long tankId = this.idGenerator.getAndIncrement();

            tank = new Tank(userID, tankId, Direction.Up, ip);
            tank.setLife(TANK_LIFE);

            Random random = new Random();
            int x;
            int y;

            // This may run for forever.. If there is no free space. XXX
            for (; ; ) {
                x = random.nextInt(FIELD_DIM);
                y = random.nextInt(FIELD_DIM);
                FieldHolder fieldElement = game.getHolderGrid().get(x * FIELD_DIM + y);
                if (!fieldElement.isPresent()) {
                    fieldElement.setFieldEntity(tank);
                    tank.setParent(fieldElement);
                    break;
                }
            }

            game.addTank(ip, tank);
            game.addEvent(new AddTankEvent(x, y , tankId));

            return tank;
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
            checkNotNull(parent.getNeighbor(direction), "Neightbor is not available");

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

            int bulletId=0;
            if(trackActiveBullets[0]==0){
                bulletId = 0;
                trackActiveBullets[0] = 1;
            }else if(trackActiveBullets[1]==0){
                bulletId = 1;
                trackActiveBullets[1] = 1;
            }

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
}
