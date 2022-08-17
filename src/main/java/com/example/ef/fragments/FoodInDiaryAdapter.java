package com.example.ef.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ef.FoodInDiary;
import com.example.ef.FragManager;
import com.example.ef.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * דף זה מאפשר לחבר בין מאכל ספיצפי לרשימה של מאכלים
 * אשר תורכב עבור כל ארוחה
 */

public class FoodInDiaryAdapter extends RecyclerView.Adapter<FoodInDiaryAdapter.NestedViewHolder> {

    private List<FoodInDiary> foodInDiaryList;

    public FoodInDiaryAdapter(List<FoodInDiary> foodInDiaryList) {
        this.foodInDiaryList = foodInDiaryList;
    }

    @NonNull
    @Override
    public NestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_meals, parent, false);
        return new NestedViewHolder(view);
    }

    /**
     * יצירת מאגד רשימת מאכלים
     * על המסך - כאשר יש מידע חדש הוא יציג אותו על המסך
     * וכל שאר המידע יוחזק ב- holder זה
     */
    @Override
    public void onBindViewHolder(@NonNull NestedViewHolder holder, int position) {
        holder.foodNameAdapter.setText(foodInDiaryList.get(position).getFoodName());
        holder.foodCalAdapter.setText(foodInDiaryList.get(position).getFoodCaloriePerMeal() + "cal");
        holder.foodGramAdapter.setText(foodInDiaryList.get(position).getFoodWeightPerMeal() + "g");
        holder.currentMealNum = foodInDiaryList.get(position).getMealNumber();
        holder.barcodePerCurrentFood = foodInDiaryList.get(position).getFoodBarcode();
        holder.calPerCurrentFood = foodInDiaryList.get(position).getFoodCaloriePerMeal();
    }

    @Override
    public int getItemCount() {
        return foodInDiaryList.size();
    }

    public class NestedViewHolder extends RecyclerView.ViewHolder {
        private TextView foodNameAdapter, foodCalAdapter, foodGramAdapter;
        int currentMealNum, calPerCurrentFood;
        String barcodePerCurrentFood;
        ImageView eatFoodBtnAdp;
        FirebaseAuth mAuth;
        FirebaseDatabase database;
        DatabaseReference myRef;

        public NestedViewHolder(@NonNull View itemView) {
            super(itemView);

            foodNameAdapter = itemView.findViewById(R.id.adp_food_in_diary_food_name);
            foodCalAdapter = itemView.findViewById(R.id.adp_food_in_diary_food_cal);
            foodGramAdapter = itemView.findViewById(R.id.adp_food_in_diary_food_gram);
            eatFoodBtnAdp = itemView.findViewById(R.id.adp_food_in_diary_food_btn);

            /** מימוש כפתור ״אכילה״ עבור המשתמש.
             * לאחר לחיצה על כפתור זה המאכל מהרשימה ימחק
             * וערכיו הקלוריים יעדכנו את בר הקלוריות
             *
             * המחיקה תתבצע עי שליפת נתוני הארוחות של המשתמש הקיימות במסד הנתונים
             * והתאמה באמצעות מספר הארוחה של המאכל שנבחר + ברקוד המאכל*/
            eatFoodBtnAdp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initVarbs();

                    myRef = database.getReference("Users").child(mAuth.getUid());

                    myRef.child("totalCalories").setValue(ServerValue.increment(calPerCurrentFood));

                    myRef = database.getReference("Users").child(mAuth.getUid()).child("Meals");
                    Query removeFoodQuery = myRef.child("Meal Number " + currentMealNum).orderByChild("diaryFoodBarcode").equalTo(barcodePerCurrentFood);

                    removeFoodQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot scanHistoryDeleteSnapshot : dataSnapshot.getChildren()) {
                                scanHistoryDeleteSnapshot.getRef().removeValue();
                                Toast.makeText(itemView.getContext(), "Food Deleted Successfully", Toast.LENGTH_SHORT).show();
                                ((FragManager) itemView.getContext()).navigateTo(new DiarySlideScanHistoryFragment());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            });
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
}