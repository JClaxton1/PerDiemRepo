package com.example.jay.theperdiemapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.DatePicker;

import com.example.jay.theperdiemapp.fragments.NewCameraFragment;
import com.example.jay.theperdiemapp.fragments.NewExpenseFragment;
import com.example.jay.theperdiemapp.fragments.NewMileageFragment;
import com.example.jay.theperdiemapp.fragments.NewReportFragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CreateNewActivity extends Activity  {
    Calendar cal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_create);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        switch (message){
            case "Report":
                NewReportFragment report = NewReportFragment.newInstance();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, report, "report")
                        .commit();
                break;
            case "Expense":
                NewExpenseFragment expense = NewExpenseFragment.newInstance();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, expense, "expense")
                        .commit();
                break;
            case "Camera":
                NewCameraFragment camera = NewCameraFragment.newInstance();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, camera, "camera")
                        .commit();
                break;

            case "Mileage":
                NewMileageFragment miles = NewMileageFragment.newInstance();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, miles, "miles")
                        .commit();
                break;

            default:
                loadFragment(new NewReportFragment());
                break;

        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }


}
