package edu.unh.cs.cs619.bulletzone.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import edu.unh.cs.cs619.bulletzone.BulletZoneServer;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongWrapper;

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
        mockMvc.perform(post("/games").with(remoteAddr("100.0.0.0"))).andExpect(status().isCreated());
    }

    @Test(expected = TankDoesNotExistException.class)
    public void testLeaveGame() throws Exception {
        //tests leave when tank is not present
        mockMvc.perform(delete("/0/leave")).andExpect(status().isNotFound());
        request = new MockHttpServletRequest();
        request.setRemoteAddr("100.0.0.0");
        repo = new InMemoryGameRepository();
        gamesController = new GamesController(repo);
        ResponseEntity<LongWrapper> l = gamesController.join(request);
        //Leave game
        gamesController.leave(0);
        //test that tank does not exist
        gamesController.leave(0);
        //mockMvc.perform(delete("/{id}/leave",)).andExpect(status().isOk());
    }

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