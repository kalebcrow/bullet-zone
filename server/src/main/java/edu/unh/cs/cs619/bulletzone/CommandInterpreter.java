package edu.unh.cs.cs619.bulletzone;

import java.util.ArrayList;

public class CommandInterpreter {

    private ArrayList<Command> sequence;

    public CommandInterpreter(ArrayList<Command> sequence){

        this.sequence = sequence;

    }

    public boolean executeSequence(){

        boolean returnBoolean = true;
        for(Command cmd : sequence){

            returnBoolean = cmd.execute();
            if(returnBoolean == false){
                break;
            }

        }

        return returnBoolean;

    }

}
