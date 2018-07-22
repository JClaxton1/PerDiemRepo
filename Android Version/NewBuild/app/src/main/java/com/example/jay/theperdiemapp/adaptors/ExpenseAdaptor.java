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
import java.util.Collections;
import java.util.Comparator;

public class ExpenseAdaptor extends ArrayAdapter<Expense> {

    private final Context mContext;
    private ArrayList<Expense> expenses;


    public ExpenseAdaptor(Context context, ArrayList<Expense> list) {
        super(context,0,list);
        mContext = context;
        if(expenses != null){
            expenses.clear();
        }

        expenses = list;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.expense_row1,parent,false);

        Expense expense = expenses.get(position);
        String totalString = String.format("%.2f", expense.getExpenseTotal());

        TextView name = listItem.findViewById(R.id.name);
        TextView date = listItem.findViewById(R.id.date);
        TextView total = listItem.findViewById(R.id.total);

        String title = removeLastChar(expense.getAssociatedReport());

        name.setText(expense.getExpenseName() + " - ("+title+")");
        date.setText(expense.getExpenseDate());
        total.setText("$"+totalString);

        return listItem;
    }


    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 10);
    }



}