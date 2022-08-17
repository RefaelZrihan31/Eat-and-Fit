package com.example.ef.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.ef.R;
import com.example.ef.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * פריגמנט המציג את בר הקלוריות של המשתמש
 * ---  יוצג כחלק מהדף Home ומדף Diary -- *
 */
public class CalBarFragment extends Fragment {
    View root;
    TextView calBarGoal, calBarFood, calBarRemaining;
    User userCalcRMR;
    String activityLevel, age, gender, goal, height, weight;
    Double calcCalories, calcTotalDailyCalories;
    int caloriesPerDay, totalFoodEatCal, tempTotalFoodEatCal;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Activity activity = getActivity();

        root = inflater.inflate(R.layout.fragment_cal_bar, container, false);

        initViews();

        initVarbs();

        myRef = database.getReference("Users").child(mAuth.getUid());

        //שליפת הנתונים הגופניים של המשתמש ממסד הנתונים
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userCalcRMR = dataSnapshot.getValue(User.class);
                activityLevel = userCalcRMR.getActivityLevel();
                age = userCalcRMR.getAge();
                gender = userCalcRMR.getGender();
                goal = userCalcRMR.getGoal();
                height = userCalcRMR.getHeight();
                weight = userCalcRMR.getWeight();
                Double rmrVal = calculateRMR(gender, weight, height, age);
                calcCalories = calculateCalories(activityLevel, rmrVal);
                calcTotalDailyCalories = calculateTotalDailyCalories(goal, calcCalories);
                caloriesPerDay = Integer.valueOf(calcTotalDailyCalories.intValue());

                // השמה ל-Goal
                calBarGoal.setText(String.valueOf(caloriesPerDay));

                //צבירה עבור השמה ה-Food - כל פעם מה שהמשתמש מחק והתקבל מהמסד נתונים + השמה ל-Remaining
                totalFoodEatCal = userCalcRMR.getTotalCalories();

                // השמה ל-Food
                calBarFood.setText(String.valueOf(totalFoodEatCal));

                //חישוב ההפרש בין מס' הקלוריות היומי למס' הקלוריות שנצברו עד כה
                tempTotalFoodEatCal = caloriesPerDay - totalFoodEatCal;
                if (tempTotalFoodEatCal <= 10 && tempTotalFoodEatCal >= -50) {
                    calBarRemaining.setTextColor(Color.parseColor("#FF8EC42C"));
                } else if (tempTotalFoodEatCal < -50) {
                    calBarRemaining.setTextColor(Color.parseColor("#FFFD3F3F"));
                }
                calBarRemaining.setText(String.valueOf(tempTotalFoodEatCal));
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        return root;
    }

    /**
     * --חישוב RMR --
     * לינק המסביר כיצד מחשבים את ה- RMR :
     * https://globalrph.com/medcalcs/resting-metabolic-rate-rmr/
     */
    private double calculateRMR(String gender, String weight, String height, String age) {
        double rmrSum;
        double v = 9.99 * Integer.parseInt(weight) + 6.25 * Integer.parseInt(height);
        if (gender.equals("Male")) {
            rmrSum = v - 4.92 * Integer.parseInt(age) + 5;
        } else {
            rmrSum = (v) - 4.92 * Integer.parseInt(age) - 161;
        }
        return rmrSum;
    }

    /**
     * חישוב צריכת קלוריות לפי התוצאה שהוחזרה מן הפונקציה המחשבת את ערכי ה-RMR ולפי רמת הפעילות של המשתמש
     */
    private double calculateCalories(String activityLevel, Double rmr) {
        double total = 0;
        switch (activityLevel) {
            case "Not Very Active":
                total = rmr * 1.2;
                break;
            case "Lightly Active":
                total = rmr * 1.3;
                break;
            case "Active":
                total = rmr * 1.5;
                break;
            case "Very Active":
                total = rmr * 1.7;
                break;
        }
        return total;

    }

    /**
     * חישוב צריכת קלוריות יומי של המשתמש
     */
    private double calculateTotalDailyCalories(String goal, Double calcCalories) {
        double total = 0;
        switch (goal) {
            case "Lose Weight":
                total = calcCalories - 500;
                break;
            case "Maintain Muscle Mass":
                total = calcCalories;
                break;
            case "Gain Muscle":
                total = calcCalories + 500;
                break;
        }
        return total;
    }

    /**
     * אתחול המשתנים בדף
     */
    private void initViews() {
        calBarGoal = root.findViewById(R.id.cal_bar_goal);
        calBarFood = root.findViewById(R.id.cal_bar_food);
        calBarRemaining = root.findViewById(R.id.cal_bar_remaining);
    }

    /**
     * אתחול האובייקטים של מסד הנתונים
     */
    private void initVarbs() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase DataBase
        database = FirebaseDatabase.getInstance();
    }
}