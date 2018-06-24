package com.example.jay.theperdiemapp.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.sip.SipAudioCall;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.jay.theperdiemapp.R;

import com.example.jay.theperdiemapp.models.Expense;
import com.example.jay.theperdiemapp.models.Report;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import net.mskurt.neveremptylistviewlibrary.NeverEmptyListView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;

public class NewMileageFragment extends Fragment {

    public static NewMileageFragment newInstance() {
        return new NewMileageFragment();
    }
    private final ArrayList<String> reportsArrayListTitles = new ArrayList<>();
    private final ArrayList<Report> reportsArrayList = new ArrayList<>();
    int TO_PLACE_PICKER_REQUEST = 1;
    int FROM_PLACE_PICKER_REQUEST = 2;
    EditText toAddress;
    EditText fromAddress;
    EditText mileageDate;
    EditText perMile;
    EditText totalMileage;
    EditText totalCost;
    Button Save;
    Button Back;
    Boolean toAddressAdded;
    Boolean fromAddressAdded;
    String Category;
    double rate;
    LatLng to;
    LatLng from;
    int ReportSelection;
    ValueEventListener listener;
    DatabaseReference reportsEndpoint;

    String temptoAddress;
    String tempfromAddress;
    String tempmileageDate;
    String temptotalMileage;
    String temptotalCost;
    double distanceMiles;
    double total;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_mileage_new, container, false);

    }

    @Override
    public void onStart() {
        super.onStart();


        setupButtons();
        setupGoogleSearchFields();
        getTitlesForSpinners();


    }

    @Override
    public void onStop() {
        super.onStop();
        reportsEndpoint.removeEventListener(listener);
    }

    private void setupButtons(){
        Save = getActivity().findViewById(R.id.save_button);
        Back = getActivity().findViewById(R.id.back_button);
        Back.setVisibility(View.INVISIBLE);

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validateFields()){
                    saveExpenseToFirebase();
                }

            }
        });

    }

    private void saveExpenseToFirebase(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser fBUser = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Report report = reportsArrayList.get(ReportSelection);
        double cost = report.getTotal();
        cost += total;
        report.setTotal(cost);
        DatabaseReference reportsRef = database.getReference("Reports");
        reportsRef.child(fBUser.getUid()).child(report.getReportID()).setValue(report);
        Expense expense = new Expense(tempfromAddress,temptoAddress,tempmileageDate,rate,distanceMiles,total,report.getReportID(),Category);
        DatabaseReference ExpenseRef = database.getReference("Expenses");
        ExpenseRef.child(fBUser.getUid()).child(expense.getExpenseID()).setValue(expense);
        getActivity().finish();
    }

    private Boolean validateFields(){
        Boolean validated = true;
        temptoAddress = toAddress.getText().toString();
        tempfromAddress = fromAddress.getText().toString();
        tempmileageDate = mileageDate.getText().toString();
        temptotalMileage = totalMileage.getText().toString();
        temptotalCost = totalCost.getText().toString();
        if(TextUtils.isEmpty(temptoAddress)) {
            toAddress.setError("Please Add a To Address");
            validated = false;
        }

        if(TextUtils.isEmpty(tempfromAddress)) {
            fromAddress.setError("Please Add A From Address");
            validated = false;
        }

        if(TextUtils.isEmpty(tempmileageDate)) {
            mileageDate.setError("Please Select A Date");
            validated = false;
        }


        if(TextUtils.isEmpty(temptotalMileage)) {
            totalMileage.setError("Please Add To And From Address");
            validated = false;
        }

        if(TextUtils.isEmpty(temptotalCost)) {
            totalCost.setError("Please Add To And From Address");
            validated = false;
        }

        return validated;


    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TO_PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getActivity(),data);
                String addy = String.format(""+place.getAddress());
                toAddress.setText(addy);
                to = place.getLatLng();
                toAddressAdded = true;
                calculateTotalCost();

            }
        } else if(requestCode == FROM_PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(getActivity(),data);
            String addy = String.format(""+place.getAddress());
            fromAddress.setText(addy);
            from = place.getLatLng();
            fromAddressAdded = true;
                calculateTotalCost();
            }
        }else{

        }

    }


    private void calculateTotalCost(){

        if(toAddressAdded && fromAddressAdded){
            DecimalFormat df2 = new DecimalFormat(".##");
            double distanceMeters = SphericalUtil.computeDistanceBetween(to, from);
            distanceMiles = distanceMeters * 0.00062137;
            total = distanceMiles * rate;
            String distanceFinal = df2.format(distanceMiles);
            String costFinal = df2.format(total);
            totalMileage.setText(distanceFinal+" Miles");
            totalCost.setText("$"+costFinal);
        }

    }

    private void setupGoogleSearchFields(){

        toAddressAdded = false;
        fromAddressAdded = false;
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

        if(prefs.contains("per_mile")){
            prefs.getString("per_mile","1");
            perMile = getActivity().findViewById(R.id.per_mile_amount);
            perMile.setText(prefs.getString("per_mile","1"));
            rate = Double.parseDouble(prefs.getString("per_mile","1"));
        }else {
            rate = 1.0;
        };

        toAddress = getActivity().findViewById(R.id.mileage_travelingTo);
        fromAddress = getActivity().findViewById(R.id.mileage_travelingFrom);
        mileageDate = getActivity().findViewById(R.id.mileage_date);
        totalMileage = getActivity().findViewById(R.id.miles);
        totalCost = getActivity().findViewById(R.id.total_cost);

        toAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), TO_PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        fromAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), FROM_PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        mileageDate.setOnClickListener(new View.OnClickListener() {
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
                        mileageDate.setText(month1 + "/" + day1 + "/" + year1);

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

    private void setupSpinners(){
        String[] myResArray = getResources().getStringArray(R.array.Categories);
        final List<String> myResArrayList = Arrays.asList(myResArray);

        Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.Categories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Category = myResArrayList.get(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner spinner1 = getActivity().findViewById(R.id.spinner2);
        // Create an ArrayAdapter using the string array and a default spinner layout

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,reportsArrayListTitles);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner1.setAdapter(adapter1);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                ReportSelection = i;




            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


    private void getTitlesForSpinners(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        if (auth.getCurrentUser() != null) {

            // already signed in
            FirebaseUser fBUser = auth.getCurrentUser();
            reportsEndpoint = mDatabase.child("Reports").child(fBUser.getUid());

            listener = reportsEndpoint.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    reportsArrayListTitles.clear();
                    for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                        Report note = noteSnapshot.getValue(Report.class);
                        reportsArrayListTitles.add(Objects.requireNonNull(note).getName());
                        reportsArrayList.add(Objects.requireNonNull(note));

                    }

                    setupSpinners();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }


}


