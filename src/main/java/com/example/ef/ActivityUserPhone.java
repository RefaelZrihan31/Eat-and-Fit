package com.example.ef;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * דף המאפשר למשתמש בעת תהליך הרישום לאפליקציה להכניס את מספר הטלפון שלו
 */

public class ActivityUserPhone extends AppCompatActivity implements View.OnClickListener {

    Button btnWelcomePhone;
    EditText userPhoneInput;
    String phoneNumber;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityUserPhone.this, ActivityMeals.class));
        this.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_phone);

        initViews();

        initButtons();
    }

    /**
     * אתחול הכפתורים בדף
     */
    private void initButtons() {
        btnWelcomePhone.setOnClickListener(this);
    }

    /**
     * אתחול המשתנים בדף
     */
    private void initViews() {
        btnWelcomePhone = findViewById(R.id.btnWelcomePhone);
        userPhoneInput = findViewById(R.id.userPhoneInput);
    }

    @SuppressLint("NonConstantResourceId")//התעלמות וסינון הערות מהמערכת
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnWelcomePhone:
                checkPhoneNumber();
                break;
        }
    }

    /**
     * פונקציה לבדיקת תקינות מספר הטלפון של המשתמש
     */
    private void checkPhoneNumber() {
        phoneNumber = userPhoneInput.getText().toString();
        Intent i = new Intent(this, ActivityVerifyUserPhone.class);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if (!phoneNumber.trim().isEmpty()) {
            if ((phoneNumber.trim()).length() == 10) {
                //save stage to local storage
                editor.putString("userphone", phoneNumber);
                editor.apply();
                startActivity(i);
                startActivity(new Intent(ActivityUserPhone.this, ActivityVerifyUserPhone.class));
                this.overridePendingTransition(0, 0);
            } else {
                Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        }
    }
}

