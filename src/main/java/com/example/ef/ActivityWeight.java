package com.example.ef;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * דף המאפשר למשתמש בעת תהליך הרישום לאפליקציה להכניס את משקלו
 */

public class ActivityWeight extends AppCompatActivity implements View.OnClickListener {

    Button btnWelcomeWeight;
    TextView npWeightValue;
    NumberPicker npWeight;
    String weightToCheck;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityWeight.this, ActivityHeight.class));
        this.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        initViews();

        initButtons();

        //Set the minimum value of NumberPicker
        npWeight.setMinValue(50);
        //Specify the maximum value/number of NumberPicker
        npWeight.setMaxValue(200);

        //Gets whether the selector wheel will not wraps when reaching the min/max value.
        npWeight.setWrapSelectorWheel(false);

        //Set a value change listener for NumberPicker
        npWeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @SuppressLint("SetTextI18n")//התעלמות וסינון הערות מהמערכת
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Display the newly selected number from picker
                npWeightValue.setText("" + newVal + " kg");
            }
        });
    }

    /**
     * אתחול הכפתורים בדף
     */
    private void initButtons() {
        btnWelcomeWeight.setOnClickListener(this);
    }

    /**
     * אתחול המשתנים בדף
     */
    private void initViews() {
        btnWelcomeWeight = findViewById(R.id.btnWelcomeWeight);
        npWeightValue = findViewById(R.id.npWeightValue);
        npWeight = (NumberPicker) findViewById(R.id.npWeight);
    }

    @SuppressLint("NonConstantResourceId")//התעלמות וסינון הערות מהמערכת
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnWelcomeWeight:
                checkWeight();
                break;
        }
    }

    /**
     * פונקציה לבדיקת תקינות משקל המשתמש
     */
    public void checkWeight() {
        Intent i = new Intent(ActivityWeight.this, ActivityUserPhone.class);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        weightToCheck = npWeightValue.getText().toString();
        if (weightToCheck.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Weight")
                    .setMessage("Select Your Weight")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return;
        }
        //save stage to local storage
        editor.putString("weight", weightToCheck.replace(" kg", ""));
        editor.apply();

        startActivity(i);

        startActivity(new Intent(ActivityWeight.this, ActivityGoal.class));
        this.overridePendingTransition(0, 0);
    }
}

