package edu.unh.cs.cs619.bulletzone;

import org.graalvm.compiler.lir.sparc.SPARCMove;

import java.util.ArrayList;

public class CommandInterpreter {

    private ArrayList<Command> sequence;

    public CommandInterpreter(ArrayList<Command> sequence){

        this.sequence = sequence;

    }

    public boolean executeSequence(){

        System.out.println("Command Interpreter: Number of commands = " + sequence.size());

        for(Command cmd: sequence){
            System.out.println(cmd.getConcreteCommandType());
        }

        boolean returnBoolean = true;
        for(Command cmd : sequence){

            //prevent vehicle from hitting something and taking damage
            if(cmd.getConcreteCommandType() == "Move"){
                if(cmd.tank.getParent().getNeighbor(cmd.direction).isEntityPresent() == true){
                    System.out.println("entity detected");
                    return false;
                }

            }

            //dynamically set delay based on tank type and
            //cmd.tank.getParent().getTerrain?

            returnBoolean = cmd.execute();
            if(returnBoolean == false){
                break;
            }

        }

        return returnBoolean;

    }

}
