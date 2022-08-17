package com.example.ef.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.ef.Food;
import com.example.ef.FragManager;
import com.example.ef.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.HashMap;

/*** פריגמט סריקת ברקודים*/

public class ScanBarcodeFragment extends Fragment {
    View root;
    Activity activity;
    private CodeScanner mCodeScanner;
    CodeScannerView scannerView;
    String name, tempBarcode;
    Integer calories, carbs, fats, protein;
    DatabaseReference myRef, myRefFoodAPI;
    FirebaseAuth mAuth;
    FirebaseDatabase database;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        activity = getActivity();
        root = inflater.inflate(R.layout.fragment_scan_barcode, container, false);
        initViews();

        initVarbs();

        myRef = database.getReference("Users").child(mAuth.getUid());

        /**לאחר סריקת ברקוד תתבצע בדיקה האם קיים ברקוד זהה ב-FoodAPI הנמצא בתוך מסד הנתונים
         * במידה וכן - יפתח חלון המציג את פרטי המאכל שנסרק והמשתמש יצטרך לבחור
         * האם להוסיף את המאכל שנסרק או לסרוק מאכל חדש.
         * במידה ולא נמצא ברקוד זהה מתוך מסד הנתונים - התמונה תעצר ולאחר לחיצה חוזרת על המסך
         * או על כפתור הסריקת תתאפשר סריקה מחודשת של ברקוד אחר
         *
         * במידה והברקוד שנסרק כבר נסרק לפני כן, ההוספה תתבצע אך לא יוצג שום שינוי ברשימת
         * המאכלים שנסרקו*/
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Query queryByCode = myRefFoodAPI.child("FoodAPI");
                        queryByCode.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (snapshot.hasChild("barcode")) {
                                        tempBarcode = snapshot.child("barcode").getValue(String.class);
                                        if (tempBarcode.equals(result.getText())) {

                                            name = snapshot.child("name").getValue(String.class);
                                            calories = snapshot.child("calories").getValue(Integer.class);
                                            fats = snapshot.child("fats").getValue(Integer.class);
                                            protein = snapshot.child("protein").getValue(Integer.class);
                                            carbs = snapshot.child("carbs").getValue(Integer.class);

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_EF_Dialog);
                                            builder.setTitle(name);
                                            builder.setMessage("Per 100g\n\n" +
                                                    "Calories: " + calories
                                                    + "\nCarbs: " + carbs
                                                    + "\nFats: " + fats
                                                    + "\nProtein: " + protein);
                                            builder.setPositiveButton("Add To My List", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Food food = new Food(name, snapshot.child("barcode").getValue(String.class), calories, fats, protein, carbs);
                                                    updateData(food);
                                                }
                                            });
                                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    ((FragManager) getActivity()).navigateTo(new ScanBarcodeFragment());
                                                }
                                            });
                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.show();
                                            // Creating Dynamic
                                            Rect displayRectangle = new Rect();

                                            Window window = getActivity().getWindow();
                                            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
                                            alertDialog.getWindow().setLayout((int) (displayRectangle.width() *
                                                    0.9f), (int) (displayRectangle.height() * 0.33f));
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                throw databaseError.toException(); // never ignore errors.
                            }
                        });
                    }

                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        return root;
    }

    /**
     * אתחול משתנים
     */
    private void initViews() {
        scannerView = root.findViewById(R.id.scannerView);
    }

    /**
     * אתחול האובייקטים
     */
    private void initVarbs() {
        mCodeScanner = new CodeScanner(activity, scannerView);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase DataBase
        database = FirebaseDatabase.getInstance();

        myRefFoodAPI = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * פונקציה המעדכנת את נתוני המאכלים שנסרקו באמצעות המשתמש והעברתם אל תוך מסד הנתונים
     */
    private void updateData(Food foodObj) {
        HashMap updateScanHistoryList = new HashMap();
        updateScanHistoryList.put("pScanHistoryName", foodObj.getName());
        updateScanHistoryList.put("pScanHistoryBarcode", foodObj.getBarcode());
        updateScanHistoryList.put("pScanHistoryCalories", foodObj.getCalories());
        updateScanHistoryList.put("pScanHistoryFats", foodObj.getFats());
        updateScanHistoryList.put("pScanHistoryProtein", foodObj.getProtein());
        updateScanHistoryList.put("pScanHistoryCarbs", foodObj.getCrabs());

        myRef.child("ScanHistory").child(foodObj.getBarcode()).updateChildren(updateScanHistoryList).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), name + " Added Successfully To Your List", Toast.LENGTH_LONG).show();
                    ((FragManager) getActivity()).navigateTo(new ScanBarcodeFragment());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}