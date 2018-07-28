package com.example.jay.theperdiemapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.jay.theperdiemapp.fragments.ExpenseListFragment;
import com.example.jay.theperdiemapp.fragments.ExpenseReportListFragment;
import com.example.jay.theperdiemapp.models.Report;
import com.firebase.ui.auth.AuthUI;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;


import java.util.ArrayList;
import java.util.Arrays;



public class MainActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static final String EXTRA_MESSAGE = "com.example.jay.perdiem.MESSAGE";
    private ViewPager mViewPager;
    private static final int RC_SIGN_IN = 123;
    private final ArrayList<Report> reportsArrayList = new ArrayList<>();
    FloatingActionButton mFabCamera;
    FloatingActionButton mFabMileage;
    FloatingActionButton mFabEdit;
    FloatingActionButton mFabReport;
    FloatingActionMenu fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAuth();
        buildLayoutAdaptor();
        buildFAButton();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }else if (id == R.id.signout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext() ,MainActivity.class));
            finish();
        }else{
            startActivity(new Intent(this, ProfileManagementActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    ExpenseReportListFragment expenseReportListFragment = new ExpenseReportListFragment();
                    return expenseReportListFragment;
                case 1:
                    ExpenseListFragment expenseListFragment = new ExpenseListFragment();
                    return expenseListFragment;
                    default:
                        return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Expenses";
                case 1:
                    return "Receipts";
                default:
                    return null;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatelists();
        checkButtons();

    }



    private void buildLayoutAdaptor(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }
    private void buildFAButton(){
         mFabCamera = findViewById(R.id.menu_item_camera);
         mFabMileage = findViewById(R.id.menu_item_mileage);
         mFabEdit = findViewById(R.id.manual_entry);
         mFabReport = findViewById(R.id.create_new_report);
         fab = findViewById(R.id.fab);

        fab.setClosedOnTouchOutside(true);




        mFabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!reportsArrayList.isEmpty()) {
                    Intent intent = new Intent(getBaseContext(), CreateNewActivity.class);

                    String message = "Camera";
                    intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);

                }else{
                    AlertNoReportMade();
                }
                fab.close(true);



            }
        });

        mFabMileage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!reportsArrayList.isEmpty()) {

                    Intent intent = new Intent(getBaseContext(), CreateNewActivity.class);

                    String message = "Mileage";
                    intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);
                }else{
                    AlertNoReportMade();
                }
                fab.close(true);
            }

        });

        mFabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!reportsArrayList.isEmpty()) {
                    Intent intent = new Intent(getBaseContext(), CreateNewActivity.class);

                    String message = "Expense";
                    intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);

                }else{
                    AlertNoReportMade();
                }
                fab.close(true);
            }
        });

        mFabReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), CreateNewActivity.class);
                String message = "Report";
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
                fab.close(true);


            }
        });




    }

    private void checkButtons(){
        mFabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!reportsArrayList.isEmpty()) {
                    Intent intent = new Intent(getBaseContext(), CreateNewActivity.class);

                    String message = "Camera";
                    intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);
                }else{
                    AlertNoReportMade();
                }


            }
        });

        mFabMileage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!reportsArrayList.isEmpty()) {
                    Intent intent = new Intent(getBaseContext(), CreateNewActivity.class);

                    String message = "Mileage";
                    intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);
                }else{
                    AlertNoReportMade();
                }
            }
        });

        mFabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!reportsArrayList.isEmpty()) {
                    Intent intent = new Intent(getBaseContext(), CreateNewActivity.class);

                    String message = "Expense";
                    intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);

                }else{
                    AlertNoReportMade();
                }
            }
        });
    }

    private void AlertNoReportMade(){
        new FancyAlertDialog.Builder(this)
                .setTitle("Please Create A Report Before Creating An Expense")
                .setBackgroundColor(Color.parseColor("#8FC0A9"))  //Don't pass R.color.colorvalue
                .setMessage("Would You Like To Create A Report Now?")
                .setNegativeBtnText("Cancel")
                .setPositiveBtnBackground(Color.parseColor("#8FC0A9"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("New Report")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setIcon(R.drawable.ic_007_invoice, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        Intent intent = new Intent(getBaseContext(), CreateNewActivity.class);
                        String message = "Report";
                        intent.putExtra(EXTRA_MESSAGE, message);
                        startActivity(intent);
                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {

                    }
                })
                .build();

    }


    private void updatelists(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        if (auth.getCurrentUser() != null) {
            // already signed in
            FirebaseUser fBUser = auth.getCurrentUser();
            DatabaseReference reportsEndpoint = mDatabase.child("Reports").child(fBUser.getUid());
            reportsEndpoint.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    reportsArrayList.clear();
                    for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                        Report note = noteSnapshot.getValue(Report.class);
                        reportsArrayList.add(note);

                    }
                    checkButtons();

                    buildFAButton();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }



    private void checkAuth(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        if (auth.getCurrentUser() != null) {
            // already signed in
            FirebaseUser fBUser = auth.getCurrentUser();
            DatabaseReference reportsEndpoint = mDatabase.child("Reports").child(fBUser.getUid());
            reportsEndpoint.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    reportsArrayList.clear();
                    for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                        Report note = noteSnapshot.getValue(Report.class);
                        reportsArrayList.add(note);

                    }
                    checkButtons();

                    buildFAButton();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            // not signed in
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false, true)
                            .setLogo(R.drawable.logo2)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        }

    }



}
