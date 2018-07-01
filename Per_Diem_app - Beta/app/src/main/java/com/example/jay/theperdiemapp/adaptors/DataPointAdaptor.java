package com.example.jay.theperdiemapp.adaptors;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.example.jay.theperdiemapp.fragments.visulazation.DataPointOne;
import com.example.jay.theperdiemapp.fragments.visulazation.DataPointThree;
import com.example.jay.theperdiemapp.fragments.visulazation.DataPointTwo;
import com.example.jay.theperdiemapp.models.Expense;
import com.example.jay.theperdiemapp.models.Report;
import java.util.ArrayList;

public class DataPointAdaptor extends PagerAdapter {

    private Context context;
    private ArrayList<Report> dataAllReports;
    private ArrayList<Expense> dataAllExpenses;
    private ArrayList<Expense> dataSelectedExpenses;

    public DataPointAdaptor(Context context) {
        super();
        this.context = context;
    }

    public DataPointAdaptor(Context context, ArrayList<Report> allReports, ArrayList<Expense> allExpenses, ArrayList<Expense> selectedExpenses) {
        super();
        this.context = context;
        this.dataAllReports = allReports;
        this.dataAllExpenses = allExpenses;
        this.dataSelectedExpenses = selectedExpenses;
    }


    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
      //  LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        switch (position){
            case 0:

                view = DataPointTwo.getView(context, collection, dataSelectedExpenses);
                break;
            case 1:
                view = DataPointOne.getView(context, collection, dataSelectedExpenses);
                break;
            case 2:
                view = DataPointThree.getView(context, collection, dataSelectedExpenses);
                break;
        }

        collection.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }
}
