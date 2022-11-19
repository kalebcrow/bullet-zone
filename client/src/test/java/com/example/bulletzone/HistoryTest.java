package com.example.bulletzone;

import static org.mockito.Mockito.verify;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

import edu.unh.cs.cs619.bulletzone.game.events.GridEvent;
import edu.unh.cs.cs619.bulletzone.replay.HistoryInterpreter;
import edu.unh.cs.cs619.bulletzone.replay.HistoryReader;
import edu.unh.cs.cs619.bulletzone.replay.HistoryWriter;

public class HistoryTest {

    private static HistoryWriter writerTesting;
    private static HistoryReader readerTesting;

    @Mock
    OutputStreamWriter mockOutputStreamWriter;
    OutputStreamWriter mockOutputStreamWriter2;

    @Test
    public void historyGetsCorrectHistory() {
        mockOutputStreamWriter = Mockito.mock((OutputStreamWriter.class));
        mockOutputStreamWriter2 = Mockito.mock(OutputStreamWriter.class);
        int[][][] array = new int[16][16][3];
        int value = 0;
        for (int i = 0; i < 16; i++) {
            for (int ii = 0; ii < 16; ii++) {
                for (int iii= 0; iii < 3; iii++) {
                    array[i][ii][iii] = value;
                    value++;
                }
            }
        }

        LinkedList<GridEvent> events = new LinkedList<>();

        writerTesting = new HistoryWriter(events, array);

        Gson gson = new Gson();
        writerTesting.WriteArray(mockOutputStreamWriter);
        try {
            verify(mockOutputStreamWriter).write(gson.toJson(array));
            verify(mockOutputStreamWriter).close();

        }
        catch (IOException e) {

        }

        writerTesting.WriteHistory(mockOutputStreamWriter2);
        try {
            verify(mockOutputStreamWriter2).write(gson.toJson(events));
            verify(mockOutputStreamWriter2).close();

        }
        catch (IOException e) {

        }
    }
}
