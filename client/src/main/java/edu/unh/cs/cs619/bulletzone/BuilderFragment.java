package edu.unh.cs.cs619.bulletzone;

import android.app.DialogFragment;
import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.support.v4.app.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import edu.unh.cs.cs619.bulletzone.game.TankController;

public class BuilderFragment extends DialogFragment {

    private String TAG = "BuilderFragment";

    private int noEdits = 0;
    private int returnedAction = 0;
    //0 means dismantle
    //10 means build indestructible wall
    //11 means build road
    //12 means build wall

    private RadioGroup radioGroup;
    private Button mConfirmChoice;
    private Spinner mBuilderSpinner;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.builder_fragment, container, false);

        mBuilderSpinner = (Spinner) rootView.findViewById(R.id.builderSpinner);
        String[] buildOptions = {"Indestructible Wall", "Road", "Wall", "Decking", "Factory"};
        ArrayAdapter aa = new ArrayAdapter(context, android.R.layout.simple_spinner_item, buildOptions);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBuilderSpinner.setAdapter(aa);

        mConfirmChoice = (Button) rootView.findViewById(R.id.builderButtonConfirm);

        radioGroup = (RadioGroup) rootView.findViewById(R.id.builderRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch(checkedId) {
                    case R.id.builderRadioDismantle:
                        noEdits++;
                        Log.d(TAG, "Dismantle Selected");
                        mBuilderSpinner.setVisibility(View.INVISIBLE);
                        returnedAction = 0;
                        break;
                    case R.id.builderRadioBuild:
                        noEdits++;
                        Log.d(TAG, "Build Selected");
                        mBuilderSpinner.setSelection(0);
                        mBuilderSpinner.setVisibility(View.VISIBLE);
                        returnedAction = 10;
                        break;
                }
            }
        });

        mBuilderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                switch (position){
                    case 0:
                        noEdits++;
                        Log.d(TAG, "BuilderSpinner: Indestructible Wall Selected");
                        returnedAction = 10;
                        break;
                    case 1:
                        noEdits++;
                        Log.d(TAG, "BuilderSpinner: Road Selected");
                        returnedAction = 11;
                        break;
                    case 2:
                        noEdits++;
                        Log.d(TAG, "BuilderSpinner: Wall Selected");
                        returnedAction = 12;
                        break;
                    case 3:
                        noEdits++;
                        Log.d(TAG, "BuilderSpinner: Decking Selected");
                        returnedAction = 13;
                        break;
                    case 4:
                        noEdits++;
                        Log.d(TAG, "BuilderSpinner: Factory Selected");
                        returnedAction = 14;
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //?
            }

        });

        mConfirmChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send some request to rest to check if requested action can be completed
                if(noEdits == 1){
                    returnedAction = 0;
                }

                Log.d(TAG, "Confirmed Choice: " + returnedAction);
                TankController.getTankController().builderActions(returnedAction);
                getFragmentManager().beginTransaction().remove(BuilderFragment.this).commit();
            }
        });


        return rootView;
    }

    public void setContext(Context context){
        this.context = context;
    }

}
