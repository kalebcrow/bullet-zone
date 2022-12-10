package edu.unh.cs.cs619.bulletzone.ui;

import android.widget.Button;

import edu.unh.cs.cs619.bulletzone.game.TankController;

public class RightButtonState extends ButtonState{

    public RightButtonState(Button inputButton){
        super(inputButton);
    }

    @Override
    public void Handle(int[] argument) {

        if(TankController.getTankController().getTankOrientation() == 2){

            if(argument[0] == 1){
                savedButton.setEnabled(true);
            }
            else{
                savedButton.setEnabled(false);
            }

        }
        else{

            if(argument[1] == 1){
                savedButton.setEnabled(true);
            }
            else{
                savedButton.setEnabled(false);
            }

        }

    }

}
