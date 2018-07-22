package com.example.jay.theperdiemapp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.jay.theperdiemapp.R;
import com.example.jay.theperdiemapp.adaptors.DataPointAdaptor;
import com.example.jay.theperdiemapp.adaptors.ExpenseAdaptor;
import com.example.jay.theperdiemapp.models.Expense;
import com.example.jay.theperdiemapp.models.Report;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import net.mskurt.neveremptylistviewlibrary.NeverEmptyListView;


import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.CacheControl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailsReportFragment extends Fragment {

    public static DetailsReportFragment newInstance() {
        return new DetailsReportFragment();
    }
    public static final MediaType JSON = MediaType.parse("application/json");


    Button Save;
    Button Back;
    TextView nameTextView;
    TextView emailTextView;
    private File pdfFile;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private static final String TAG = "DetailsReportFragment";

    DatabaseReference reportsEndpoint;
    DatabaseReference expenseEndpoint;
    ValueEventListener listener;
    private final ArrayList<Report> reportsArrayList = new ArrayList<>();
    private final ArrayList<Expense> expenseArrayList = new ArrayList<>();
    private ArrayList<Expense> selectedExpenses = new ArrayList<>();
    String selectedReport;
    Report selectReport;
    OkHttpClient client = new OkHttpClient();



    private ViewPager mViewPager;
    DataPointAdaptor dataPointAdaptor;



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
        emailTextView = getActivity().findViewById(R.id.emailTextView5);
        nameTextView = getActivity().findViewById(R.id.nameTextView5);
        findReportnoReturn();






        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().finish();

            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chooseSubmissionMethod();


            }
        });

    }


    private Report findReport(){

         selectReport = new Report();
        for(Report report : reportsArrayList) {
            if(report.getReportID().equals(selectedReport)){
                selectReport = report;
            }
        }


        return selectReport;
    }

    private void findReportnoReturn(){

        selectReport = new Report();
        for(Report report : reportsArrayList) {
            if(report.getReportID().equals(selectedReport)){
                selectReport = report;
            }
        }

    }


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

                    nameTextView.setText(selectedReport);
                    Report r = findReport();

                    String totalString = String.format("%.2f", r.getTotal());
                    emailTextView.setText("$"+totalString);

