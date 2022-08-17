package com.example.ef;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

/*** דף המאפשר בניית רשימה עבור כל כרטייסת הכתבות*/

public class ArticleListAdapter extends ArrayAdapter {
    TextView articleTitle, articleDescription;
    ImageView articleImage;
    MaterialCardView articleCardView;
    ArrayList<Article> articleClassArrayList;


    public ArticleListAdapter(@NonNull Context context, ArrayList<Article> articleClassArrayList) {
        super(context, R.layout.list_item_article, articleClassArrayList);
        this.articleClassArrayList = articleClassArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_article, parent, false);
        }
        articleCardView = convertView.findViewById(R.id.article_link);
        articleImage = convertView.findViewById(R.id.article_image);
        articleTitle = convertView.findViewById(R.id.article_title);
        articleDescription = convertView.findViewById(R.id.article_description);

        Article articleClass = this.articleClassArrayList.get(position);


//        // יצירת הפניה עבור המיקום במסד הנתונים לשליפת או עדכון תמונת הפרופיל
//         storageRef = FirebaseStorage.getInstance().getReference("ArticleImages/"+ articleClass.imgId + ".jpg");
//
//        //נסיון לשליפת נתוני התמונה מתוך מאגר המידע
//        //במידה ויצליח לשלוף בהצלחה תתקבל תמונת הפרופיל
//        //במידה ולא, תוצג תמונת הפרופיל ברירת המחדל
//        try {
//            File localFile = File.createTempFile("tempFile", ".jpg");
//            storageRef.getFile(localFile)
//                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                             bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                        }
//                    });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        articleImage.setImageResource(articleClass.imgId);
        articleTitle.setText(articleClass.title);
        articleDescription.setText(articleClass.description);

        // פתיחת דף אינטרנט
        articleCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openLinksIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(articleClass.link));
                getContext().startActivity(openLinksIntent);
            }
        });

        return convertView;
    }
}
