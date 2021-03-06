package com.example.jay.theperdiemapp.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;


import com.example.jay.theperdiemapp.R;
import com.example.jay.theperdiemapp.models.Expense;
import com.example.jay.theperdiemapp.models.Report;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.text.FirebaseVisionCloudText;
import com.google.firebase.ml.vision.cloud.text.FirebaseVisionCloudTextDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mindorks.paracamera.Camera;
import com.stfalcon.multiimageview.MultiImageView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;

public class NewCameraFragment extends Fragment {
    public static NewCameraFragment newInstance() {
        return new NewCameraFragment();
    }

    Button Save;
    Button Back;
    Button Photo;
    Button Delete;
    EditText nameOfBusiness;
    EditText expenseDate;
    EditText expenseTotalCost;
    EditText expenseMemo;
    String TempName;
    String TempExpenseDate;
    String TempExpenseTotalCost;
    Place place;
    Camera camera;
    MultiImageView picFrame;
    Bitmap image;
    String imagePath;
    double total;
    String TAG = "com.example.jay.theperdiemapp.fragments.NewCameraFragment";
    Report selectedReport;

    String Category;
    int ReportSelection;
    DatabaseReference reportsEndpoint;
    ValueEventListener listener;
    private final ArrayList<String> reportsArrayListTitles = new ArrayList<>();
    private final ArrayList<Report> reportsArrayList = new ArrayList<>();
    int LOCATION_PICKER_REQUEST = 3;