//                    Toast.makeText(getActivity(),"Reports count: "+reportsArrayList.size(),Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }



    private void createPdfWrapper() throws FileNotFoundException,DocumentException{

        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);


                                    }
                                }
                            });
                    return;
                }

                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        }else {
            createPdf();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    try {
                        createPdfWrapper();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "WRITE_EXTERNAL Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void createPdf() throws FileNotFoundException, DocumentException {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }

        pdfFile = new File(docsFolder.getAbsolutePath(),auth.getCurrentUser().getDisplayName()+" Per Diem Expense Report.pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();

        document.addAuthor(auth.getCurrentUser().getDisplayName());
        document.addCreationDate();
        document.add(new Paragraph(auth.getCurrentUser().getDisplayName()));
        document.add(new Paragraph(auth.getCurrentUser().getEmail()));
        document.add(new Paragraph("Report Name: "+reportsArrayList.get(0).getName()));
        document.add(new Paragraph("Report Dates: "+reportsArrayList.get(0).getStartDate()+" - "+reportsArrayList.get(0).getEndDate()));
        String result =  String.format("%.2f", reportsArrayList.get(0).getTotal());
        document.add(new Paragraph("Report Total: $"+ result));

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("EXPENSES"));
        document.add(new Paragraph(" "));

        for (final Expense expense: selectedExpenses){
            document.add(new Paragraph("Expense Name: "+expense.getExpenseName()));
            document.add(new Paragraph("Expense Date: "+expense.getExpenseDate()));
            document.add(new Paragraph("Expense Memo: "+expense.getExpenseMemo()));
            document.add(new Paragraph("Expense Image: "+expense.getAddressToImage()));
            document.add(new Paragraph("Expense Location: "+expense.getExpenseLocation()));
            result =  String.format("%.2f", expense.getExpenseTotal());
            document.add(new Paragraph("Expense Total: $"+result));
            document.add(new Paragraph(" "));

            if(!expense.getAddressToImage().startsWith("N")){
                try {
                    URL url = new URL(expense.getAddressToImage());
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Image img = Image.getInstance(url);
                    document.add(img);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        }



        document.close();
        previewPdf();
        sendPDF();

    }





    private void sendPDF(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {auth.getCurrentUser().getEmail()});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Per Diem Expense Report");
        intent.putExtra(Intent.EXTRA_TEXT, auth.getCurrentUser().getDisplayName()+" Per Diem - New Expense Report");

        if (!pdfFile.exists() || !pdfFile.canRead()) {
            Toast.makeText(getActivity(), "Attachment Error", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Uri uri = Uri.fromFile(pdfFile);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Send email..."));
    }

    private void previewPdf() {

        PackageManager packageManager = getActivity().getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(pdfFile);
            intent.setDataAndType(uri, "application/pdf");

            startActivity(intent);
        }else{
            Toast.makeText(getActivity(),"Download a PDF Viewer to see the generated PDF",Toast.LENGTH_SHORT).show();
        }
    }


    private void chooseSubmissionMethod(){
        new FancyAlertDialog.Builder(getActivity())
                .setTitle("Submit Report")
                .setBackgroundColor(Color.parseColor("#8FC0A9"))  //Don't pass R.color.colorvalue
                .setMessage("How Would You Like To Submit?")
                .setNegativeBtnText("NetSuite")
                .setPositiveBtnBackground(Color.parseColor("#8FC0A9"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Email")
                .setNegativeBtnBackground(Color.parseColor("#7ca38f"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setIcon(R.drawable.ic_007_invoice, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        try {
                            createPdfWrapper();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {

                        if (!selectedExpenses.isEmpty()){
                            if(!findReport().getStatus().equals("SUBMITTED")){
                                thread.start();
                            }else{
                                Toast.makeText(getActivity(),"This Report Has Already Been Submitted",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(getActivity(),"You Must Create At Least One Expense Before Submitting",Toast.LENGTH_LONG).show();
                        }




                    }
                })
                .build();
        }



    String buildJson() {

        Report report = findReport();

        String NetSuiteUsername = "";
        String NetsuitePassword = "";



        String rName = report.getName();
        rName = rName.replaceAll("[']","");

        String rMemo = report.getMemo();
        rMemo = rMemo.replaceAll("[']","");

        String rID = report.getReportID();
        rID = rID.replaceAll("[']","");

        String rStartDate = report.getStartDate();
        rStartDate = rStartDate.replaceAll("[']","");

        String rEndDate = report.getEndDate();
        rEndDate = rEndDate.replaceAll("[']","");

        String rStatus = report.getStatus();
        rStatus = rStatus.replaceAll("[']","");

        String rTotal = report.getTotal().toString();
        rTotal = rTotal.replaceAll("[']","");

        String addressToImage = "";
        String associatedReport = "";
        String expenseCategory = "";
        String expenseDate = "";
        String expenseID = "";
        String expenseLocation = "";
        String expenseMemo = "";
        String expenseName = "";
        String expenseTotal = "";
        String mileageFromAddress = "";
        String mileagePerMileRate = "";
        String mileageToAddress = "";
        String mileageTotalMiles = "";

        String string = "{'Username':'"+NetSuiteUsername+"',"
                + "'Password':'"+NetsuitePassword+"',"
                + "'Report':"
                +     "{'rName':'" + rName +
                     "','rMemo':'" + rMemo +
                       "','rID':'" + rID +
                "','rStartDate':'" + rStartDate +
                  "','rEndDate':'" + rEndDate +
                   "','rStatus':'" + rStatus +
                    "','rTotal':'" + rTotal +
                    "','Expenses':[" ;

        for(Expense expense: selectedExpenses){
            addressToImage = expense.getAddressToImage();
            addressToImage = addressToImage.replaceAll("[']","");

            associatedReport = expense.getAssociatedReport();
            associatedReport = associatedReport.replaceAll("[']","");

            expenseCategory = expense.getExpenseCategory();
            expenseCategory = expenseCategory.replaceAll("[']","");

            expenseDate = expense.getExpenseDate();
            expenseDate = expenseDate.replaceAll("[']","");

            expenseID = expense.getExpenseID();
            expenseID = expenseID.replaceAll("[']","");

            expenseLocation = expense.getExpenseLocation();
            expenseLocation = expenseLocation.replaceAll("[']","");

            expenseMemo = expense.getExpenseMemo();
            expenseMemo = expenseMemo.replaceAll("[']","");

            expenseName = expense.getExpenseName();
            expenseName = expenseName.replaceAll("[']","");

            expenseTotal = String.valueOf(expense.getExpenseTotal());
            expenseTotal = expenseTotal.replaceAll("[']","");

            mileageFromAddress = expense.getMileageFromAddress();
            mileageFromAddress = mileageFromAddress.replaceAll("[']","");

            mileagePerMileRate = String.valueOf(expense.getMileagePerMileRate());
            mileagePerMileRate = mileagePerMileRate.replaceAll("[']","");

            mileageToAddress = expense.getMileageToAddress();
            mileageToAddress = mileageToAddress.replaceAll("[']","");

            mileageTotalMiles = String.valueOf(expense.getMileageTotalMiles());
            mileageTotalMiles = mileageTotalMiles.replaceAll("[']","");


            string += "{'addressToImage':'"+addressToImage+"',"
                    + "'associatedReport':'"+associatedReport+"',"
                    + "'expenseCategory':'"+expenseCategory+"',"
                    + "'expenseDate':'"+expenseDate+"',"
                    + "'expenseID':'"+expenseID+"',"
                    + "'expenseLocation':'"+expenseLocation+"',"
                    + "'expenseMemo':'"+expenseMemo+"',"
                    + "'expenseName':'"+expenseName+"',"
                    + "'expenseTotal':'"+expenseTotal+"',"
                    + "'mileageFromAddress':'"+mileageFromAddress+"',"
                    + "'mileagePerMileRate':'"+mileagePerMileRate+"',"
                    + "'mileageToAddress':'"+mileageToAddress+"',"
                    + "'mileageTotalMiles':'"+mileageTotalMiles+"'}";

            if(selectedExpenses.indexOf(expense) != selectedExpenses.size()-1){
                string += ",";
            }

        }
        string += "]}"+"}";
        try {

            JSONObject obj = new JSONObject(string);

            Log.d("My App", obj.toString());
            return obj.toString();

        } catch (Throwable tx) {
            Log.e("My App", "Could not parse malformed JSON: \"" + string + "\"");
        }
        return string;




    }

    Thread thread = new Thread(new Runnable() {

        @Override
        public void run() {
            try  {
                OkHttpClient client = new OkHttpClient();



                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, buildJson());
                final Request request = new Request.Builder()
                        .url("https://1248189-sb1.restlets.api.netsuite.com/app/site/hosting/restlet.nl?script=1007&deploy=1")
                        .post(body)
                        .addHeader("authorization", "NLAuth nlauth_account=1248189_SB1, nlauth_email=jclaxton@gnln.com, nlauth_signature=Autoset216, nlauth_role=3")
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "31275d77-7851-0ef9-aa43-f87ad190c00c")
                        .build();

                try (final Response response = client.newCall(request).execute()) {
                    Log.d("My ReQuest:", request.headers().toString());
                    Log.d("My Response:", response.toString());
                    Log.d("MySTRING",buildJson());
                    if(response.code() == 200) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                FirebaseUser fBUser = auth.getCurrentUser();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reportsRef = database.getReference("Reports");
                                selectReport.setStatus("SUBMITTED");
                                reportsRef.child(fBUser.getUid()).child(selectReport.getReportID()).setValue(selectReport);

                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                                getActivity().finish();

                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("My Error:", e.getMessage());
            }
        }
    });
}


