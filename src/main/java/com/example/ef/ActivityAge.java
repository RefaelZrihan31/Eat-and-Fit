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
 * דף המאפשר למשתמש בעת תהליך הרישום לאפליקציה להכניס את גילו
 */
public class ActivityAge extends AppCompatActivity implements View.OnClickListener {

    Button btnWelcomeAge;
    TextView npAgeValue;
    NumberPicker npAge;
    String ageToCheck;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityAge.this, ActivityGender.class));
        this.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age);

        initViews();

        initButtons();

        //Set the minimum value of NumberPicker
        npAge.setMinValue(18);

        //Specify the maximum value/number of NumberPicker
        npAge.setMaxValue(120);

        //Gets whether the selector wheel will not wraps when reaching the min/max value.
        npAge.setWrapSelectorWheel(false);

        //Set a value change listener for NumberPicker
        npAge.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @SuppressLint("SetTextI18n")//התעלמות וסינון הערות מהמערכת
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Display the newly selected number from picker
                npAgeValue.setText("" + newVal);
            }
        });
    }

    /**
     * אתחול הכפתורים בדף
     */
    private void initButtons() {
        btnWelcomeAge.setOnClickListener(this);
    }

    /**
     * אתחול המשתנים בדף
     */
    private void initViews() {
        btnWelcomeAge = findViewById(R.id.btnWelcomeAge);
        npAgeValue = findViewById(R.id.npAgeValue);
        npAge = (NumberPicker) findViewById(R.id.npAge);
    }

    @SuppressLint("NonConstantResourceId")//התעלמות וסינון הערות מהמערכת
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnWelcomeAge:
                checkAge();
                break;
        }
    }

    /**
     * פונקציה לבדיקת תקינות גיל המשתמש
     */
    public void checkAge() {
        Intent i = new Intent(ActivityAge.this, ActivityUserPhone.class);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        ageToCheck = npAgeValue.getText().toString();
        if (ageToCheck.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Age")
                    .setMessage("Select Your Age")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return;

        }
        //save stage to local storage
        editor.putString("age", ageToCheck);
        editor.apply();

        startActivity(i);

        startActivity(new Intent(ActivityAge.this, ActivityHeight.class));
        this.overridePendingTransition(0, 0);
    }
}


