package com.example.ef;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ef.fragments.DiarySlideScanHistoryFragment;
import com.example.ef.fragments.HomeFragment;
import com.example.ef.fragments.MoreFragment;
import com.example.ef.fragments.ProgressFragment;
import com.example.ef.fragments.ScanBarcodeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

/**
 * דף ניהול המאפשר מעבר בין כל הפרגמנטים השונים המוצגים
 * למשתמש לאחר תהליך ההרשמה וכינסתו למערכת
 */

public class FragManager extends AppCompatActivity {

    BottomNavigationView bottomNav;
    FloatingActionButton scanBarcode;
    FragmentManager fragmentManager;

    /**
     * בעת לחיצה על כפתר ״אחורה״ של המכשיר , תוקפץ הודעה עבור המשתמש האם לצאת
     * לחלוטין מהאפליקציה
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder =new AlertDialog.Builder(this,R.style.Theme_EF_Dialog);
        builder.setTitle("Exit");
        builder.setMessage("Are You Sure You Want To Exit?");
        builder.setNegativeButton(android.R.string.no, null);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finishAffinity();
                        System.exit(0);
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        // Creating Dynamic
        Rect displayRectangle = new Rect();

        Window window = this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        alertDialog.getWindow().setLayout((int) (displayRectangle.width() *
                0.8f), (int) (displayRectangle.height() * 0.2f));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frag_manager);
        scanBarcode = findViewById(R.id.icScanBarcode);
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        fragmentManager = getSupportFragmentManager();

        /** בעת לחיצה על כפתור הסריקה מתוך סרגל הניווט יתבצע מעבר את דף סריקת הברקוד*/
        scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ScanBarcodeFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null) // name can be null
                        .commit();
                bottomNav.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_UNLABELED);
            }
        });

        // בעת כניסה ראשונית לאפלייקציה הדף שיוצג יהיה HomeFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().replace(R.id.fragment_container, new HomeFragment()).addToBackStack(null).commit();
        }
    }

    /**
     * מימוש סרגל ניווט ומעברים עבור כל כפתור בסרגל את הפריגמנט המתאים
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = new HomeFragment();
                    switch (item.getItemId()) {
                        case R.id.icHome:
                            selectedFragment = new HomeFragment();
                            break;

                        case R.id.icDiary:
                            selectedFragment = new DiarySlideScanHistoryFragment();
                            break;

                        case R.id.icProgress:
                            selectedFragment = new ProgressFragment();
                            break;

                        case R.id.icMore:
                            selectedFragment = new MoreFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .addToBackStack(null)
                            .commit();
                    bottomNav.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_AUTO);
                    return true;
                }
            };

    /**
     * פונקציה המבצעת טעינה מחודשת עבור הפריגמנט שהתקבל - ריענון מסך
     */
    public void navigateTo(Fragment fragment) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment).addToBackStack(null);
        transaction.commit();
    }
}

