package com.example.ef;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.ef.fragments.ProgressFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * דף זה מאפשר לחבר בין כל נתוני קצב נתוני התקדמות  הקיימים של המשתמש
 */

public class ProgressListAdapter extends ArrayAdapter {

    ArrayList<Progress> progressArrayList;
    TextView progressWeight, progressDate;
    ImageButton progressImgBtn, progressDeleteItemBtn;
    Progress progress;
    FirebaseAuth mAuth;

    public ProgressListAdapter(@NonNull Context context, ArrayList<Progress> progressArrayList) {
        super(context, R.layout.list_progress_items, progressArrayList);
        this.progressArrayList = progressArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_progress_items, parent, false);
        }

        progressWeight = convertView.findViewById(R.id.progress_weight);
        progressDate = convertView.findViewById(R.id.progress_date);
        progressImgBtn = convertView.findViewById(R.id.progress_img_btn);
        progressDeleteItemBtn = convertView.findViewById(R.id.progress_delete_item);
        progress = this.progressArrayList.get(position);
        progressWeight.setText(progress.kilogram + "KG");
        progressDate.setText(progress.date);
        progressDeleteItemBtn.setTag(position);

        /** שליפת נתוני התמונות של המשתמש מתוך האחסון של מסד הנתונים - עבור כל תמונה ברשימה */
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("UserImages/" + FirebaseAuth.getInstance().getUid() + "/" + progress.imageId + ".jpg");
        progressImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    File localFile = File.createTempFile("tempFile", ".jpg");
                    storageRef.getFile(localFile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    ImageView image = new ImageView(getContext());
                                    image.setImageBitmap(bitmap);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_EF_Dialog);
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).setView(image);
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    // Creating Dynamic
                                    Rect displayRectangle = new Rect();

                                    Window window = alertDialog.getWindow();
                                    window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
                                    alertDialog.getWindow().setLayout((int) (displayRectangle.width() *
                                            0.9f), (int) (displayRectangle.height() * 0.5f));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "No Image...", Toast.LENGTH_SHORT).show();
                                }
                            });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        /**שליפה ועדכון של נתוני תמונה ופרטי קצב התקדמות שנבחרו מתוך מסד הנתונים והסרתם מתוך מסד הנתונים*/
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
        Query progressDeleteQuery = ref.child("ProgressList").orderByChild("progressDate").equalTo(progressDate.getText().toString());

        progressDeleteItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDeleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot progressDeleteSnapshot : dataSnapshot.getChildren()) {
                            progressDeleteSnapshot.getRef().removeValue();
                            Toast.makeText(getContext(), "Item Deleted Successfully", Toast.LENGTH_SHORT).show();
                            ((FragManager) getContext()).navigateTo(new ProgressFragment());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });

            }
        });

        return convertView;
    }
}
