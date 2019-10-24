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
import com.google.common.base.Preconditions;

import edu.unh.cs.cs619.bulletzone.datalayer.BulletZoneData;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.repository.GameRepository;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongWrapper;
import edu.unh.cs.cs619.bulletzone.util.StringArrayWrapper;

@RestController
@RequestMapping(value = "/games/account")
public class AccountController {
    private static final Logger log = LoggerFactory.getLogger(GamesController.class);

    private final GameRepository gameRepository;

    @Autowired
    public AccountController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    /**
     * Handles a PUT request to register a new user account
     *
     * @param name The username
     * @param password The password
     * @return a response w/ success boolean
     */
    @RequestMapping(method = RequestMethod.PUT, value = "register/{name}/{password}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public ResponseEntity<BooleanWrapper> reqRegisterNew(@PathVariable String name, @PathVariable String password)
    {
        // Log the request
        log.debug("Register '" + name + "' with password '" + password + "'");
        // Return the response (true if account created)
        /*
        return new ResponseEntity<BooleanWrapper>(new BooleanWrapper(
                TODO: something that invokes users.createUser(name, password) and does other setup
                ),
                HttpStatus.ACCEPTED);
         */
        return null;
    }

    /**
     * Handles a PUT request to login a user
     *
     * @param name The username
     * @param password The password
     * @return a response w/ the user ID (or -1 if invalid)
     */
    @RequestMapping(method = RequestMethod.PUT, value = "login/{name}/{password}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public ResponseEntity<LongWrapper> reqLogin(@PathVariable String name, @PathVariable String password)
    {
        // Log the request
        log.debug("Login '" + name + "' with password '" + password + "'");
        // Return the response (return user ID if valid login)
        /*
        return new ResponseEntity<LongWrapper>(new LongWrapper(
                TODO: something that invokes users.validateLogin(name, password)
                ),
                HttpStatus.ACCEPTED);
         */
        return null;
    }

    /**
     * Handles a GET request to return all category
     * @return a response includes String array
     */
    @RequestMapping(method = RequestMethod.GET, value = "getCategories", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public ResponseEntity<StringArrayWrapper> getCategory(){
        // Log the request
        log.debug("getCategories");
        // Return the response (list of strings)
        /*
        return new ResponseEntity<StringArrayWrapper>(new StringArrayWrapper(
                TODO: something that invokes types.getCategories()
                ),
                HttpStatus.ACCEPTED);
         */
        return null;
    }

    /**
     * Handles a GET request to return different component types in a given category
     * @param category The category
     * @return a response includes String array
     */
    @RequestMapping(method = RequestMethod.GET, value = "getTypes/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public ResponseEntity<StringArrayWrapper> getItem(@PathVariable String category){
        // Log the request
        log.debug("getTypes '" + category + "'");
        // Return the response (list of strings)
        /*
        return new ResponseEntity<StringArrayWrapper>(new StringArrayWrapper(
                TODO: something that invokes types.get*()
                ),
                HttpStatus.ACCEPTED);
         */
        return null;
    }
}
