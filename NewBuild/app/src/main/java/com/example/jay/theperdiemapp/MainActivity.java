package com.example.jay.theperdiemapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jay.theperdiemapp.fragments.ExpenseListFragment;
import com.example.jay.theperdiemapp.fragments.ExpenseReportListFragment;
import com.firebase.ui.auth.AuthUI;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static final String EXTRA_MESSAGE = "com.example.jay.perdiem.MESSAGE";

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAuth();
        buildLayoutAdaptor();
        buildFABbutton();



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

    /**
     * A placeholder fragment containing a simple view.
     */

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    ExpenseReportListFragment erlf = new ExpenseReportListFragment();
                    return erlf;
                case 1:
                    ExpenseListFragment elf = new ExpenseListFragment();
                    return elf;
                    default:
                        return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
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

    private void buildLayoutAdaptor(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }
    private void buildFABbutton(){
        FloatingActionButton mFabCamera = findViewById(R.id.menu_item_camera);
        FloatingActionButton mFabMileage = findViewById(R.id.menu_item_mileage);
        FloatingActionButton mFabEdit = findViewById(R.id.manual_entry);
        FloatingActionButton mFabReport = findViewById(R.id.create_new_report);



        mFabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), CreateNewActivity.class);

                String message = "Camera";
                intent.putExtra(EXTRA_MESSAGE, message);
          //      intent.putStringArrayListExtra(EXTRA_REPORTS_ARRAY_LIST,reportsArrayListTitles);
                startActivity(intent);



            }
        });

        mFabMileage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), CreateNewActivity.class);

                String message = "Mileage";
                intent.putExtra(EXTRA_MESSAGE, message);
             //   intent.putStringArrayListExtra(EXTRA_REPORTS_ARRAY_LIST,reportsArrayListTitles);
                startActivity(intent);
            }
        });

        mFabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), CreateNewActivity.class);

                String message = "Expense";
                intent.putExtra(EXTRA_MESSAGE, message);
                //   intent.putStringArrayListExtra(EXTRA_REPORTS_ARRAY_LIST,reportsArrayListTitles);
                startActivity(intent);
            }
        });

        mFabReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), CreateNewActivity.class);
                String message = "Report";
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);

            }
        });


        //TODO Hide OCR Camera Button
        mFabCamera.setEnabled(false);

    }


    private void checkAuth(){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        if (auth.getCurrentUser() != null) {

//            // already signed in
//            FirebaseUser fBUser = auth.getCurrentUser();
//
//            userName.setText(fBUser.getDisplayName());
//
//            DatabaseReference reportsEndpoint = mDatabase.child("Reports").child(fBUser.getUid());
//
//
//
//
//
//            reportsEndpoint.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    reportsArrayList.clear();
//                    reportsArrayListTitles.clear();
//
//                    for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
//                        Reports note = noteSnapshot.getValue(Reports.class);
//
//                        reportsArrayList.add(note);
//                        reportsArrayListTitles.add(Objects.requireNonNull(note).getIdentifier());
//
//
//                        if(note.getExpenses() != null) {
//                            for (Expense expense : note.getExpenses()) {
//                                expensesArrayList.add(expense);
//                            }
//                        }
//
//
//
//
//
//                        mAdapter = new ReportAdaptor(getBaseContext(),reportsArrayList);
//                        ArrayAdapter adapter = new ArrayAdapter<>(getBaseContext(),
//                                R.layout.activity_listview, reportsArrayList);
//
//
//                        report_count.setText(adapter.getCount()+"");
//                        if(adapter.getCount()>1){
//                            report_count_label.setText("Reports");
//                        }else{
//                            report_count_label.setText("Report");
//                        }
//
//
//
//                        ListView listView = findViewById(R.id.list);
//                        listView.setAdapter(mAdapter);
//
//                    }
//
//
//
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//            //  Users currentUser = new Users(fBUser.getDisplayName(), fBUser.getEmail(), fBUser.getUid());
//            //tv.setText(currentUser.getUid());
//





        } else {
            // not signed in
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false, true)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        }

    }
}
