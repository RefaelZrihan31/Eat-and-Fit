package com.example.ef.fragments;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ef.Article;
import com.example.ef.ArticleListAdapter;
import com.example.ef.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * פריגמנט דף הבית - בדף זה יוצגו מספר כרטיסיות המכילות כתבות שונות
 * העוסקות בכושר ובתזונה נכונה, ובנסוף יוצג בר הקלריות
 */
public class HomeFragment extends Fragment {

    ArrayList<Article> articleArrayList;
    ListView articleListItem;
    ArticleListAdapter articleListAdapter;
    View root;
    private static final int TAKE_PHOTO = 6;
    private static final int GALLERY_PHOTO = 7;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        root = inflater.inflate(R.layout.fragment_home, container, false);

        //מערך תמונות כתבות
        int[] imageId = {R.drawable.articlepic1,
                R.drawable.articlepic2,
                R.drawable.articlepic3,
                R.drawable.articlepic4,
                R.drawable.articlepic5};


        initViews();

        initVarbs();

        //שליפת מידע עבור כל הכתבות הנמצאות במסד הנתונים
        myRef = database.getReference("ArticleAPI");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                articleArrayList = new ArrayList<>();
                int counterImage = 0 ;
                for (DataSnapshot dSnapshot : dataSnapshot.getChildren()) {
                    String articleDescription = dSnapshot.child("articleDescription").getValue(String.class);
                    String articleLink = dSnapshot.child("articleLink").getValue(String.class);
                    String articleTitle = dSnapshot.child("articleTitle").getValue(String.class);

                    Article article = new Article(articleTitle,articleDescription,articleLink,imageId[counterImage++]);
                    articleArrayList.add(article);
                }
                articleListAdapter = new ArticleListAdapter(activity, articleArrayList);
                articleListItem.setAdapter(articleListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });




        /**קבלת אישורי גישה מהמשתמש -
         *   עבור גישה למצלמת המכשיר
         * עבור גישה לקבצים המכשיר*/
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, TAKE_PHOTO);
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PHOTO);
        return root;
    }

    /*** אתחול המשתנים בדף*/
    private void initViews() {
        articleListItem = root.findViewById(R.id.article_list_item);
    }

    /*** אתחול האובייקטים בדף*/
    private void initVarbs() {

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase DataBase
        database = FirebaseDatabase.getInstance();
    }

}
