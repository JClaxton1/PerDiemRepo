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
import com.anychart.anychart.Cartesian;
import com.anychart.anychart.CartesianSeriesColumn;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.EnumsAnchor;
import com.anychart.anychart.HoverMode;
import com.anychart.anychart.Pie;
import com.anychart.anychart.Position;
import com.anychart.anychart.TooltipPositionMode;
import com.anychart.anychart.ValueDataEntry;
import com.anychart.anychart.chart.common.Event;
import com.anychart.anychart.chart.common.ListenersInterface;
import com.example.jay.theperdiemapp.R;
import com.example.jay.theperdiemapp.models.Expense;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataPointThree extends Fragment {
        // Store instance variables
//        private String title;
//        private int page;

    TextView tv;
        public static DataPointThree newInstance() { return new DataPointThree();}

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
        Cartesian cartesian = AnyChart.column();
        List<DataEntry> data = getData(selectedExpenses);
        CartesianSeriesColumn column = cartesian.column(data);
        column.getTooltip()
                .setTitleFormat("{%X}")
                .setPosition(Position.CENTER_BOTTOM)
                .setAnchor(EnumsAnchor.CENTER_BOTTOM)
                .setOffsetX(0d)
                .setOffsetY(5d)
                .setFormat("{%Value}{groupsSeparator: } Expenses");
        cartesian.setAnimation(true);
        cartesian.setTitle("Daily Number Of Expenses");
        cartesian.getYScale().setMinimum(0d);
        cartesian.getTooltip().setPositionMode(TooltipPositionMode.POINT);
        cartesian.getInteractivity().setHoverMode(HoverMode.BY_X);
        cartesian.getYAxis().setTitle("No. Of Expenses");
        anyChartView.setChart(cartesian);
        return view;
    }

//    private static double getTotal(ArrayList<Expense> selectedExpenses){
//        double total = 0;
//        for(Expense e: selectedExpenses){
//            total += e.getExpenseTotal();
//        }
//        return total;
//    }

    private static List<DataEntry> getData(ArrayList<Expense> selectedExpenses){
        List<DataEntry> data = new ArrayList<>();
        ArrayList<String> Dates = new ArrayList<>();

        for(Expense expense:selectedExpenses){
            if (!Dates.contains(expense.getExpenseDate())){
                Dates.add(expense.getExpenseDate());
            }
        }




        if(Dates.size()>0){
            Collections.sort(Dates);
            for (String s: Dates){
                int d = 0;
                for (Expense e : selectedExpenses){
                    if (e.getExpenseDate().equals(s)){
                        d += 1;
                    }
                }
                data.add(new ValueDataEntry(s, d));

            }
        }

        return data;
    }







}
