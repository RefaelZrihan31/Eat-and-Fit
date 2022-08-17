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
 * דף המאפשר למשתמש בעת תהליך הרישום לאפליקציה להכניס את גובהו
 */

public class ActivityHeight extends AppCompatActivity implements View.OnClickListener {

    Button btnWelcomeHeight;
    TextView npHeightValue;
    NumberPicker npHeight;
    String heightToCheck;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityHeight.this, ActivityAge.class));
        this.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height);
        initViews();
        initButtons();

        //Set the minimum value of NumberPicker
        npHeight.setMinValue(140);

        //Specify the maximum value/number of NumberPicker
        npHeight.setMaxValue(240);

        //Gets whether the selector wheel will not wraps when reaching the min/max value.
        npHeight.setWrapSelectorWheel(false);

        //Set a value change listener for NumberPicker
        npHeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @SuppressLint("SetTextI18n")//התעלמות וסינון הערות מהמערכת
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Display the newly selected number from picker
                npHeightValue.setText("" + newVal + " cm");
            }
        });
    }

    /**
     * אתחול הכפתורים בדף
     */
    private void initButtons() {
        btnWelcomeHeight.setOnClickListener(this);
    }

    /**
     * אתחול המשתנים בדף
     */
    private void initViews() {
        btnWelcomeHeight = findViewById(R.id.btnWelcomeHeight);
        npHeightValue = findViewById(R.id.npHeightValue);
        npHeight = (NumberPicker) findViewById(R.id.npHeight);
    }

    @SuppressLint("NonConstantResourceId")//התעלמות וסינון הערות מהמערכת
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnWelcomeHeight:
                checkHeight();
                break;
        }
    }

    /**
     * פונקציה לבדיקת תקינות גובה המשתמש
     */
    public void checkHeight() {
        Intent i = new Intent(ActivityHeight.this, ActivityUserPhone.class);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        heightToCheck = npHeightValue.getText().toString();
        if (heightToCheck.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Height")
                    .setMessage("Select Your Height")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return;
        }
        //save stage to local storage
        editor.putString("height", heightToCheck.replace(" cm", ""));
        editor.apply();

        startActivity(i);

        startActivity(new Intent(ActivityHeight.this, ActivityWeight.class));
        this.overridePendingTransition(0, 0);
    }
}