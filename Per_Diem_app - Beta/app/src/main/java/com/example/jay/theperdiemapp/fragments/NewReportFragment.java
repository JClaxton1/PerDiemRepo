package com.example.jay.theperdiemapp.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;


import com.example.jay.theperdiemapp.R;
import com.example.jay.theperdiemapp.models.Report;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class NewReportFragment extends Fragment {

    public static NewReportFragment newInstance() {
        return new NewReportFragment();
    }

    EditText startDate;
    EditText endDate;
    EditText reportName;
    EditText memo;
    String startDateString;
    String endDateString;
    String memoString;
    Button Save;
    Button Back;
    String TAG = "com.example.jay.theperdiemapp.fragments.NewReportFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_report_new_add, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setupViews();
        setupButtons();
        setupDates();

    }

    private void setupButtons(){
        Save = getActivity().findViewById(R.id.save_button);
        Back = getActivity().findViewById(R.id.back_button);
        Back.setVisibility(View.INVISIBLE);

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFields();
            }
        });

    }

    private void setupViews(){
        startDate = getActivity().findViewById(R.id.start_date);
        endDate = getActivity().findViewById(R.id.end_date);
        reportName = getActivity().findViewById(R.id.report_name);
        memo = getActivity().findViewById(R.id.memo);


    }



    private void setupDates(){


        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

                // Create the DatePickerDialog instance


// Listener
                DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

                    // when dialog box is closed, below method will be called.
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        String year1 = String.valueOf(selectedYear);
                        String month1 = String.valueOf(selectedMonth + 1);
                        String day1 = String.valueOf(selectedDay);
                        startDateString = month1 + "/" + day1 + "/" + year1;
                        startDate.setText(startDateString);


                    }
                };

                DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                        datePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle("Select the date");
                datePicker.show();


            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

                // Create the DatePickerDialog instance


// Listener
                DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

                    // when dialog box is closed, below method will be called.
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        String year1 = String.valueOf(selectedYear);
                        String month1 = String.valueOf(selectedMonth + 1);
                        String day1 = String.valueOf(selectedDay);
                        endDateString = month1 + "/" + day1 + "/" + year1;
                        endDate.setText(endDateString);


                    }
                };

                DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                        datePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle("Select the date");
                datePicker.show();


            }
        });
    }

    private void validateFields(){

        if(!TextUtils.isEmpty(memo.getText().toString())){
            memoString = memo.getText().toString();
        }else{
            memoString = "";
        }

        ArrayList<EditText> editTextList = new ArrayList<>();
        editTextList.add(startDate);
        editTextList.add(endDate);
        editTextList.add(reportName);
        int i = 0;


        for(EditText edit : editTextList){
            if(!TextUtils.isEmpty(edit.getText())){
                i++;
            }
        }
        editTextList.clear();

        if (i == 3){
            saveOutToFireBase();
        }else{
            Log.e(TAG,"Fields Are not Valid!");
        }
    }
    private void saveOutToFireBase(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser fBUser = auth.getCurrentUser();
        Report report = new Report(reportName.getText().toString(),startDateString,endDateString,memoString);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reportsRef = database.getReference("Reports");
        reportsRef.child(fBUser.getUid()).child(report.getReportID()).setValue(report);
        getActivity().finish();

        }





}
