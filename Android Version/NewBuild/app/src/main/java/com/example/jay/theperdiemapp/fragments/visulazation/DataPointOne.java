package com.example.jay.theperdiemapp.fragments.visulazation;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.EnumsAlign;
import com.anychart.anychart.LegendLayout;
import com.anychart.anychart.Pie;
import com.anychart.anychart.ValueDataEntry;
import com.anychart.anychart.chart.common.Event;
import com.anychart.anychart.chart.common.ListenersInterface;
import com.example.jay.theperdiemapp.R;
import com.example.jay.theperdiemapp.fragments.DetailsExpenseFragment;
import com.example.jay.theperdiemapp.models.Expense;

import java.util.ArrayList;
import java.util.List;

public class DataPointOne extends Fragment {

    public static DataPointOne newInstance() { return new DataPointOne();}

    TextView tv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.point_data_layout, container, false);

    }

    // Store instance variables based on arguments passed
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

    @Override
    public void onStart() {
        super.onStart();


    }


    // Inflate the view for the fragment based on layout XML

    public static View getView(final Context context, ViewGroup collection, ArrayList<Expense> selectedExpenses) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.point_data_layout, collection, false);
        AnyChartView anyChartView = view.findViewById(R.id.any_chart_view);
        Pie pie = AnyChart.pie();
        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(context, event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });

        List<DataEntry> data = getData(selectedExpenses);
        pie.setData(data);
        pie.setTitle("Category Spending");
        pie.getLabels().setPosition("inside");
        anyChartView.setChart(pie);



        return view;
    }

//    private static double getTotal(ArrayList<Expense> selectedExpenses){
//            double total = 0;
//            for(Expense e: selectedExpenses){
//                total += e.getExpenseTotal();
//            }
//           return total;
//    }

    private static List<DataEntry> getData(ArrayList<Expense> selectedExpenses){
        List<DataEntry> data = new ArrayList<>();
        ArrayList<String> Categories = new ArrayList<>();

        for(Expense expense:selectedExpenses){
            if (!Categories.contains(expense.getExpenseCategory())){
                Categories.add(expense.getExpenseCategory());
            }
        }


        if(Categories.size()>0){
            for (String s: Categories){
                Double d = 0.0;
                for (Expense e : selectedExpenses){
                    if (e.getExpenseCategory().equals(s)){
                        d += e.getExpenseTotal();
                    }
                }
                data.add(new ValueDataEntry(s, d));

            }
        }

        return data;
    }

}
