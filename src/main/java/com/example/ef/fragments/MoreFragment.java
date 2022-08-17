package com.example.ef.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ef.FragManager;
import com.example.ef.MorePhotoTakerDialogFragment;
import com.example.ef.R;
import com.example.ef.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * פרגמנט המשמש עבור עדכון פרטי המשתמש -
 * גיל, משקל, גובה, מין, מטרה, רמת פעילות
 * החלפת תמונת פרופיל ושם משתמש
 */
public class MoreFragment extends Fragment implements View.OnClickListener {
    View root;
    EditText moreAge, moreWeight, moreHeight, userName;
    TextView spinner;
    Button btnSaveChanges;
    CircleImageView userProfilePic;
    ArrayAdapter<String> adapterGender, adapterGoal, adapterActivityLevel;
    List<String> itemsGender, itemsGoal, itemsActivityLevel;
    Spinner dropdownGender, dropdownGoal, dropdownActivityLevel;
    Bundle args;
    User userMore;
    String activityLevelDB, ageDB, genderDB, goalDB, heightDB, weightDB, userNameDB;
    int spinnerPositionGender, spinnerPositionGoal, spinnerPositionActivityLevel;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference storageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_more, container, false);

        initViews();

        initVarbs();

        initButtons();

        myRef = database.getReference("Users").child(mAuth.getUid());

        // שליפת נתוני פרטי המשתמש אותם יוכל לעדכן
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userMore = dataSnapshot.getValue(User.class);
                activityLevelDB = userMore.getActivityLevel();
                ageDB = userMore.getAge();
                genderDB = userMore.getGender();
                goalDB = userMore.getGoal();
                heightDB = userMore.getHeight();
                weightDB = userMore.getWeight();
                userNameDB = userMore.getUserName();

                // השמת הנתונים שנשלפו בדף
                moreAge.setText(ageDB);

                moreWeight.setText(weightDB);

                moreHeight.setText(heightDB);

                userName.setText(userNameDB);

                spinnerPositionGender = adapterGender.getPosition(genderDB);

                spinnerPositionGoal = adapterGoal.getPosition(goalDB);

                spinnerPositionActivityLevel = adapterActivityLevel.getPosition(activityLevelDB);

                dropdownGender.setSelection(spinnerPositionGender);

                dropdownGoal.setSelection(spinnerPositionGoal);

                dropdownActivityLevel.setSelection(spinnerPositionActivityLevel);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        // יצירת הפניה עבור המיקום במסד הנתונים לשליפת או עדכון תמונת הפרופיל
        storageRef = FirebaseStorage.getInstance().getReference("UserImages/" + FirebaseAuth.getInstance().getUid() + "/" + "profilePic.jpg");

        //נסיון לשליפת נתוני התמונה מתוך מאגר המידע
        //במידה ויצליח לשלוף בהצלחה תתקבל תמונת הפרופיל
        //במידה ולא, תוצג תמונת הפרופיל ברירת המחדל
        try {
            File localFile = File.createTempFile("tempFile", ".jpg");
            storageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            userProfilePic.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return root;
    }

    /*** אתחול האובייקטים בדף*/
    private void initVarbs() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase DataBase
        database = FirebaseDatabase.getInstance();

        args = new Bundle();

        //create a list of items for the spinner.
        itemsGender = Arrays.asList("Male", "Female");
        itemsGoal = Arrays.asList("Lose Weight", "Maintain Muscle Mass", "Gain Muscle");
        itemsActivityLevel = Arrays.asList("Not Very Active", "Lightly Active", "Active", "Very Active");


        //create an adapter to describe how the items are displayed, com.example.ef.adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        adapterGender = new ArrayAdapter<>(getActivity(), R.layout.selected_spinner_item, itemsGender);

        //set the spinners adapter to the previously created one.
        adapterGender.setDropDownViewResource(R.layout.spinner_dropdown_items);
        dropdownGender.setAdapter(adapterGender);


        //create an adapter to describe how the items are displayed, com.example.ef.adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        adapterGoal = new ArrayAdapter<>(getActivity(), R.layout.selected_spinner_item, itemsGoal);

        //set the spinners adapter to the previously created one.
        adapterGoal.setDropDownViewResource(R.layout.spinner_dropdown_items);
        dropdownGoal.setAdapter(adapterGoal);

        //create an adapter to describe how the items are displayed, com.example.ef.adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        adapterActivityLevel = new ArrayAdapter<>(getActivity(), R.layout.selected_spinner_item, itemsActivityLevel);

        //set the spinners adapter to the previously created one.
        adapterActivityLevel.setDropDownViewResource(R.layout.spinner_dropdown_items);
        dropdownActivityLevel.setAdapter(adapterActivityLevel);

    }

    /*** אתחול המשתנים בדף*/
    private void initViews() {
        //get the spinner from the xml.
        dropdownGender = root.findViewById(R.id.more_spinner);
        dropdownGoal = root.findViewById(R.id.more_spinner_goal);
        dropdownActivityLevel = root.findViewById(R.id.more_spinner_activity_level);
        moreAge = root.findViewById(R.id.more_age);
        moreWeight = root.findViewById(R.id.more_weight);
        moreHeight = root.findViewById(R.id.more_height);
        spinner = root.findViewById(R.id.more_spinner_gender);
        btnSaveChanges = root.findViewById(R.id.btn_more_save);
        userName = root.findViewById(R.id.user_name);
        userProfilePic = root.findViewById(R.id.user_profile_pic);
    }

    /*** אתחול הכפתורים בדף*/
    private void initButtons() {
        btnSaveChanges.setOnClickListener(this);
        userProfilePic.setOnClickListener(this);
    }

    //התעלמות וסינון הערות מהמערכת
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /** בעת לחיצה כפתור זה, תתבצע שליפת כל הנתונים מהשדות הקיימים בדף
             * ולאחר מכן תתצבע בדיקת תקינות ערכי השדות.
             * במידה ונתוני השדות תקינים - יתבצע עדכון פרטים במסד הנתונים ופרטיו המעדוכנים
             * יוצגו בדף לאחר מכן.*/
            case R.id.btn_more_save:
                String ageUpdate = moreAge.getText().toString();
                String weightUpdate = moreWeight.getText().toString();
                String heightUpdate = moreHeight.getText().toString();
                String genderUpdate = dropdownGender.getSelectedItem().toString();
                String activityLevelUpdate = dropdownActivityLevel.getSelectedItem().toString();
                String goalUpdate = dropdownGoal.getSelectedItem().toString();
                String userNameUpdate = userName.getText().toString();

                if (checkUpdateEditTextValues(userNameUpdate, ageUpdate, weightUpdate, heightUpdate)) {
                    updateData(ageUpdate, weightUpdate, heightUpdate, genderUpdate, activityLevelUpdate, goalUpdate, userNameUpdate);
                }

                ((FragManager) getActivity()).navigateTo(new MoreFragment());

                break;
            /**בעת לחיצה על כפתור תמונה זה, יתבצע מעבר ל- DialogFragment המציג אפשרות
             * בחירת תמונת פרופיל*/
            case R.id.user_profile_pic:
                MorePhotoTakerDialogFragment morePhotoTakerDialogFragment = new MorePhotoTakerDialogFragment();
                morePhotoTakerDialogFragment.show(getActivity().getSupportFragmentManager(), "Image Fragment");
                break;
        }
    }

    /**
     * פונקציה לבדיקת תקינות קלט עבור נתוני פרטי המשתמש המוצגים בדף
     */
    private boolean checkUpdateEditTextValues(String userNameUpdate, String ageUpdate, String weightUpdate, String heightUpdate) {

        if (!userNameUpdate.isEmpty() && !ageUpdate.isEmpty() && !weightUpdate.isEmpty() && !heightUpdate.isEmpty()) {

            int tempAge = Integer.parseInt(ageUpdate);
            int tempWeight = Integer.parseInt(weightUpdate);
            int tempHeight = Integer.parseInt(heightUpdate);

            if (!(tempAge >= 18 && tempAge <= 120)) {
                Toast.makeText(getContext(), "Out of range... Age between 18-120", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!(tempWeight >= 50 && tempWeight <= 200)) {
                Toast.makeText(getContext(), "Out of range... Weight between 50-200", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!(tempHeight >= 140 && tempHeight <= 240)) {
                Toast.makeText(getContext(), "Out of range... Height between 140-240", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(getContext(), "One or more fields are empty...", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * פונקציה המעדכנת את נתוני מידע המשתמש שעודכנו בדף אל תוך מסד הנתונים
     */
    private void updateData(String ageUpdate, String weightUpdate, String heightUpdate, String genderUpdate, String activityLevelUpdate, String goalUpdate, String userNameUpdate) {
        HashMap updateUser = new HashMap();
        updateUser.put("age", ageUpdate);
        updateUser.put("weight", weightUpdate);
        updateUser.put("height", heightUpdate);
        updateUser.put("gender", genderUpdate);
        updateUser.put("activityLevel", activityLevelUpdate);
        updateUser.put("goal", goalUpdate);
        updateUser.put("userName", userNameUpdate);

        myRef.updateChildren(updateUser).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


