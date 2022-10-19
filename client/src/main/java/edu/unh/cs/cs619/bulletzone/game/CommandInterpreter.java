package edu.unh.cs.cs619.bulletzone.game;


import org.json.JSONObject;

import java.util.LinkedList;

import edu.unh.cs.cs619.bulletzone.game.commands.Commands;

public class CommandInterpreter {

    private static volatile CommandInterpreter INSTANCE = null;

    public LinkedList<Commands> getHistory() {
        return history;
    }

    private LinkedList<Commands> history;

    private CommandInterpreter() {
        history = new LinkedList<>();
    }

    public static CommandInterpreter getCommandInterpreter() {
        if(INSTANCE == null) {
            synchronized (CommandInterpreter.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CommandInterpreter();
                }
            }
        }
        return INSTANCE;
    }


    public void interpret(JSONObject jsonObject) {

    }

}
