package com.example.ef;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Timer;
import java.util.TimerTask;

/**
 * דף טעינת נתונים היוצג לאחר סיום תהליך ההרשמה
 */

public class ProcessDataLoading extends AppCompatActivity {

    int counter = 0;//אתחול מונה אחוזי טעינה

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_data_loading);

        prog();
        YoYo.with(Techniques.Pulse)
                .duration(900)
                .repeat(4)
                .playOn(findViewById(R.id.title_process));
    }

    //פונקציה האחראית על הצגת הפס טעינה והצגת אחוזי הטעינה המתעדכנים של הדף
    private void prog() {
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {//הצהרה על אובייקט של טיימר שיופעל לביצוע הפעולת טעינה פעם אחת
            @Override
            public void run() {
                counter++;
                if (counter == 100) {//אם הוא הגיע למס' הטיימר יפסיק את הפעולה שלו
                    timer.cancel();
                    startActivity(new Intent(ProcessDataLoading.this, FragManager.class));
                    ProcessDataLoading.this.finish();//יופעל פעם אחת בלבד לאחר התחברות לאפליקציה
                }
            }
        };
        timer.schedule(timerTask, 0, 30);//קביעת זמן הטעינה
    }
}