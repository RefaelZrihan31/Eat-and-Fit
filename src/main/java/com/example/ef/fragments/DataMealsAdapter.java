package com.example.ef.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ef.DataMeals;
import com.example.ef.FoodInDiary;
import com.example.ef.R;

import java.util.ArrayList;
import java.util.List;

/**
 * דף זה מאפשר לחבר בין רשימת הארוחות לבין רשימת המאכלים
 * עבור כל ארוחה
 */

public class DataMealsAdapter extends RecyclerView.Adapter<DataMealsAdapter.ItemViewHolder> {

    private List<DataMeals> dataMealsList;
    private List<FoodInDiary> foodInDiaryArrayList = new ArrayList<>();

    public DataMealsAdapter(List<DataMeals> dataMealsList) {
        this.dataMealsList = dataMealsList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_food, parent, false);
        return new ItemViewHolder(view);
    }

    /**
     * יצירת מאגד כרטסיות ואתחול כל מידע המאכלים והארוחות
     * על המסך - כאשר יש מידע חדש הוא יציג אותו על המסך
     * וכל שאר המידע יוחזק ב- holder זה
     */
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        DataMeals dataMealsModel = dataMealsList.get(position);
        holder.titleOfMealNumber.setText(dataMealsModel.getFoodName());

        //טיפול באייקון סגירה / פתיחה עבור לשונית הארוחה
        boolean isExpandable = dataMealsModel.isExpandable();
        holder.expandableFoodLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
        if (isExpandable) {
            holder.arrowImage.setImageResource(R.drawable.ic_arrow_upward);
        } else {
            holder.arrowImage.setImageResource(R.drawable.ic_arrow_downward);
        }

        FoodInDiaryAdapter adapter = new FoodInDiaryAdapter(foodInDiaryArrayList);
        holder.recyclerViewListOfFoodLayout.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerViewListOfFoodLayout.setHasFixedSize(true);
        holder.recyclerViewListOfFoodLayout.setAdapter(adapter);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            //מימוש פתיחת כרטיסיית ״ארוחה״ בעמוד
            public void onClick(View v) {
                dataMealsModel.setExpandable(!dataMealsModel.isExpandable());
                foodInDiaryArrayList = dataMealsModel.getFoodInDiaryList();
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataMealsList.size();
    }

    /**
     * אתחול כל משתני המאכלים המוצגים על המסך
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private RelativeLayout expandableFoodLayout;
        private TextView titleOfMealNumber;
        private ImageView arrowImage;
        private RecyclerView recyclerViewListOfFoodLayout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandableFoodLayout = itemView.findViewById(R.id.expandable_food_layout);
            titleOfMealNumber = itemView.findViewById(R.id.title_of_meal_number);
            arrowImage = itemView.findViewById(R.id.arrow_imageview);
            recyclerViewListOfFoodLayout = itemView.findViewById(R.id.recyclerView_list_of_food_layout);
        }
    }
}
