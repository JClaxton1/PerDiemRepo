//
//  PageThree.swift
//  Per Diem App
//
//  Created by Jason Claxton on 7/27/18.
//  Copyright © 2018 Jason Claxton. All rights reserved.
//

import Foundation
import UIKit
import Charts

class PageThree: UIViewController {
    

    @IBOutlet weak var barChartView: BarChartView!
    var months: [String]!
    var selectedReport: Report?
    var selectedExpenses: [Expense]?
    
    override func viewDidLoad() {
        
        
        buildChart()
    }
    
    
    func buildChart(){
        barChartView.noDataText = "No data available"
        var dates = [""]
        var entries = [BarChartDataEntry.init(x: 0.0, y: 0.0)]
        dates.removeAll()
        for i in selectedExpenses! {
            
            if (dates.contains(i.expenseDate)){
                
            }else{
                dates.append(i.expenseDate)
            }
            
            
        }
        
        if (dates.count > 0){
            dates.sort()
            var count = 0.0
            for s in dates{
                var d = 0.0
                
                for e in selectedExpenses!{
                    if e.expenseDate.elementsEqual(s){
                        d += 1
                    }
                }
                count += 1
                var newEntry = BarChartDataEntry(x: count, y: d)
                entries.append(newEntry)
            }
            
            
        }
        let finalEntries:[BarChartDataEntry]?
        finalEntries = entries
        let dataSet = BarChartDataSet(values:finalEntries, label: "Per Day Expenses")
        let data = BarChartData(dataSets: [dataSet])
        barChartView.data = data
        barChartView.chartDescription?.text = ""
        
        //        let entry1 = BarChartDataEntry(x: 1, y: 120.0)
        //        let entry2 = BarChartDataEntry(x: 2, y: 4.0)
        //        let entry3 = BarChartDataEntry(x: 3, y: 18.0)
        //        let entry4 = BarChartDataEntry(x: 4, y: 18.0)
        //        let entry5 = BarChartDataEntry(x: 5, y: 18.0)
        //        let entry6 = BarChartDataEntry(x: 6, y: 18.0)
        //        let entry7 = BarChartDataEntry(x: 7, y: 18.0)
        //        let entry8 = BarChartDataEntry(x: 8, y: 18.0)
        //        let entry9 = BarChartDataEntry(x: 9, y: 18.0)
        //        let entry10 = BarChartDataEntry(x: 10, y: 18.0)
        //        let entry11 = BarChartDataEntry(x: 11, y: 18.0)
        //        let entry12 = BarChartDataEntry(x: 12, y: 18.0)
        //
        //        let data = BarChartData(dataSets: [dataSet])
        //        barChartView.data = data
        //        barChartView.chartDescription?.text = ""
        
        //This must stay at end of function
        barChartView.notifyDataSetChanged()
    }
    
}

