package com.example.jay.theperdiemapp.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.ImageView;
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

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;

public class DetailsExpenseFragment extends Fragment {

    public static DetailsExpenseFragment newInstance() {
        return new DetailsExpenseFragment();
    }

    Button Save;
    Button Back;
    Button Photo;
    EditText nameOfBusiness;
    EditText expenseDate;
    EditText expenseTotalCost;
    EditText expenseMemo;
    String TempName;
    String TempExpenseDate;
    String TempExpenseTotalCost;
    Place place;
    Camera camera;
    ImageView picFrame;
    Bitmap image;
    String imagePath;
    double total;
    Expense ChosenExpense;
    Spinner spinner1;
    Spinner spinner;
    List<String> myResArrayList;

    Boolean toAddressAdded;
    Boolean fromAddressAdded;

    int TO_PLACE_PICKER_REQUEST = 1;
    int FROM_PLACE_PICKER_REQUEST = 2;
    EditText toAddress;
    EditText fromAddress;
    EditText mileageDate;
    EditText perMile;
    EditText totalMileage;
    EditText totalCost;

    String temptoAddress;
    String tempfromAddress;
    String tempmileageDate;
    String temptotalMileage;
    String temptotalCost;
    double distanceMiles;

    double rate;
    LatLng to;
    LatLng from;


    String Category;
    int ReportSelection;
    DatabaseReference reportsEndpoint;
    ValueEventListener listener;
    private final ArrayList<String> reportsArrayListTitles = new ArrayList<>();
    private final ArrayList<Report> reportsArrayList = new ArrayList<>();
    int LOCATION_PICKER_REQUEST = 3;

    DatabaseReference reportsRef;
    DatabaseReference ExpenseRef;
    Boolean isMileage;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.setHasOptionsMenu(true);

        Intent i = getActivity().getIntent();
        if(i.hasExtra("Choosen Expense")){
            ChosenExpense = new Expense();
            Bundle bundle = i.getBundleExtra("Choosen Expense");
            String expenseName = bundle.getString("expenseName");
            if(expenseName.startsWith("Mile")){

                isMileage = true;
                return inflater.inflate(R.layout.fragment_mileage_new, container, false);

            }
        }

