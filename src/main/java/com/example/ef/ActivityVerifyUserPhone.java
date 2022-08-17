package com.example.ef;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

/**
 * דף המאפשר למשתמש בעת תהליך הרישום לאפליקציה להכניס את קוד האימות שנשלח לו
 */

public class ActivityVerifyUserPhone extends AppCompatActivity implements View.OnClickListener {

    EditText userPhoneVerify;
    Button btnVerify;
    String verificationID, phoneNumber, userPhoneToDB, genderToDB, goalsToDB, activityToDB, ageToDB, heightToDB, weightToDB;
    int mealsToDB;
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_user_phone);

        initViews();

        initVarbs();

        initButtons();

        /**שליפת כל המידע של המשתמש מתוך תהליך ההרשמה השמור ב- Local Storage */
        phoneNumber = PreferenceManager.getDefaultSharedPreferences(this).getString("userphone", "");
        userPhoneToDB = "+972" + phoneNumber.substring(1);
        genderToDB = PreferenceManager.getDefaultSharedPreferences(this).getString("gender", "");
        ageToDB = PreferenceManager.getDefaultSharedPreferences(this).getString("age", "");
        heightToDB = PreferenceManager.getDefaultSharedPreferences(this).getString("height", "");
        weightToDB = PreferenceManager.getDefaultSharedPreferences(this).getString("weight", "");
        goalsToDB = PreferenceManager.getDefaultSharedPreferences(this).getString("goal", "");
        activityToDB = PreferenceManager.getDefaultSharedPreferences(this).getString("activityLevel", "");
        mealsToDB = PreferenceManager.getDefaultSharedPreferences(this).getInt("meals", 0);

        sendVerificationCode(phoneNumber);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
    }

    /**
     * אתחול האובייקטים בדף
     */
    private void initVarbs() {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * אתחול הכפתורים בדף
     */
    private void initButtons() {
        btnVerify.setOnClickListener(this);
    }

    /**
     * אתחול המשתנים בדף
     */
    private void initViews() {
        btnVerify = findViewById(R.id.btn_verify);
        userPhoneVerify = findViewById(R.id.user_phone_verify);
    }

    @SuppressLint("NonConstantResourceId")//התעלמות וסינון הערות מהמערכת
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_verify:
                String verify = userPhoneVerify.getText().toString();
                if (verify.isEmpty()) {
                    Toast.makeText(ActivityVerifyUserPhone.this, "Wrong OTP Code", Toast.LENGTH_SHORT).show();
                } else {
                    verifyCode(verify);
                }
                break;
        }
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+972" + phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            final String code = credential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(ActivityVerifyUserPhone.this, "Verification Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);
            verificationID = s;
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);
        signinByCredentials(credential);
    }

    /**
     * פונקציה המטפלת ביצירת אובייקט חדש עם הנתונים שהשמשתש הכניס בעת תהליך ההרשמה
     * ובודקת אם קוד האימות שהוזן תקין, במידה וכן נוצר משתמש חדש במערכת והמשתמש יועבר אל דף הבית.
     * במידה ולא. תתקבל הודעת שגיאה עבור קוד האימות וישלח עבור המשתמש קוד חדש
     */
    private void signinByCredentials(PhoneAuthCredential credential) {

        User user = new User(userPhoneToDB, genderToDB, activityToDB, goalsToDB, ageToDB, heightToDB, weightToDB, mealsToDB, "User E&F", 0);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            myRef.child(mAuth.getUid()).setValue(user);

                            startActivity(new Intent(ActivityVerifyUserPhone.this, ProcessDataLoading.class));
                            ActivityVerifyUserPhone.this.overridePendingTransition(0, 0);
                        } else {
                            Toast.makeText(ActivityVerifyUserPhone.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}