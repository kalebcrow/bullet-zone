package com.example.bulletzone;

import com.squareup.otto.Bus;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import edu.unh.cs.cs619.bulletzone.game.BoardView;
import edu.unh.cs.cs619.bulletzone.replay.HistoryInterpreter;
import edu.unh.cs.cs619.bulletzone.replay.HistoryReader;
import edu.unh.cs.cs619.bulletzone.replay.HistoryWriter;

public class HistoryTest {

    @Mock
    HistoryInterpreter interpret;
    @Mock
    HistoryWriter write;
    @Mock
    HistoryReader read;

    BoardView testBoardView;
    @Mock
    Bus mBus;

    @BeforeClass
    public void setup() {
        interpret = new HistoryInterpreter();
    }



    @Test
    public void historyGetsCorrectHistory()
    {

    }

    @Test

}
