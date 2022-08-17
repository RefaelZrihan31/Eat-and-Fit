package com.example.ef.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ef.Progress;
import com.example.ef.ProgressListAdapter;
import com.example.ef.R;
import com.example.ef.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * פריגמנט המציג את תיעוד התקדמות המשתמש ומאפשר לו
 * לעדכן נתוני המשקל בכל יום עפי קצב ההתקדמות שלו
 * ובנוסף הוספת תמונה (לא חובה) עבור תהליך ההתקדמות
 */

public class ProgressFragment extends Fragment {

    View root;
    Activity activity;
    ArrayList<Progress> progressArrayList;
    ListView progressListItems;
    ProgressListAdapter progressListAdapter;
    ImageButton entriesBtn;
    TextView startWeight, currentWeight, changeWeight;
    User userProgress;
    String weightDB;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        root = inflater.inflate(R.layout.fragment_progress, container, false);

        initViews();

        initVarbs();

        /** מימוש כפתור לחיצה להוספת פרטי התקדמות חדשים.
         * לאחר הלחיצה יוצג למשתמש DialogFragment המאפשר לו להוסיף משקל ותמונה*/
        entriesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddProgressFragment addProgressFragment = new AddProgressFragment();
                addProgressFragment.show(getActivity().getSupportFragmentManager(), "My Fragment");
            }
        });

        myRef = database.getReference("Users").child(mAuth.getUid());

        // הפניה ושליפה של נתוני המשקל התחלתי של המשתמש והשמתו בסרגל קצב ההתקדמות
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userProgress = dataSnapshot.getValue(User.class);
                weightDB = userProgress.getWeight();
                startWeight.setText(weightDB + "KG");
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });


        /**
         * יצירת הפניה ושליפת מידע של המשתמש עבור קצב נתוני ההקדמות
         * השמורים בתוך מסד הנתונים והשמתם וחיושבם בתוך
         * סרגל ההתקדמות במסך זה*/
        myRef = database.getReference("Users").child(mAuth.getUid()).child("ProgressList");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressArrayList = new ArrayList<>();
                progressListAdapter = new ProgressListAdapter(activity, progressArrayList);
                for (DataSnapshot dSnapshot : dataSnapshot.getChildren()) {
                    String pImage = dSnapshot.child("imageTimeStamp").getValue(String.class);
                    String pDate = dSnapshot.child("progressDate").getValue(String.class);
                    String pWeight = dSnapshot.child("progressWeight").getValue(String.class);
                    Progress progress = new Progress(pWeight, pDate, pImage);
                    progressArrayList.add(progress);
                    currentWeight.setText(pWeight + "KG");
                    changeWeight.setText((Integer.parseInt(weightDB) - Integer.parseInt(pWeight)) + "KG");
                }
                progressListItems.setAdapter(progressListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        return root;
    }

    /**
     * אתחול המשתנים בדף
     */
    private void initViews() {
        progressListItems = root.findViewById(R.id.progress_list_item);
        startWeight = root.findViewById(R.id.start_weight_progress);
        currentWeight = root.findViewById(R.id.current_weight_progress);
        changeWeight = root.findViewById(R.id.change_weight_progress);
        entriesBtn = root.findViewById(R.id.entries_btn);
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

    @Override
    public void onResume() {
        super.onResume();
    }
}