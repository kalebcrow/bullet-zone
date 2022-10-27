package edu.unh.cs.cs619.bulletzone.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import edu.unh.cs.cs619.bulletzone.BulletZoneServer;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BulletZoneServer.class})
public class GamesControllerTest {

    MockHttpServletRequest request;

    MockMvc mockMvc;


    @Mock
    private InMemoryGameRepository repo;

    @InjectMocks
    private GamesController gamesController;



    @Before
    public void setUp() throws Exception {
        //MockitoAnnotations.openMocks(this);
        repo = mock(InMemoryGameRepository.class);
        gamesController = mock(GamesController.class);
        mockMvc = MockMvcBuilders.standaloneSetup(gamesController).build();
    }


    @Test
    public void testJoinGame() throws Exception {
        //joins game and returns tank id
        mockMvc.perform(post("/games").with(remoteAddr("100.20.10.0"))).andExpect(status().isCreated());
    }

    @Test
    public void testGetGrid() throws Exception {
        //performs get request on server gets ok response back
       //MvcResult result = mockMvc.perform(get("/games").accept(MediaType.APPLICATION_JSON_VALUE).with(remoteAddr("100.20.10.0"))).andExpect(status().isOk());
       //String tank = result.getResponse().toString();
    }

    @Test
    public void testLeaveGame() throws Exception {
        //tests leave when tank is not present
        mockMvc.perform(delete("/0/leave")).andExpect(status().isNotFound());
        //adds tank
        mockMvc.perform(post("/games").with(remoteAddr("100.20.10.0")));
        //checks tank was deleted
        mockMvc.perform(delete("/{id}/leave",0)).andExpect(status().isOk());
    }
    /*
    @Test
    public void test
    */

    private static RequestPostProcessor remoteAddr(final String remoteAddr) { // it's nice to extract into a helper
        return new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setRemoteAddr(remoteAddr);
                return request;
            }
        };
    }
}