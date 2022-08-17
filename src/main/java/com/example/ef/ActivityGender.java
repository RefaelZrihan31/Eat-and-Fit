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
 * דף המאפשר למשתמש בעת תהליך הרישום לאפליקציה להכניס את המין
 */
public class ActivityGender extends AppCompatActivity implements View.OnClickListener {
    Button btnWelcomeMale, btnWelcomeFemale;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityGender.this, Welcome.class));
        this.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);

        initViews();

        initButtons();
    }

    /**
     * אתחול הכפתורים בדף
     */
    private void initButtons() {
        btnWelcomeMale.setOnClickListener(this);
        btnWelcomeFemale.setOnClickListener(this);
    }

    /**
     * אתחול המשתנים בדף
     */
    private void initViews() {
        btnWelcomeMale = findViewById(R.id.btnWelcomeMale);
        btnWelcomeFemale = findViewById(R.id.btnWelcomeFemale);
    }

    @SuppressLint("NonConstantResourceId")//התעלמות וסינון הערות מהמערכת
    @Override
    public void onClick(View v) {
        Intent i = new Intent(ActivityGender.this, ActivityUserPhone.class);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        switch (v.getId()) {
            case R.id.btnWelcomeMale:
                //save stage to local storage
                editor.putString("gender", "Male");
                editor.apply();
                break;

            case R.id.btnWelcomeFemale:
                //save stage to local storage
                editor.putString("gender", "Female");
                editor.apply();
                break;
        }

        startActivity(i);

        startActivity(new Intent(ActivityGender.this, ActivityAge.class));
        this.overridePendingTransition(0, 0);
    }
}