    DatabaseReference reportsRef;
    DatabaseReference ExpenseRef;
    AlertDialog dialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_expense_new, container, false);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        takePhoto();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        getTitlesForSpinners();
        setupSpinners();
        setupGoogleSearchFields();
        setupButtons();



    }
    @Override
    public void onStop() {
        super.onStop();
        reportsEndpoint.removeEventListener(listener);
    }

    private void setupButtons(){
        Save = getActivity().findViewById(R.id.save_button);
        Back = getActivity().findViewById(R.id.back_button);
        Delete = getActivity().findViewById(R.id.delete);
        Delete.setVisibility(View.INVISIBLE);
        Back.setVisibility(View.INVISIBLE);
        Photo = getActivity().findViewById(R.id.add_photo);
        picFrame = getActivity().findViewById(R.id.mileage_imageView);
        Photo.setVisibility(View.INVISIBLE);




        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validateFields()){
                    saveExpenseToFirebase();


                }

            }
        });

    }


    private void takePhoto(){
        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(1)
                .setDirectory("pics")
                .setName("ali_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);

        try {
            camera.takePicture();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void saveExpenseToFirebase(){
        dialog = new SpotsDialog.Builder().setContext(getActivity()).build();
        dialog.setMessage("Saving");
        dialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser fBUser = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Report report = reportsArrayList.get(ReportSelection);

        reportsRef = database.getReference("Reports");
        ExpenseRef = database.getReference("Expenses");



        if (imagePath != null){
            UploadImage(storageRef,report.getReportID());

        }else{
            double cost = report.getTotal();
            total = Double.parseDouble(TempExpenseTotalCost);
            cost += total;
            report.setTotal(cost);
            reportsRef.child(fBUser.getUid()).child(report.getReportID()).setValue(report);
            Expense expense = new Expense(TempName,TempExpenseDate,total,report.getReportID(),Category,place.getAddress().toString());

            ExpenseRef.child(fBUser.getUid()).child(expense.getExpenseID()).setValue(expense);
            dialog.dismiss();
            getActivity().finish();
        }

    }

    private void UploadImage(final StorageReference storageRef, final String ReportId ){

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
                Log.d(TAG,"Failed: "+exception);

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
                    Log.d(TAG,"Success: "+downloadUri);


                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    final FirebaseUser fBUser = auth.getCurrentUser();

                    Report report = reportsArrayList.get(ReportSelection);




                    Expense expense;

                    if (place != null){
                        expense = new Expense(TempName,TempExpenseDate,total,report.getReportID(),Category,place.getAddress().toString(),downloadUri.toString());
                    }else{
                        expense = new Expense(TempName,TempExpenseDate,total,report.getReportID(),Category,"N/A",downloadUri.toString());
                    }



                    ExpenseRef.child(fBUser.getUid()).child(expense.getExpenseID()).setValue(expense);

                    double cost = report.getTotal();
                    total = Double.parseDouble(TempExpenseTotalCost);
                    cost += total;
                    report.setTotal(cost);
                    reportsRef.child(fBUser.getUid()).child(report.getReportID()).setValue(report);
                    dialog.dismiss();
                    getActivity().finish();


                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,""+requestCode);

        if (requestCode == LOCATION_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(getActivity(),data);
                String name = String.format(""+place.getName());
                nameOfBusiness.setText(name);
            }
        }

        if(requestCode == Camera.REQUEST_TAKE_PHOTO){
            image = camera.getCameraBitmap();


            if(image != null) {
                imagePath = camera.getCameraBitmapPath();
                picFrame.addImage(BitmapFactory.decodeFile(imagePath));
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(BitmapFactory.decodeFile(imagePath));

                dialog = new SpotsDialog.Builder().setContext(getActivity()).build();


                FirebaseVisionCloudTextDetector detector = FirebaseVision.getInstance()
                        .getVisionCloudTextDetector();
                dialog.show();
                Task<FirebaseVisionCloudText> result = detector.detectInImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionCloudText>() {
                            @Override
                            public void onSuccess(FirebaseVisionCloudText firebaseVisionCloudText) {
                                // Task completed successfully
                                // ...
                                if(firebaseVisionCloudText != null){
                                    Log.d(TAG,"success"+firebaseVisionCloudText.getText());
                                    fillForm(firebaseVisionCloudText.getText());
                                }else{
                                    Toast.makeText(getActivity(),"Sorry No Text Found",Toast.LENGTH_SHORT).show();
                                    Log.d(TAG,"Success But No Text Found");
                                }

                                dialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                                Log.d(TAG,"Failed"+e);

                            }
                        });

            }else{
                Log.d(TAG,"Picture not taken!");
            }
        }


    }

    private void fillForm(String s){
        ArrayList<String> aList= new ArrayList(Arrays.asList(s.split("\\r?\\n")));

        String name =  aList.get(0).toString();
        nameOfBusiness.setText(name);
        String Ty ="";
        for(String s1 : aList){

            if(s1.startsWith("$")){
                expenseTotalCost.setText(s1);


            }else if(s1.startsWith("Total")||s1.startsWith("Grand Total")||s1.startsWith("Payment")||s1.startsWith("Sale Amount")){
               Ty = aList.get(aList.indexOf(s1)+1);

               Log.d("FOUND THIS", ""+s1);
               Toast.makeText(getActivity(),""+s1,Toast.LENGTH_SHORT).show();

               expenseTotalCost.setText(Ty);
           }
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

        if(!checkIfDateIsCorrect()){
            expenseDate.setError("Date Outside of Range");

            if (selectedReport != null){
                Toast.makeText(getActivity(),"Date Outside of Selected report Range "+selectedReport.getStartDate()+" - "+selectedReport.getEndDate(),Toast.LENGTH_SHORT).show();
            }
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
    }



    private void setupSpinners(){
        String[] myResArray = getResources().getStringArray(R.array.Categories);
        final List<String> myResArrayList = Arrays.asList(myResArray);

        Spinner spinner = getActivity().findViewById(R.id.spinner);
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

                Log.d(TAG,Category);


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
    private Boolean checkIfDateIsCorrect(){
        if (!reportsArrayList.isEmpty()){
            selectedReport = reportsArrayList.get(ReportSelection);

            Boolean temp = false;
            SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy");

            try {
                Date min, max;   // assume these are set to something
                Date d;
                d = dateFormat.parse(TempExpenseDate);
                min = dateFormat.parse(selectedReport.getStartDate());
                max = dateFormat.parse(selectedReport.getEndDate());

                if (d.compareTo(min) >= 0 && d.compareTo(max) <= 0){
                    temp=true;
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }

            return temp;
        }
        return false;
    }


}



