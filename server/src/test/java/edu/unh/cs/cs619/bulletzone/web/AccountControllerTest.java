package edu.unh.cs.cs619.bulletzone.web;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import edu.unh.cs.cs619.bulletzone.BulletZoneServer;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;
import edu.unh.cs.cs619.bulletzone.repository.DataRepository;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BulletZoneServer.class})
public class AccountControllerTest {
    // for api calls
    MockMvc mockMvc;
    @InjectMocks
    private AccountController accountController;

    // for mock account controller
    @Mock
    private DataRepository data;

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
    public void Register_NewUserAndPasswordPutRequest_ReturnsSuccessful() throws Exception {
        // perform put register request and check status 201
        mockMvc.perform(put("/games/account/register/newuser/newpass"))
                .andExpect(status().isCreated());
    }

    @Test
    public void Login_NewUserAndPasswordPutRequest_ReturnsSuccessful() throws Exception {
        // perform put login request and check status 201
        mockMvc.perform(put("/games/account/login/newuser/newpass"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void Balance_BalancePutRequest_ReturnsSuccessful() throws Exception {
        // perform put login request and check status 201
        mockMvc.perform(put("/games/account/newuser/balance"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void Items_ItemsPutRequest_ReturnsSuccessful() throws Exception {
        // perform put login request and check status 201
        mockMvc.perform(put("/games/account/newuser/items"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void Register_CheckUserCreation_GameUserValidated() throws Exception {
        String username = "newuser";
        String password = "newpass";
        data = mock(DataRepository.class);
        AccountController ac = new AccountController(data);
        ac.register(username, password);

        verify(data).validateUser(username,password, true);
    }

    @Test
    public void Login_CheckUserCreation_GameUserValidated() throws Exception {
        String username = "newuser";
        String password = "newpass";
        data = mock(DataRepository.class);
        AccountController ac = new AccountController(data);
        ac.login(username, password);

        verify(data).validateUser(username,password, false);
    }

    @Test
    public void Items_CheckItemList_ItemListIsCreated() throws Exception {
        String username = "newuser";
        String password = "newpass";
        data = mock(DataRepository.class);
        AccountController ac = new AccountController(data);
        ac.register(username, password);
        ac.items("newuser");

        verify(data).getUserItem("newuser");
    }

    @Test
    public void Balance_CheckBalance_BalanceIsCalled() throws Exception {
        String username = "newuser";
        String password = "newpass";
        data = mock(DataRepository.class);
        AccountController ac = new AccountController(data);
        ac.register(username, password);
        ac.balance("newuser");

        verify(data).getUserAccountBalance("newuser");
    }
}