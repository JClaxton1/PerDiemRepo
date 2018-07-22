package com.example.jay.theperdiemapp.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.jay.theperdiemapp.CreateNewActivity;
import com.example.jay.theperdiemapp.DetailsActivity;
import com.example.jay.theperdiemapp.ProfileManagementActivity;
import com.example.jay.theperdiemapp.R;
import com.example.jay.theperdiemapp.adaptors.ExpenseAdaptor;
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
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.squareup.picasso.Picasso;

import net.mskurt.neveremptylistviewlibrary.NeverEmptyListView;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.jay.theperdiemapp.MainActivity.EXTRA_MESSAGE;

//TODO EXPENSE LIST FRAGMENT
public class ExpenseListFragment extends android.support.v4.app.Fragment{

    public static ExpenseListFragment newInstance() {
        return new ExpenseListFragment();
    }

    private final ArrayList<String> EXPENSEArrayListTitles = new ArrayList<>();
    private final ArrayList<Expense> EXPENSEArrayList = new ArrayList<>();
    private final ArrayList<Report> REPORTArrayList = new ArrayList<>();
    LinearLayout linearLayout;

    private static final String TAG = "ExpenseListFragment.TAG";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_expense, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        TextView nameTextView = getActivity().findViewById(R.id.nameTextView1);
        TextView emailTextView = getActivity().findViewById(R.id.emailTextView1);
        linearLayout = getActivity().findViewById(R.id.linearLayout4);
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
            DatabaseReference expenseEndpoint = mDatabase.child("Expenses").child(fBUser.getUid());
            DatabaseReference reportEndpoint = mDatabase.child("Reports").child(fBUser.getUid());

            CircleImageView imageView = getActivity().findViewById(R.id.profile_image1);
            Picasso.get().load(fBUser.getPhotoUrl()).into(imageView);

            nameTextView.setText(fBUser.getDisplayName());
            emailTextView.setText(fBUser.getEmail());

            reportEndpoint.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    REPORTArrayList.clear();
                    for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                        Report note = noteSnapshot.getValue(Report.class);

                        REPORTArrayList.add(Objects.requireNonNull(note));

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            expenseEndpoint.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    EXPENSEArrayListTitles.clear();
                    EXPENSEArrayList.clear();
                    for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                        Expense note = noteSnapshot.getValue(Expense.class);
                        EXPENSEArrayListTitles.add(Objects.requireNonNull(note).getExpenseName());
                        EXPENSEArrayList.add(Objects.requireNonNull(note));

                    }


                    if(getActivity() != null){
                        ExpenseAdaptor adapter = new ExpenseAdaptor(getActivity(),EXPENSEArrayList);
                        NeverEmptyListView neverEmptyListView = getActivity().findViewById(R.id.listview2);
                        neverEmptyListView.setAdapter(adapter);

                        neverEmptyListView.setHolderClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (!REPORTArrayList.isEmpty()) {
                                    Intent intent = new Intent(getActivity(), CreateNewActivity.class);

                                    String message = "Expense";
                                    intent.putExtra(EXTRA_MESSAGE, message);
                                    //   intent.putStringArrayListExtra(EXTRA_REPORTS_ARRAY_LIST,reportsArrayListTitles);
                                    startActivity(intent);
                                }else{
                                    AlertNoReportMade();
                                }

                            }
                        });


                        neverEmptyListView.getListview().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                                Expense expense = EXPENSEArrayList.get(position);
                                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                                Bundle b = buildBundle(expense);
                                intent.putExtra("Choosen Expense",b);
                                String message = "Expense";
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, android.R.id.text1, values);

        //Set NeverEmptyListView's adapter
        NeverEmptyListView neverEmptyListView = getActivity().findViewById(R.id.listview);
        neverEmptyListView.setAdapter(adapter);

    }

    private void AlertNoReportMade(){
        new FancyAlertDialog.Builder(getActivity())
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
                        Intent intent = new Intent(getActivity(), CreateNewActivity.class);
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

    private Bundle buildBundle(Expense expense){

        Bundle b = new Bundle();
        b.putString("expenseID", expense.getExpenseID());
        b.putString("expenseName", expense.getExpenseName());
        b.putDouble("expenseTotal", expense.getExpenseTotal());
        b.putString("expenseDate", expense.getExpenseDate());
        b.putString("expenseLocation", expense.getExpenseLocation());
        b.putString("expenseMemo", expense.getExpenseMemo());
        b.putString("expenseCategory", expense.getExpenseCategory());
        b.putString("addressToImage", expense.getAddressToImage());
        b.putString("associatedReport", expense.getAssociatedReport());
        b.putString("mileageFromAddress", expense.getMileageFromAddress());
        b.putString("mileageToAddress", expense.getMileageToAddress());
        b.putDouble("mileagePerMileRate", expense.getMileagePerMileRate());
        b.putDouble("mileageTotalMiles", expense.getMileageTotalMiles());


        return b;
    }



}
