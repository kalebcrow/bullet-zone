package edu.unh.cs.cs619.bulletzone.ui;

import android.widget.Button;

import edu.unh.cs.cs619.bulletzone.game.TankController;

public class UpButtonState extends ButtonState{

    public UpButtonState(Button inputButton){
        super(inputButton);
    }

    @Override
    public void Handle(int[] argument) {

        //facing forwards, use first argument given in the array
        if(TankController.getTankController().getTankOrientation() == 0){

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
