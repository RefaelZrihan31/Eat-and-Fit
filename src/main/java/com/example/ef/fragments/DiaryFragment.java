package com.example.ef.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ef.DataMeals;
import com.example.ef.FoodInDiary;
import com.example.ef.FragManager;
import com.example.ef.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/***
 *  פריגמנט המציג את הרשימת הארוחות היומית של המשתמש,
 *  בנוסף יוצג גם בר קלוריות ובו מספר הקלוריות היומי, כמה קלריות המשתמש צרך
 *  וכמה קלוריות נשארו עבורו
 */


public class DiaryFragment extends Fragment {

    View root;
    private RecyclerView recyclerViewListMealsAndFood;
    private List<DataMeals> DataMealsList;
    private DataMealsAdapter dataMealsAdapter;
    Integer diaryFoodMealNumber;
    ImageView restartMealsBtn;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        root = inflater.inflate(R.layout.fragment_diary, container, false);

        initViews();

        initVarbs();

        recyclerViewListMealsAndFood.setHasFixedSize(true);
        recyclerViewListMealsAndFood.setLayoutManager(new LinearLayoutManager(getContext()));


        myRef = database.getReference("Users").child(mAuth.getUid()).child("Meals");

        // שליפת נתוני הארוחות האוכל של המשתמש ממסד הנתונים
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        List<FoodInDiary> foodInDiaryArrayList = new ArrayList<>();
                        for (DataSnapshot dSnapshot : d.getChildren()) {
                            if (dSnapshot.hasChild("diaryFoodBarcode")) {
                                String diaryFoodBarcode = dSnapshot.child("diaryFoodBarcode").getValue(String.class);
                                String diaryFoodName = dSnapshot.child("diaryFoodName").getValue(String.class);
                                Integer diaryFoodCaloriePerMeal = dSnapshot.child("diaryFoodCaloriePerMeal").getValue(Integer.class);
                                Integer diaryFoodWeightPerMeal = dSnapshot.child("diaryFoodWeightPerMeal").getValue(Integer.class);
                                diaryFoodMealNumber = dSnapshot.child("diaryFoodMealNumber").getValue(Integer.class);
                                //יצירת אובייקט של FoodInDiary והוספתו לרשימה
                                FoodInDiary foodInDiary = new FoodInDiary(diaryFoodName, diaryFoodBarcode, diaryFoodCaloriePerMeal, diaryFoodWeightPerMeal, diaryFoodMealNumber);
                                foodInDiaryArrayList.add(foodInDiary);
                            }
                        }
                        //הוספת כל רשימת ה-FoodInDiary שנוצרה והוספתה לארוחה המתאימה
                        DataMealsList.add(new DataMeals(foodInDiaryArrayList, "Meal #" + diaryFoodMealNumber));
                    }
                    dataMealsAdapter = new DataMealsAdapter(DataMealsList);
                    recyclerViewListMealsAndFood.setAdapter(dataMealsAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        /** מימוש כפתור ריסטרט עבור הארוחות - לאחר לחיצה על הפתור, סטטוס מצב הארוחות מתאפס
         * וערכי בר הקלוריות גם הם מתאפסים */
        restartMealsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef = database.getReference("Users").child(mAuth.getUid());
                myRef.child("totalCalories").setValue(0);
                myRef.child("Meals").removeValue();
                ((FragManager) getContext()).navigateTo(new DiarySlideScanHistoryFragment());
            }
        });

        return root;
    }

    /**
     * אתחול האובייקטים
     */
    private void initVarbs() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        DataMealsList = new ArrayList<>();
    }

    /**
     * אתחול המשתנים בדף
     */
    private void initViews() {
        recyclerViewListMealsAndFood = root.findViewById(R.id.recyclerView_list_meals_and_food);
        restartMealsBtn = root.findViewById(R.id.btn_clear_data);
    }
}

