package com.example.jay.theperdiemapp.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;



import com.example.jay.theperdiemapp.MainActivity;
import com.example.jay.theperdiemapp.R;
import com.example.jay.theperdiemapp.adaptors.DataPointAdaptor;
import com.example.jay.theperdiemapp.adaptors.ExpenseAdaptor;
import com.example.jay.theperdiemapp.models.Expense;
import com.example.jay.theperdiemapp.models.Report;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.SphericalUtil;
import com.mindorks.paracamera.Camera;
import com.squareup.picasso.Picasso;
import net.mskurt.neveremptylistviewlibrary.NeverEmptyListView;





import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;

public class DetailsReportFragment extends Fragment {

    public static DetailsReportFragment newInstance() {
        return new DetailsReportFragment();
    }

    Button Save;
    Button Back;


    DatabaseReference reportsEndpoint;
    DatabaseReference expenseEndpoint;
    ValueEventListener listener;
    private final ArrayList<Report> reportsArrayList = new ArrayList<>();
    private final ArrayList<Expense> expenseArrayList = new ArrayList<>();
    private ArrayList<Expense> selectedExpenses = new ArrayList<>();
    String selectedReport;


    private ViewPager mViewPager;
    DataPointAdaptor dataPointAdaptor;


    Context context1;


    DatabaseReference reportsRef;
    DatabaseReference ExpenseRef;
    Boolean isMileage;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.setHasOptionsMenu(true);

//        Intent i = getActivity().getIntent();
//        if(i.hasExtra("Choosen Expense")){
//            ChosenExpense = new Expense();
//            Bundle bundle = i.getBundleExtra("Choosen Expense");
//            String expenseName = bundle.getString("expenseName");
//            if(expenseName.startsWith("Mile")){
//
//                isMileage = true;
//                return inflater.inflate(R.layout.fragment_mileage_new, container, false);
//
//            }
//        }
//
//        isMileage = false;




        return inflater.inflate(R.layout.fragment_report_details, container, false);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }



    @Override
    public void onStart() {
        super.onStart();

        Intent i = getActivity().getIntent();
        if(i.hasExtra("Choosen Expense")){
            selectedReport=i.getStringExtra("Choosen Expense");
        }
        getData();
        setupButtons();







    }

    private void setupDataVisuals(){
        mViewPager = getActivity().findViewById(R.id.pager);
        dataPointAdaptor = new DataPointAdaptor(getActivity(),reportsArrayList,expenseArrayList,selectedExpenses);
        mViewPager.setAdapter(dataPointAdaptor);



    }



    @Override
    public void onStop() {
        super.onStop();
    }






    private void setupButtons(){
        Save = getActivity().findViewById(R.id.save_button);
        Back = getActivity().findViewById(R.id.back_button);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().finish();

            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }


//    private Report findReport(){
//
//        Report r = new Report();
//        for(Report report : reportsArrayList) {
//            if(report.getReportID().equals()){
//                r = report;
//            }
//        }
//
//
//        return r;
//    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {


    }



    private ArrayList<Expense> getRelatedExpenses(ArrayList<Expense> fullList){
        ArrayList<Expense> temp = new ArrayList<>();

        for(Expense expense : fullList){
            if(expense.getAssociatedReport().equals(selectedReport)){
                temp.add(expense);
            }

        }





        return temp;
    }







    private void getData(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        if (auth.getCurrentUser() != null) {

            // already signed in
            FirebaseUser fBUser = auth.getCurrentUser();
            reportsEndpoint = mDatabase.child("Reports").child(fBUser.getUid());
            expenseEndpoint = mDatabase.child("Expenses").child(fBUser.getUid());

            listener = expenseEndpoint.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    expenseArrayList.clear();
                    selectedExpenses.clear();

                    for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                        Expense note = noteSnapshot.getValue(Expense.class);
                        expenseArrayList.add(Objects.requireNonNull(note));

                    }

//                    Toast.makeText(getActivity(),"List count: "+expenseArrayList.size(),Toast.LENGTH_LONG).show();
                    selectedExpenses = getRelatedExpenses(expenseArrayList);

                    if(getActivity()!=  null){
                        ExpenseAdaptor adapter = new ExpenseAdaptor(getActivity(),selectedExpenses);
                        NeverEmptyListView neverEmptyListView = getActivity().findViewById(R.id.listview5);
                        neverEmptyListView.setAdapter(adapter);
                        setupDataVisuals();
                    }




                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            listener = reportsEndpoint.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    reportsArrayList.clear();

                    for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                        Report note = noteSnapshot.getValue(Report.class);
                        reportsArrayList.add(Objects.requireNonNull(note));

                    }

//                    Toast.makeText(getActivity(),"Reports count: "+reportsArrayList.size(),Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

}