        isMileage = false;
        return inflater.inflate(R.layout.fragment_expense_new, container, false);

    }

    @Override
    public void onStart() {
        super.onStart();

        if(isMileage){
            getTitless();
            setupButtons2();
            setupGoogleSearchFields2();
            FillOutForm2();

        }else{
            setupGoogleSearchFields();
            setupButtons();
            FillOutForm();
        }

    }


    @Override
    public void onStop() {
        super.onStop();
    }

    private void setupButtons2(){
        Save = getActivity().findViewById(R.id.save_button);
        Save.setText("Update");
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

                if(validateFields2()){
                    saveExpenseToFirebase2();
                }

            }
        });

    }

    private void saveExpenseToFirebase2(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser fBUser = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Report report = findReport();
        double cost = report.getTotal();
        //cost -= ChosenExpense.getExpenseTotal();

        //cost += total;
        report.setTotal(cost - ChosenExpense.getExpenseTotal() + total);
        DatabaseReference reportsRef = database.getReference("Reports");
        reportsRef.child(fBUser.getUid()).child(report.getReportID()).setValue(report);

        ChosenExpense.setMileageFromAddress(tempfromAddress);
        ChosenExpense.setMileageToAddress(temptoAddress);
        ChosenExpense.setMileagePerMileRate(rate);
        ChosenExpense.setMileageTotalMiles(distanceMiles);
        ChosenExpense.setExpenseTotal(total);

        DatabaseReference ExpenseRef = database.getReference("Expenses");
        ExpenseRef.child(fBUser.getUid()).child(ChosenExpense.getExpenseID()).setValue(ChosenExpense);
        getActivity().finish();
    }

    private Boolean validateFields2(){
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

    private void setupGoogleSearchFields2(){

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


    private void FillOutForm2(){

        Intent i = getActivity().getIntent();
        if(i.hasExtra("Choosen Expense")){
            ChosenExpense = new Expense();
            Bundle bundle = i.getBundleExtra("Choosen Expense");

            String expenseID = bundle.getString("expenseID");
            String expenseName = bundle.getString("expenseName");
            Double expenseTotal1 = bundle.getDouble("expenseTotal");
            String expenseDate1 = bundle.getString("expenseDate");
            String expenseLocation = bundle.getString("expenseLocation");
            String expenseMemo1 = bundle.getString("expenseMemo");
            String expenseCategory = bundle.getString("expenseCategory");
            String addressToImage = bundle.getString("addressToImage");
            String associatedReport = bundle.getString("associatedReport");
            String mileageFromAddress = bundle.getString("mileageFromAddress");
            String mileageToAddress = bundle.getString("mileageToAddress");
            Double mileagePerMileRate = bundle.getDouble("mileagePerMileRate");
            Double mileageTotalMiles = bundle.getDouble("mileageTotalMiles");

            ChosenExpense.setExpenseID(expenseID);
            ChosenExpense.setExpenseName(expenseName);
            ChosenExpense.setExpenseTotal(expenseTotal1);
            ChosenExpense.setExpenseDate(expenseDate1);
            ChosenExpense.setExpenseLocation(expenseLocation);
            ChosenExpense.setExpenseMemo(expenseMemo1);
            ChosenExpense.setExpenseCategory(expenseCategory);
            ChosenExpense.setAddressToImage(addressToImage);
            ChosenExpense.setAssociatedReport(associatedReport);
            ChosenExpense.setMileageFromAddress(mileageFromAddress);
            ChosenExpense.setMileageToAddress(mileageToAddress);
            ChosenExpense.setMileagePerMileRate(mileagePerMileRate);
            ChosenExpense.setMileageTotalMiles(mileageTotalMiles);

            toAddress.setText(ChosenExpense.getMileageToAddress());
            fromAddress.setText(ChosenExpense.getMileageFromAddress());


            mileageDate.setText(ChosenExpense.getExpenseDate());

            String totalString = String.format("%.2f", ChosenExpense.getExpenseTotal());
            totalCost.setText("$"+totalString);
            totalString = String.format("%.2f", ChosenExpense.getMileageTotalMiles());
            totalMileage.setText(totalString+" Miles");




            ArrayList<String> ChoosenCat = new ArrayList<>();
            ChoosenCat.add(ChosenExpense.getExpenseCategory());
            ArrayList<String> ChoosenReport = new ArrayList<>();
            ChoosenReport.add(ChosenExpense.getAssociatedReport());

            spinner =  getActivity().findViewById(R.id.spinner);
            spinner1 = getActivity().findViewById(R.id.spinner2);


            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,ChoosenCat);
            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,ChoosenReport);

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            spinner1.setAdapter(adapter1);

            spinner.setEnabled(false);
            spinner1.setEnabled(false);


            String Temp = ChosenExpense.getAddressToImage();
            char first = Temp.charAt(0);

            if(first != 'N'){
                Picasso.get().load(ChosenExpense.getAddressToImage()).into(picFrame);
            }


        }


    }



    private void FillOutForm(){

        Intent i = getActivity().getIntent();
        if(i.hasExtra("Choosen Expense")){
            ChosenExpense = new Expense();
            Bundle bundle = i.getBundleExtra("Choosen Expense");

            String expenseID = bundle.getString("expenseID");
            String expenseName = bundle.getString("expenseName");
            Double expenseTotal1 = bundle.getDouble("expenseTotal");
            String expenseDate1 = bundle.getString("expenseDate");
            String expenseLocation = bundle.getString("expenseLocation");
            String expenseMemo1 = bundle.getString("expenseMemo");
            String expenseCategory = bundle.getString("expenseCategory");
            String addressToImage = bundle.getString("addressToImage");
            String associatedReport = bundle.getString("associatedReport");
            String mileageFromAddress = bundle.getString("mileageFromAddress");
            String mileageToAddress = bundle.getString("mileageToAddress");
            Double mileagePerMileRate = bundle.getDouble("mileagePerMileRate");
            Double mileageTotalMiles = bundle.getDouble("mileageTotalMiles");

            ChosenExpense.setExpenseID(expenseID);
            ChosenExpense.setExpenseName(expenseName);
            ChosenExpense.setExpenseTotal(expenseTotal1);
            ChosenExpense.setExpenseDate(expenseDate1);
            ChosenExpense.setExpenseLocation(expenseLocation);
            ChosenExpense.setExpenseMemo(expenseMemo1);
            ChosenExpense.setExpenseCategory(expenseCategory);
            ChosenExpense.setAddressToImage(addressToImage);
            ChosenExpense.setAssociatedReport(associatedReport);
            ChosenExpense.setMileageFromAddress(mileageFromAddress);
            ChosenExpense.setMileageToAddress(mileageToAddress);
            ChosenExpense.setMileagePerMileRate(mileagePerMileRate);
            ChosenExpense.setMileageTotalMiles(mileageTotalMiles);

            nameOfBusiness.setText(ChosenExpense.getExpenseName());
            expenseMemo.setText(ChosenExpense.getExpenseName());
            expenseDate.setText(ChosenExpense.getExpenseDate());
            expenseTotalCost.setText(ChosenExpense.getExpenseTotal().toString());





            ArrayList<String> ChoosenCat = new ArrayList<>();
            ChoosenCat.add(ChosenExpense.getExpenseCategory());
            ArrayList<String> ChoosenReport = new ArrayList<>();
            ChoosenReport.add(ChosenExpense.getAssociatedReport());

            spinner =  getActivity().findViewById(R.id.spinner);
            spinner1 = getActivity().findViewById(R.id.spinner2);


            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,ChoosenCat);
            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,ChoosenReport);

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            spinner1.setAdapter(adapter1);

            spinner.setEnabled(false);
            spinner1.setEnabled(false);


            String Temp = ChosenExpense.getAddressToImage();
            char first = Temp.charAt(0);

            if(first != 'N'){
                Picasso.get().load(ChosenExpense.getAddressToImage()).into(picFrame);
            }


        }


    }

    private void setupButtons(){
        Save = getActivity().findViewById(R.id.save_button);
        Save.setText("Update");
        Back = getActivity().findViewById(R.id.back_button);
        Photo = getActivity().findViewById(R.id.add_photo);
        Photo.setVisibility(View.INVISIBLE);
        picFrame = getActivity().findViewById(R.id.mileage_imageView);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().finish();

            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validateFields()){
                    saveExpenseToFirebase();

                    //    public Expense(String Name, String Date, Double Total, String ReportID, String Catagory, String FromAddress){
                }

            }
        });

        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(1)
                .setDirectory("pics")
                .setName("ali_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);





        Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    camera.takePicture();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    private void saveExpenseToFirebase(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser fBUser = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Report report = findReport();



        double cost = report.getTotal();
        cost -= ChosenExpense.getExpenseTotal();


        total = Double.parseDouble(TempExpenseTotalCost);
        cost += total;
        report.setTotal(cost);
        reportsRef = database.getReference("Reports");
        ExpenseRef = database.getReference("Expenses");



        if (imagePath != null){
            reportsRef.child(fBUser.getUid()).child(report.getReportID()).setValue(report);
            UploadImage(storageRef);


        }else{
            reportsRef.child(fBUser.getUid()).child(report.getReportID()).setValue(report);
            ChosenExpense.setExpenseName(TempName);
            ChosenExpense.setExpenseDate(TempExpenseDate);
            ChosenExpense.setExpenseTotal(total);

            ExpenseRef.child(fBUser.getUid()).child(ChosenExpense.getExpenseID()).setValue(ChosenExpense);
            getActivity().finish();
        }

    }

    private Report findReport(){
        String text = spinner1.getSelectedItem().toString();

        Report r = new Report();
        for(Report report : reportsArrayList) {
            if(report.getReportID().equals(text)){
                r = report;
            }
        }


        return r;
    }

    private void UploadImage(final StorageReference storageRef ){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser fBUser = auth.getCurrentUser();
        final Uri file = Uri.fromFile(new File(imagePath));
        final StorageReference riversRef = storageRef.child(fBUser.getUid()+"/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getActivity(),"Failed: "+exception,Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                StorageReference riversRef = storageRef.child(fBUser.getUid()+"/"+file.getLastPathSegment());
                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Toast.makeText(getActivity(),"Sucesss: "+downloadUri,Toast.LENGTH_SHORT).show();

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    final FirebaseUser fBUser = auth.getCurrentUser();

                    Report report = reportsArrayList.get(ReportSelection);


                    ChosenExpense.setExpenseName(TempName);
                    ChosenExpense.setExpenseDate(TempExpenseDate);
                    ChosenExpense.setExpenseTotal(total);
                    ChosenExpense.setAddressToImage(downloadUri.toString());

                    ExpenseRef.child(fBUser.getUid()).child(ChosenExpense.getExpenseID()).setValue(ChosenExpense);
                    getActivity().finish();



                } else {
                    // Handle failures
                    // ...
                }
            }
        });
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

        if (requestCode == LOCATION_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(getActivity(),data);
                String name = String.format(""+place.getName());
                nameOfBusiness.setText(name);
            }
        }

        if(requestCode == Camera.REQUEST_TAKE_PHOTO && !isMileage){
            image = camera.getCameraBitmap();
            imagePath = camera.getCameraBitmapPath();

            if(image != null) {

                picFrame.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            }else{
                Toast.makeText(getActivity(),"Picture not taken!",Toast.LENGTH_SHORT).show();
            }
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





    private Boolean validateFields(){
        Boolean validated = true;

        TempName = nameOfBusiness.getText().toString();
        TempExpenseDate = expenseDate.getText().toString();
        TempExpenseTotalCost = expenseTotalCost.getText().toString();


        if(TextUtils.isEmpty(TempName)) {
            nameOfBusiness.setError("Please Add a Name");
            validated = false;
        }

        if(TextUtils.isEmpty(TempExpenseDate)) {
            expenseDate.setError("Please Add A Date");
            validated = false;
        }

        if(TextUtils.isEmpty(TempExpenseTotalCost)) {
            expenseTotalCost.setError("Please Select A Date");
            validated = false;
        }


        return validated;


    }

    private void setupGoogleSearchFields(){


        nameOfBusiness = getActivity().findViewById(R.id.name_of_business1);
        expenseDate = getActivity().findViewById(R.id.expense_date);
        expenseTotalCost = getActivity().findViewById(R.id.total_cost);
        expenseMemo = getActivity().findViewById(R.id.memo);


        nameOfBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), LOCATION_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });



        expenseDate.setOnClickListener(new View.OnClickListener() {
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
                        expenseDate.setText(month1 + "/" + day1 + "/" + year1);

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

        getTitless();
    }



    private void setupSpinners(){
        String[] myResArray = getResources().getStringArray(R.array.Categories);
        myResArrayList = Arrays.asList(myResArray);

        spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.Categories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Category = myResArrayList.get(i);
//                Toast.makeText(getActivity(),Category,Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

         spinner1 = getActivity().findViewById(R.id.spinner2);
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
    private void getTitless(){
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


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

}
