package edu.unh.cs.cs619.bulletzone.web;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sun.org.apache.xpath.internal.operations.Bool;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import edu.unh.cs.cs619.bulletzone.BulletZoneServer;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;
import edu.unh.cs.cs619.bulletzone.repository.DataRepository;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BulletZoneServer.class})
public class AccountControllerTest {

    MockMvc mockMvc;
    @Mock
    private DataRepository data;
    @InjectMocks
    private AccountController accountController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    public void ConstructAccountController_UsingRootURL_IsNotFound() throws Exception {
        mockMvc.perform(put("/games/account"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void Register_NewUserAndPassword_RegisterNewUser() throws Exception {
        // perform put register request and check status 201
        mockMvc.perform(put("/games/account/register/newuser/newpass"))
                .andExpect(status().isCreated());
    }

    @Test
    public void Register_CheckUserNotNull_GameUserNotNull() throws Exception {
        // perform put register request and check status 201
        //MvcResult result = mockMvc.perform(put("/games/account/register/newuser/newpass"))
        //        .andExpect(status().isCreated())
        //        .andReturn();

        //String resultStr = result.getResponse().getContentAsString();
        //assertNull(resultStr);
        ResponseEntity<BooleanWrapper> registered = accountController.register("newuser", "newpass");
        assertNotNull(registered);
        //assertEquals(registered.getStatusCode(), 201);

        // check the new user is not null to check it was actually registered
        //GameUser gu = data.validateUser(null, "newuser", "newpass", false);
        //assertNull(gu);
    }

    @Test
    public void Login_NewUserAndPassword_RegisterNewUser() throws Exception {
        // perform put login request and check status 201
        mockMvc.perform(put("/games/account/login/newuser/newpass"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void Login_CheckUserNotNull_GameUserNotNull() throws Exception {
        // perform put request and check status 201
        mockMvc.perform(put("/games/account/login/newuser/newpass"))
                .andExpect(status().is2xxSuccessful());

        // check the new user is not null to check it was actually created / logged in
        GameUser gu = data.validateUser(null, "newuser", "newpass", false);
        assertNotNull(gu);
    }
}