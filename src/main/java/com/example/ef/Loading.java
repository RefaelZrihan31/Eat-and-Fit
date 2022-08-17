package com.example.ef;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

/**
 * דף פתיחה - טעינה ראשונית של האפליקציה
 */

public class Loading extends AppCompatActivity {

    int counter = 0;//אתחול מונה אחוזי טעינה
    FirebaseAuth mAuth;
    String currentUserPhoneId;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        prog();
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
                    mAuth = FirebaseAuth.getInstance();
                    currentUser = mAuth.getCurrentUser();

                    //במידה ולא קיים משתמש עם פרטי המשתמש הקיימים בזיכרון המכשיר אל מול נתוני נתוני אחד המשתמשים
                    //הקיים שנלשף מתוך מסד הנתונים - המשתמש יועבר את תהליך ההרשמה
                    //אחרת ,המשתמש יכנס עם פרטיו אל תוך הדף הבית וידלג על תהליך ההרשמה המחודשת
                    if (currentUser != null) {
                        String phoneNumber = PreferenceManager.getDefaultSharedPreferences(Loading.this).getString("userphone", "");
                        String phoneNumberWithAreaCode = "+972" + phoneNumber.substring(1);
                        currentUserPhoneId = currentUser.getPhoneNumber();
                        if (phoneNumberWithAreaCode.equals(currentUserPhoneId)) {
                            startActivity(new Intent(Loading.this, FragManager.class));
                        } else {
                            startActivity(new Intent(Loading.this, Welcome.class));
                        }
                    } else {
                        startActivity(new Intent(Loading.this, Welcome.class));
                    }
                    Loading.this.finish();//יופעל פעם אחת בלבד לאחר התחברות לאפליקציה
                }
            }
        };
        timer.schedule(timerTask, 0, 20);//קביעת זמן הטעינה
    }
}

