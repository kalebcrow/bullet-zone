package edu.unh.cs.cs619.bulletzone;

import org.graalvm.compiler.lir.sparc.SPARCMove;

import java.util.ArrayList;

public class CommandInterpreter {

    private ArrayList<Command> sequence;

    public CommandInterpreter(ArrayList<Command> sequence){

        this.sequence = sequence;

    }

    public boolean executeSequence(){

        boolean returnBoolean = true;
        for(Command cmd : sequence){

            //prevent vehicle from hitting something and taking damage
            if(cmd.getConcreteCommandType() == "Move"){
                if(cmd.tank.getParent().getNeighbor(cmd.direction).isPresent() == true){
                    return false;
                }

            }

            //dynmaically set delay based on tank type and
            //cmd.tank.getParent().getTerrain?

            returnBoolean = cmd.execute();
            if(returnBoolean == false){
                break;
            }

        }

        return returnBoolean;

    }

}
