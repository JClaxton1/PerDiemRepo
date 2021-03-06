package com.example.jay.theperdiemapp.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;



import com.example.jay.theperdiemapp.CreateNewActivity;
import com.example.jay.theperdiemapp.DetailsActivity;
import com.example.jay.theperdiemapp.ProfileManagementActivity;
import com.example.jay.theperdiemapp.R;
import com.example.jay.theperdiemapp.adaptors.ReportAdaptor;
import com.example.jay.theperdiemapp.models.Expense;
import com.example.jay.theperdiemapp.models.Report;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import net.mskurt.neveremptylistviewlibrary.NeverEmptyListView;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

import static com.example.jay.theperdiemapp.MainActivity.EXTRA_MESSAGE;

public class ExpenseReportListFragment extends android.support.v4.app.Fragment {



    private final ArrayList<String> reportsArrayListTitles = new ArrayList<>();
    private final ArrayList<Report> reportsArrayList = new ArrayList<>();
    TextView nameTextView;
    TextView emailTextView;
    LinearLayout linearLayout;
    String TAG = "com.example.jay.theperdiemapp.fragments.ExpenseReportListFragment";
    AlertDialog dialog;

    public static ExpenseReportListFragment newInstance() {
        return new ExpenseReportListFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_report_expense, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new SpotsDialog.Builder().setContext(getActivity()).build();
        dialog.setMessage("Loading");
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();



         nameTextView = getActivity().findViewById(R.id.nameTextView);
         emailTextView = getActivity().findViewById(R.id.emailTextView);
         linearLayout = getActivity().findViewById(R.id.linearLayout3);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ProfileManagementActivity.class);
                startActivity(intent);

            }
        });


        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        if (auth.getCurrentUser() != null) {

            // already signed in
            FirebaseUser fBUser = auth.getCurrentUser();
            DatabaseReference reportsEndpoint = mDatabase.child("Reports").child(fBUser.getUid());

            CircleImageView imageView = getActivity().findViewById(R.id.profile_image4);
            Picasso.get().load(fBUser.getPhotoUrl()).into(imageView);


            nameTextView.setText(fBUser.getDisplayName());
            emailTextView.setText(fBUser.getEmail());

            reportsEndpoint.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    reportsArrayListTitles.clear();
                    reportsArrayList.clear();
                    for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                        Report note = noteSnapshot.getValue(Report.class);
                        reportsArrayListTitles.add(Objects.requireNonNull(note).getName());
                        reportsArrayList.add(note);

                    }



                    if(getActivity() != null){
                        ReportAdaptor adapter = new ReportAdaptor(getActivity(),reportsArrayList);
                        NeverEmptyListView neverEmptyListView = getActivity().findViewById(R.id.listview);
                        neverEmptyListView.setAdapter(adapter);

                        dialog.dismiss();
                        neverEmptyListView.setHolderClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(getActivity(), CreateNewActivity.class);
                                String message = "Report";
                                intent.putExtra(EXTRA_MESSAGE, message);
                                startActivity(intent);

                            }
                        });


                        neverEmptyListView.getListview().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                Log.d(TAG,"YOU SELECTED "+reportsArrayListTitles.get(position));

                                Report report = reportsArrayList.get(position);

                                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                                intent.putExtra("Choosen Expense",report.getReportID());
                                String message = "Report";
                                intent.putExtra(EXTRA_MESSAGE, message);
                                //   intent.putStringArrayListExtra(EXTRA_REPORTS_ARRAY_LIST,reportsArrayListTitles);
                                startActivity(intent);






                            }
                        });

                    }





                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
        }


        //Create an empty adapter
        String[] values={};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, android.R.id.text1, values);

        //Set NeverEmptyListView's adapter
        NeverEmptyListView neverEmptyListView = getActivity().findViewById(R.id.listview);
        neverEmptyListView.setAdapter(adapter);
    }
}
