package com.example.ef;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

/**
 * דף המאפשר למשתמש בעת תהליך הרישום לאפליקציה להכניס את מספר הארוחות שהוא רוצה לאכול ביום
 * במידה ולא ידע כמה ארוחות יצטרך - תתבצע בחירה רנדומלית עבורו
 */

public class ActivityMeals extends AppCompatActivity implements View.OnClickListener {

    Button btnWelcomeThreeMeals, btnWelcomeFiveMeals, btnWelcomeSevenMeals, btnWelcomeCoachDecide;
    Random r;
    int randomNumber;
    int[] mealsArr = {3, 5, 7};

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityMeals.this, ActivityLevel.class));
        this.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);
        initViews();
        initButtons();
        r = new Random();
    }

    /**
     * אתחול הכפתורים בדף
     */
    private void initButtons() {
        btnWelcomeThreeMeals.setOnClickListener(this);
        btnWelcomeFiveMeals.setOnClickListener(this);
        btnWelcomeSevenMeals.setOnClickListener(this);
        btnWelcomeCoachDecide.setOnClickListener(this);
    }

    /**
     * אתחול המשתנים בדף
     */
    private void initViews() {
        btnWelcomeThreeMeals = findViewById(R.id.btnWelcomeThreeMeals);
        btnWelcomeFiveMeals = findViewById(R.id.btnWelcomeFiveMeals);
        btnWelcomeSevenMeals = findViewById(R.id.btnWelcomeSevenMeals);
        btnWelcomeCoachDecide = findViewById(R.id.btnWelcomeCoachDecide);
    }

    @SuppressLint("NonConstantResourceId")//התעלמות וסינון הערות מהמערכת
    @Override
    public void onClick(View v) {
        Intent i = new Intent(ActivityMeals.this, ActivityUserPhone.class);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        switch (v.getId()) {
            case R.id.btnWelcomeThreeMeals:
                //save stage to local storage
                editor.putInt("meals", 3);
                editor.apply();
                break;

            case R.id.btnWelcomeFiveMeals:
                //save stage to local storage
                editor.putInt("meals", 5);
                editor.apply();
                break;

            case R.id.btnWelcomeSevenMeals:
                //save stage to local storage
                editor.putInt("meals", 7);
                editor.apply();
                break;

            case R.id.btnWelcomeCoachDecide:
                randomNumber = r.nextInt(3);
                //save stage to local storage
                editor.putInt("meals", mealsArr[randomNumber]);
                editor.apply();
                break;
        }

        startActivity(i);

        startActivity(new Intent(ActivityMeals.this, ActivityUserPhone.class));
        this.overridePendingTransition(0, 0);
    }
}