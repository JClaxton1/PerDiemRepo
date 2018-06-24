package com.example.jay.theperdiemapp.adaptors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jay.theperdiemapp.R;
import com.example.jay.theperdiemapp.models.Expense;
import com.example.jay.theperdiemapp.models.Report;

import java.util.ArrayList;

public class ReportAdaptor extends ArrayAdapter<Report> {

    private final Context mContext;
    private ArrayList<Report> reportList;

    public ReportAdaptor(Context context, ArrayList<Report> list) {
        super(context,0,list);
        mContext = context;
        if(reportList != null){
            reportList.clear();
        }
        reportList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.expense_row,parent,false);

        Report report = reportList.get(position);
        String dateString = report.getStartDate() + " - " + report.getEndDate();
        String totalString = String.format("%.2f", report.getTotal());

        TextView name = listItem.findViewById(R.id.name);
        TextView date = listItem.findViewById(R.id.date);
        TextView total = listItem.findViewById(R.id.total);

        name.setText(report.getName());
        date.setText(dateString);
        total.setText("$"+totalString);

        return listItem;
    }
}