package com.example.ef;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.ef.fragments.DiarySlideScanHistoryFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * דף זה מאפשר לחבר בין כל נתוני המאכלים שנסרקו באמצעות המשתמש המשתמש
 */

public class ScanHistoryListAdapter extends ArrayAdapter {
    TextView foodName, foodCalorie;
    Food food;
    ArrayList<Food> foodListScanHistory;
    ImageView scanHistoryDeleteItemBtn, scanHistoryAddItemBtn;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference ref, mealsRef;
    Integer numOfMeals;
    User userScanHistory;


    public ScanHistoryListAdapter(@NonNull Context context, ArrayList<Food> foodListScanHistory) {
        super(context, R.layout.list_of_scan_history, foodListScanHistory);
        this.foodListScanHistory = foodListScanHistory;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_of_scan_history, parent, false);
        }

        foodName = convertView.findViewById(R.id.food_name_scan_history);
        foodCalorie = convertView.findViewById(R.id.food_calorie_scan_history);
        scanHistoryDeleteItemBtn = convertView.findViewById(R.id.scan_history_remove);
        scanHistoryAddItemBtn = convertView.findViewById(R.id.scan_history_add);

        food = this.foodListScanHistory.get(position);
        foodName.setText(food.name);
        foodCalorie.setText(food.calories + "cal");


        initVarbs();


        ref = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());

        //שליפת מספר הארוחת מתוך מסד הנתונים עבור המשתמש
        mealsRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userScanHistory = dataSnapshot.getValue(User.class);
                numOfMeals = userScanHistory.getMeals();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        //הפנייה עבור כל המאכלים שנסרקו ונשמרו בתוך מסד הנתונים ושליפתם
        //השליפה תתבצע באמצעות התאמה של הברקוד מתוך היסטוריית הסריקות של המשתמש אל מול
        //המאכל שנבחר מתוך רשימת המאכלים שנסרקו הנמצאים בדף הנוכחי
        // במידה ויש התאמה זהה בינהם לחיצה על כפתור המחיקה המופיע ימחק את המאכל שנבחר מתוך מסד הנתונים
        //ומן המסך הנוכחי
        Query scanHistoryQuery = ref.child("ScanHistory").orderByChild("pScanHistoryBarcode").equalTo(food.getBarcode());
        scanHistoryDeleteItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scanHistoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot scanHistoryDeleteSnapshot : dataSnapshot.getChildren()) {
                            scanHistoryDeleteSnapshot.getRef().removeValue();
                            Toast.makeText(getContext(), "Item Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        /**
         * הוספת מאכל אל תוך רשימת הארוחות
         * בעת לחיצה על כפתור זה תתבצע שליפה של המאכל הנבחר להוספה מתוך מסד הנתונים.
         * לאחר הלחיצה יפתח חלון שבאמצעותו המשתמש יוכל להזין את כמות בגרם של המאכל אותו הוא רוצה להוסיף אל הרשימה
         * לאחר הזנת כמות הגרם שהוזנה יתבצע חישוב של חלוקה עפי מספר הארוחות שיש למשתמש לעומת כמות הגרם שנבחרה
         * לסיום תתבצע הוספה של המאכל שנבחר עבור כל ארוחה אל תוך מסד הנתונים*/
        scanHistoryAddItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scanHistoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            for (DataSnapshot scanHistoryAddSnapshot : dataSnapshot.getChildren()) {
                                if (scanHistoryAddSnapshot.hasChild("pScanHistoryBarcode")) {
                                    String nameToAdd = scanHistoryAddSnapshot.child("pScanHistoryName").getValue(String.class);
                                    String barcodeToAdd = scanHistoryAddSnapshot.child("pScanHistoryBarcode").getValue(String.class);
                                    Integer caloriesToAdd = scanHistoryAddSnapshot.child("pScanHistoryCalories").getValue(Integer.class);
                                    final EditText input = new EditText(getContext());
                                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    input.setTextColor(Color.parseColor("#FFFFFF"));
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_EF_Dialog);
                                    builder.setTitle("Add to Meals");
                                    builder.setMessage("How many grams would you like to eat " + nameToAdd + " ?");
                                    builder.setView(input);
                                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (input.getText().toString().isEmpty()) {
                                                Toast.makeText(getContext(), "Enter how many grams would you like to eat!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            Integer inputValueGram = Integer.parseInt(input.getText().toString());
                                            if (!(inputValueGram >= 1 && inputValueGram <= 3000)) {
                                                Toast.makeText(getContext(), "Out of range!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            //משיכת הקלוריות של המאכל שהתווסף והפיכתו עבור גרם 1
                                            Double oneGramCaloriePerFood = caloriesToAdd / 100.0;

                                            // חישוב מספר הקלוריות כפול גרם שהוכנס מהמשתמש
                                            Double gramForFoodPerDay = oneGramCaloriePerFood * inputValueGram;

                                            Double dividerFoodCaloriesPerMeals = gramForFoodPerDay / numOfMeals;

                                            Integer dividerFoodGramsPerMeals = inputValueGram / numOfMeals;

                                            // -----------------השמות להיררככיה ומאכלים-------------------

                                            //יצירת אובייקט שיכיל את שם המאכל, ברקוד, מס' הקלוריות בכל מנה לכל ארוחה ומשקל המנה לכל ארוחה
                                            //הלולאה תתבצע כמס' הארוחות ותעדכן את המסד נתונים עם הפרמטרים הנ"ל
                                            for (int i = 0; i < numOfMeals; i++) {
                                                FoodInDiary foodInDiary = new FoodInDiary(nameToAdd, barcodeToAdd, Integer.valueOf(dividerFoodCaloriesPerMeals.intValue()), dividerFoodGramsPerMeals, i + 1);
                                                updateData(foodInDiary);
                                            }

                                            Toast.makeText(getContext(), "Food Added To Your Diary", Toast.LENGTH_SHORT).show();
                                            ((FragManager) getContext()).navigateTo(new DiarySlideScanHistoryFragment());
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    // Creating Dynamic
                                    Rect displayRectangle = new Rect();

                                    Window window = alertDialog.getWindow();
                                    window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
                                    alertDialog.getWindow().setLayout((int) (displayRectangle.width() *
                                            0.9f), (int) (displayRectangle.height() * 0.3f));
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        return convertView;
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

    /**
     * פעולה זו מעדכנת במסד הנתונים את פרטי המאכל שהמשתמש רוצה להוסיף.
     * יוצרת יוצרת אובייקט של מילון עם מפתח-ערך ומעדכנת במקום המתאים לפי עץ ההירכיות שהוגדר במסד הנתונים
     */
    private void updateData(FoodInDiary foodInDiaryObj) {
        HashMap updateFoodInDiaryList = new HashMap();
        updateFoodInDiaryList.put("diaryFoodName", foodInDiaryObj.getFoodName());
        updateFoodInDiaryList.put("diaryFoodBarcode", foodInDiaryObj.getFoodBarcode());
        updateFoodInDiaryList.put("diaryFoodCaloriePerMeal", foodInDiaryObj.getFoodCaloriePerMeal());
        updateFoodInDiaryList.put("diaryFoodWeightPerMeal", foodInDiaryObj.getFoodWeightPerMeal());
        updateFoodInDiaryList.put("diaryFoodMealNumber", foodInDiaryObj.getMealNumber());

        mealsRef.child("Meals").child("Meal Number " + foodInDiaryObj.getMealNumber()).child(foodInDiaryObj.getFoodBarcode()).updateChildren(updateFoodInDiaryList).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
            }
        });
    }
}

