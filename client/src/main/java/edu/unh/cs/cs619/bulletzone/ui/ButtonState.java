package edu.unh.cs.cs619.bulletzone.ui;

import android.widget.Button;

public class ButtonState {

    Button savedButton;

    public ButtonState(Button inputButton){
        savedButton = inputButton;
    }

    public void Handle(int[] argument){}

    public void setEnabled(){
        savedButton.setEnabled(true);
    }

}
