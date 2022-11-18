package edu.unh.cs.cs619.bulletzone.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import javax.servlet.http.HttpServletRequest;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.BuildingDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.InvalidResourceTileType;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.repository.GameDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.repository.GameRepository;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.EventWrapper;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongArrayWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongWrapper;

@RestController
@RequestMapping(value = "/games")
class GamesController {

    private static final Logger log = LoggerFactory.getLogger(GamesController.class);

    private final GameRepository gameRepository;

    @Autowired
    public GamesController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @RequestMapping(method = RequestMethod.POST, value = "{userID}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    ResponseEntity<LongArrayWrapper> join(@PathVariable long userID, HttpServletRequest request) {
        Tank[] tank;
        try {
            tank = gameRepository.join(userID, request.getRemoteAddr());
            Long[] tankIds = new Long[3];
            for(int i=0;i<3;i++){
                tankIds[i] = tank[i].getId();
            }
            log.info("Player joined: tankId={} IP={}", tank[0].getId(), request.getRemoteAddr());

            return new ResponseEntity<LongArrayWrapper>(
                    new LongArrayWrapper(tankIds),
                    HttpStatus.CREATED
            );
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    ResponseEntity<GridWrapper> grid() {
        return new ResponseEntity<GridWrapper>(new GridWrapper(gameRepository.getGrid()), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/turn/{direction}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> turn(@PathVariable long tankId, @PathVariable byte direction)
            throws TankDoesNotExistException, LimitExceededException, IllegalTransitionException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(gameRepository.turn(tankId, Direction.fromByte(direction))),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/move/{direction}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> move(@PathVariable long tankId, @PathVariable byte direction)
            throws TankDoesNotExistException, LimitExceededException, IllegalTransitionException, GameDoesNotExistException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(gameRepository.move(tankId, Direction.fromByte(direction))),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/fire", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> fire(@PathVariable long tankId)
            throws TankDoesNotExistException, LimitExceededException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(gameRepository.fire(tankId, 1)),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/fire/{bulletType}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> fire(@PathVariable long tankId, @PathVariable int bulletType)
            throws TankDoesNotExistException, LimitExceededException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(gameRepository.fire(tankId, bulletType)),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "{tankId}/leave", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    HttpStatus leave(@PathVariable long[] tankId)
            throws TankDoesNotExistException {
        //System.out.println("Games Controller leave() called, tank ID: "+tankId);
        gameRepository.leave(tankId);
        return HttpStatus.OK;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleBadRequests(Exception e) {
        return e.getMessage();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/event/{timeStamp}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    ResponseEntity<EventWrapper> event(@PathVariable long timeStamp) {
        return new ResponseEntity<EventWrapper>(
                new EventWrapper(gameRepository.getEvents(timeStamp)),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/mine", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> mine(@PathVariable long tankId)
            throws TankDoesNotExistException, LimitExceededException, InvalidResourceTileType {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(gameRepository.mine(tankId)),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/build/{buildingType}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> build(@PathVariable long tankId, @PathVariable int buildingType)
            throws TankDoesNotExistException, LimitExceededException, InvalidResourceTileType, BuildingDoesNotExistException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(gameRepository.build(tankId,buildingType)),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/dismantle", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> dismantle(@PathVariable long tankId)
            throws TankDoesNotExistException, LimitExceededException, InvalidResourceTileType {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(gameRepository.dismantle(tankId)),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/moveTo/{desiredLocation}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> moveTo(@PathVariable long tankId, @PathVariable int desiredLocation)
            throws TankDoesNotExistException{
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(gameRepository.moveTo(tankId, desiredLocation)),
                HttpStatus.OK
        );
    }

}
