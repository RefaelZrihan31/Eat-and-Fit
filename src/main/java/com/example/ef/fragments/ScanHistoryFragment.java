package com.example.ef.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.ef.Food;
import com.example.ef.R;
import com.example.ef.ScanHistoryListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * פריגמנט המציג את נתוני המאכלים שנסרקו עי המשתמש
 * והוספת המאכל שיבחר מתוך הרשימה אל דף רשימת הארוחות היומיות
 * ובנוסף ניתן למחוק מותר שנסרק גם כן
 */

public class ScanHistoryFragment extends Fragment {
    View root;
    ArrayList<Food> foodArrayList;
    ListView scanHistoryListItems;
    ScanHistoryListAdapter scanHistoryListAdapter;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Activity activity = getActivity();
        root = inflater.inflate(R.layout.fragment_scan_history, container, false);

        initViews();

        initVarbs();

        /**הפניה ושליפת נתוני המאכלים שנסרקו של המשתמש מתוך מסד הנתונים
         * והשמתם בתוך רשימת המאכלים*/
        myRef = database.getReference("Users").child(mAuth.getUid()).child("ScanHistory");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                foodArrayList = new ArrayList<>();
                scanHistoryListAdapter = new ScanHistoryListAdapter(activity, foodArrayList);
                for (DataSnapshot dSnapshot : dataSnapshot.getChildren()) {
                    String pScanHistoryBarcode = dSnapshot.child("pScanHistoryBarcode").getValue(String.class);
                    String pScanHistoryName = dSnapshot.child("pScanHistoryName").getValue(String.class);
                    Integer pScanHistoryCalories = dSnapshot.child("pScanHistoryCalories").getValue(Integer.class);
                    Integer pScanHistoryFats = dSnapshot.child("pScanHistoryFats").getValue(Integer.class);
                    Integer pScanHistoryProtein = dSnapshot.child("pScanHistoryProtein").getValue(Integer.class);
                    Integer pScanHistoryCarbs = dSnapshot.child("pScanHistoryCarbs").getValue(Integer.class);
                    Food food = new Food(pScanHistoryName, pScanHistoryBarcode, pScanHistoryCalories, pScanHistoryFats, pScanHistoryProtein, pScanHistoryCarbs);
                    foodArrayList.add(food);
                }
                scanHistoryListItems.setAdapter(scanHistoryListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        return root;
    }

    private void initViews() {
        scanHistoryListItems = root.findViewById(R.id.list_food_scan_history);
    }

    private void initVarbs() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase DataBase
        database = FirebaseDatabase.getInstance();
    }
}