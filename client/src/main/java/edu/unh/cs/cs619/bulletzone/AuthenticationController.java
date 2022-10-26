package edu.unh.cs.cs619.bulletzone;

import android.content.Context;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;

import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongWrapper;

@EBean
public class AuthenticationController {
    @RestService
    BulletZoneRestClient restClient;

    /**
     * Constructor for InputHandler
     * [Feel free to add arguments and initialization as needed]
     */
    public AuthenticationController() {
        //note: any work that needs to be done with an annotated item like @RestService or @Bean
        //      will not work here, but should instead go into a method below marked
        //      with the @AfterInject annotation.
    }

    @AfterInject
    public void afterInject() {
        //Any initialization involving components annotated with things like @RestService or @Bean
        //goes here.
    }
    
    /**
     * Uses restClient to login.
     *
     * @param username Username provided by user.
     * @param password Password for account provided by user.
     */
    public long login(String username, String password) {
        LongWrapper result = restClient.login(username, password);
        if (result == null) {
            return -1;
        }
        return result.getResult();
    }

    /**
     * Uses restClient to register.
     *
     * @param username New username provided by user.
     * @param password Password for new account provided by user.
     */
    public boolean register(String username, String password) {
        BooleanWrapper result = restClient.register(username, password);
        if (result == null) {
            return false;
        }
        return result.isResult();
    }

    /**
     * Uses restClient to get user balance
     *
     * @param username New username provided by user.
     */
    public long balance(String username) {
        LongWrapper result = restClient.balance(username);
        if (result == null) {
            return -1;
        }
        return result.getResult();
    }

    /**
     * Uses restClient to get user tank.
     *
     * @param username New username provided by user.
     */
    public String items(String username) {
        String result = restClient.items(username);
        if (result == null) {
            return "";
        }
        return result;
    }

    /**
     * Helper for testing
     *
     * @param restClientPassed tested restClient
     */
    public void initialize(BulletZoneRestClient restClientPassed) {
        restClient = restClientPassed;
    }

}
