package com.example.ef;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * דף המאפשר למשתמש בעת תהליך הרישום לאפליקציה להכניס את מטרתו הגופנית אליה ירצה להגיע בעת השימוש
 * באפליקציה
 */

public class ActivityGoal extends AppCompatActivity implements View.OnClickListener {
    Button btnWelcomeLoseWeight, btnWelcomeMaintainMuscleMass, btnWelcomeGainMuscle;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityGoal.this, ActivityWeight.class));
        this.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        initViews();

        initButtons();
    }

    /**
     * אתחול הכפתורים בדף
     */
    private void initButtons() {
        btnWelcomeLoseWeight.setOnClickListener(this);
        btnWelcomeMaintainMuscleMass.setOnClickListener(this);
        btnWelcomeGainMuscle.setOnClickListener(this);
    }

    /**
     * פונקציה לבדיקת תקינות גיל המשתמש
     */
    private void initViews() {
        btnWelcomeLoseWeight = findViewById(R.id.btnWelcomeLoseWeight);
        btnWelcomeMaintainMuscleMass = findViewById(R.id.btnWelcomeMaintainMuscleMass);
        btnWelcomeGainMuscle = findViewById(R.id.btnWelcomeGainMuscle);
    }

    @SuppressLint("NonConstantResourceId")//התעלמות וסינון הערות מהמערכת
    @Override
    public void onClick(View v) {
        Intent i = new Intent(ActivityGoal.this, ActivityUserPhone.class);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        switch (v.getId()) {
            case R.id.btnWelcomeLoseWeight:
                //save stage to local storage
                editor.putString("goal", "Lose Weight");
                editor.apply();
                break;

            case R.id.btnWelcomeMaintainMuscleMass:
                //save stage to local storage
                editor.putString("goal", "Maintain Muscle Mass");
                editor.apply();
                break;

            case R.id.btnWelcomeGainMuscle:
                //save stage to local storage
                editor.putString("goal", "Gain Muscle");
                editor.apply();
                break;
        }

        startActivity(i);

        startActivity(new Intent(ActivityGoal.this, ActivityLevel.class));
        this.overridePendingTransition(0, 0);
    }
}