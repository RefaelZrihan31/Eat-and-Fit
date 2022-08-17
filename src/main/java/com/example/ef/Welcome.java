package com.example.ef;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

/**
 * דף כניסה לתחילת רישום אל האפליקציה
 */
public class Welcome extends AppCompatActivity implements View.OnClickListener {

    Button btnWelcomeLetsGo;

    @Override
    public void onBackPressed() {
        finishAffinity();

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        YoYo.with(Techniques.RollIn)
                .duration(900)
                .playOn(findViewById(R.id.welcome_title));
        initViews();

        initButtons();
    }

    /**
     * אתחול הכפתורים בדף
     */
    private void initButtons() {
        btnWelcomeLetsGo.setOnClickListener(this);
    }

    /**
     * אתחול המשתנים בדף
     */
    private void initViews() {
        btnWelcomeLetsGo = findViewById(R.id.btnWelcomeLetsGo);
    }

    @SuppressLint("NonConstantResourceId")//התעלמות וסינון הערות מהמערכת
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnWelcomeLetsGo:
                startActivity(new Intent(Welcome.this, ActivityGender.class));
                this.overridePendingTransition(0, 0);
                break;
        }
    }
}


