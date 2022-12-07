package edu.unh.cs.cs619.bulletzone.ui;

import android.widget.Button;

public class FireButtonState extends ButtonState{

    public FireButtonState(Button inputButton){
        super(inputButton);
    }

    @Override
    public void Handle(int[] argument) {

        if(argument[2] == 1){
            savedButton.setEnabled(true);
        }
        else{
            savedButton.setEnabled(false);
        }

    }
}
