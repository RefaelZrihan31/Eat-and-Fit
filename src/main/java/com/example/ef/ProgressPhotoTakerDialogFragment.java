package com.example.ef;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * חלון המאפשר לבחור תמונת פרופיל עבור המשתמש
 * מתוך מצלמת המכשיר או מתוך הגלרייה
 * שימוש עבור פריגמנט Progress
 */


public class ProgressPhotoTakerDialogFragment extends DialogFragment implements View.OnClickListener {
    View root;
    private static final int TAKE_PHOTO = 6;
    private static final int GALLERY_PHOTO = 7;
    Button btnTakePhoto, btnGallery;
    ImageView imageView;
    FirebaseStorage firebaseStorage;
    StorageReference storageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_dialog_progress_photo_taker, null);

            initVarbs();

            initViews();

            initButtons();
        }
        return root;
    }

    /**
     * אתחול אובייקטים בדף
     */
    private void initVarbs() {
        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference("UserImages/");
    }

    /**
     * אתחול הכפתורים בדף
     */
    private void initButtons() {
        btnTakePhoto.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
    }

    /**
     * אתחול המשתנים בדף
     */
    private void initViews() {
        btnTakePhoto = root.findViewById(R.id.btnTakePhotoProgress);
        btnGallery = root.findViewById(R.id.btnTakeGalleryProgress);
        imageView = root.findViewById(R.id.imageViewProgress);
    }

    // פונקציה המאפשרת כניסה לגלריית המכשיר
    private void takeGalleryAction() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, GALLERY_PHOTO);//one can be replaced with any action code
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PHOTO);
        }
    }

    //פונקציה המאפשרת כניסה אל מצלמת המכשיר
    private void takePhotoAction() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePhoto, TAKE_PHOTO);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, TAKE_PHOTO);
        }
    }

    @Override// קבלת אישור גישה עבור מצלמת המכשיר
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePhoto, TAKE_PHOTO);
                }
                break;
        }
    }

    @Override//פונקציה המטפלת בצילום התמונה או בחירת תמונה מהגלרייה והעאלתה אל מסד הנתונים
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //יצירת אובייקט שיכיל את פורמט חתימת הזמן של התמונה שצולמה לפי התאריך הנוכחי
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("ddMMyyyy");
        String photoTimeStamp = s.format(new Date());
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getContext(), "Image capture.", Toast.LENGTH_SHORT).show();
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imageView.setImageBitmap(imageBitmap);


                    // העלאת תמונה למסד נתונים באמצעות המצלמה
                    imageView.setDrawingCacheEnabled(true);
                    imageView.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] bb = baos.toByteArray();

                    UploadTask uploadTask = storageRef.child(FirebaseAuth.getInstance().getUid()).child(photoTimeStamp + ".jpg").putBytes(bb);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Successful Upload.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getContext(), "Upload Failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                    editor.putString("progressImageTime", photoTimeStamp);
                    editor.apply();

                } else {
                    Toast.makeText(getContext(), "Operation canceled.", Toast.LENGTH_SHORT).show();
                }
                break;

            //אם המשתמש בחר לעלות תמונה מהגלריה - תמונה זו תתווסף אל מסד הנתונים לאחר הבחירה
            case GALLERY_PHOTO:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getContext(), "Image capture.", Toast.LENGTH_SHORT).show();
                    Uri pickedImage = data.getData();
                    imageView.setImageURI(pickedImage);
                    storageRef.child(FirebaseAuth.getInstance().getUid())
                            .child(photoTimeStamp + ".jpg").putFile(pickedImage);
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                    editor.putString("progressImageTime", photoTimeStamp);
                    editor.apply();

                } else {
                    Toast.makeText(getContext(), "Operation canceled.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTakePhotoProgress:
                takePhotoAction();
                break;
            case R.id.btnTakeGalleryProgress:
                takeGalleryAction();
                break;
        }
    }
}

