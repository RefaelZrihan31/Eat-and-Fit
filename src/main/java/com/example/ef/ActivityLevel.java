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
 * דף המאפשר למשתמש בעת תהליך הרישום לאפליקציה להכניס את רמת הפעילות הגופנית שלו
 */

public class ActivityLevel extends AppCompatActivity implements View.OnClickListener {

    Button btnWelcomeNotVeryActive, btnWelcomeLightlyActive, btnWelcomeActive, btnWelcomeVeryActive;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityLevel.this, ActivityGoal.class));
        this.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        initViews();

        initButtons();
    }

    /**
     * אתחול הכפתורים בדף
     */
    private void initButtons() {
        btnWelcomeNotVeryActive.setOnClickListener(this);
        btnWelcomeLightlyActive.setOnClickListener(this);
        btnWelcomeActive.setOnClickListener(this);
        btnWelcomeVeryActive.setOnClickListener(this);
    }

    /**
     * אתחול המשתנים בדף
     */
    private void initViews() {
        btnWelcomeNotVeryActive = findViewById(R.id.btnWelcomeNotVeryActive);
        btnWelcomeLightlyActive = findViewById(R.id.btnWelcomeLightlyActive);
        btnWelcomeActive = findViewById(R.id.btnWelcomeActive);
        btnWelcomeVeryActive = findViewById(R.id.btnWelcomeVeryActive);
    }

    @SuppressLint("NonConstantResourceId")//התעלמות וסינון הערות מהמערכת
    @Override
    public void onClick(View v) {
        Intent i = new Intent(ActivityLevel.this, ActivityUserPhone.class);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        switch (v.getId()) {
            case R.id.btnWelcomeNotVeryActive:
                //save stage to local storage
                editor.putString("activityLevel", "Not Very Active");
                editor.apply();
                break;

            case R.id.btnWelcomeLightlyActive:
                //save stage to local storage
                editor.putString("activityLevel", "Lightly Active");
                editor.apply();
                break;

            case R.id.btnWelcomeActive:
                //save stage to local storage
                editor.putString("activityLevel", "Active");
                editor.apply();
                break;

            case R.id.btnWelcomeVeryActive:
                //save stage to local storage
                editor.putString("activityLevel", "Very Active");
                editor.apply();
                break;
        }

        startActivity(i);

        startActivity(new Intent(ActivityLevel.this, ActivityMeals.class));
        this.overridePendingTransition(0, 0);
    }
}