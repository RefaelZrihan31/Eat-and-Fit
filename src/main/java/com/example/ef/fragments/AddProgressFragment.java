package com.example.ef.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.ef.Progress;
import com.example.ef.ProgressPhotoTakerDialogFragment;
import com.example.ef.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * חלון המאפשר להכניס פרטים של קצב התקדמות המשתמש עם אפשרות להוספת תמונה מהמצלמה או גלריה
 */

public class AddProgressFragment extends DialogFragment implements View.OnClickListener {
    View root;
    ImageButton addProgressImage, addProgressBtn;
    EditText addProgressWeight;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Activity activity = getActivity();
        root = inflater.inflate(R.layout.fragment_add_progress, container, false);

        initViews();

        initVarbs();

        initButtons();

        myRef = database.getReference("Users").child(mAuth.getUid());

        return root;
    }

    /**
     * אתחול הכפתורים בדף
     */
    private void initButtons() {
        addProgressBtn.setOnClickListener(this);
        addProgressImage.setOnClickListener(this);
    }

    /**
     * אתחול המשתנים בדף
     */
    private void initViews() {
        addProgressWeight = root.findViewById(R.id.add_progress_weight);
        addProgressImage = root.findViewById(R.id.add_progress_image);
        addProgressBtn = root.findViewById(R.id.add_progress_btn);
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
     * add_progress_btn: בעת לחיצה תתווסף רשומה של נתוני המשקל + תמונה(לא חובה) של המשתמש
     * progressListDate: יצירת אובייקט של תאריך לפי שעון המכשיר היוצר מזהה חד ערכי של תאריך בכל יום
     * add_progress_image: בעת לחיצה יפתח חלון שהמשתמש יתבקש לבחור מאיפה לקחת את התמונה מהמצלמה או הגלריה
     */
    @SuppressLint("NonConstantResourceId")//התעלמות וסינון הערות מהמערכת
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_progress_btn:
                String weightUpdate = addProgressWeight.getText().toString();
                if (weightUpdate.isEmpty()) {
                    Toast.makeText(getContext(), "One or more fields are empty...", Toast.LENGTH_SHORT).show();
                    return;
                }

                //בדיקת תקינות קלט עבור המשקל
                int tempWeight = Integer.parseInt(weightUpdate);

                if (!(tempWeight >= 50 && tempWeight <= 200)) {
                    Toast.makeText(getContext(), "Out of range... Weight between 50-200", Toast.LENGTH_SHORT).show();
                    return;
                }

                @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH);

                String progressListDate = s.format(new Date());

                //מחרוזת שמכילה את שם התמונה הספציפית
                String photoTimeStamp = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("progressImageTime", "");
                Progress progress = new Progress(weightUpdate, progressListDate, photoTimeStamp);
                updateData(progress);

                break;
            case R.id.add_progress_image:
                ProgressPhotoTakerDialogFragment progressPhotoTakerDialogFragment = new ProgressPhotoTakerDialogFragment();
                progressPhotoTakerDialogFragment.show(getActivity().getSupportFragmentManager(), "Image Fragment");
                break;
        }
    }


    /**
     * פעולה זו מעדכנת במסד הנתונים את פרטי ההתקדמות שהמתשמש הכניס.
     * יוצרת יוצרת אובייקט של מילון עם מפתח-ערך ומעדכנת במקום המתאים לפי עץ ההירכיות שהוגדר במסד הנתונים
     */
    private void updateData(Progress progressObj) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
        String progressListDataPerDay = s.format(new Date());

        //יצירת אובייקט נוסף של תאריך עם אותה הצורה של ההצגה על מנת לבדוק אם המשתמש בחר תמונה או לא בתיעוד החדש
        @SuppressLint("SimpleDateFormat") SimpleDateFormat imageTimeStampToCheck = new SimpleDateFormat("ddMMyyyy");
        String photoTimeStampToCheck = imageTimeStampToCheck.format(new Date());
        if (!photoTimeStampToCheck.equals(progressObj.getImageId())) {
            progressObj.setImageId("00000000");
        }


        HashMap updateProgList = new HashMap();
        updateProgList.put("imageTimeStamp", progressObj.getImageId());
        updateProgList.put("progressDate", progressObj.getDate());
        updateProgList.put("progressWeight", progressObj.getKilogram());

        myRef.child("ProgressList").child(progressListDataPerDay).updateChildren(updateProgList).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * הצגת חלון על גבי המסך הנוכחי כ-95% מרוחב המסך
     */
    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();

        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        // Set the width of the dialog proportional to 95% of the screen width
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Call super onResume after sizing
        super.onResume();
    }
}