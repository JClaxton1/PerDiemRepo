//
//  PageOne.swift
//  Per Diem App
//
//  Created by Jason Claxton on 7/26/18.
//  Copyright © 2018 Jason Claxton. All rights reserved.
//

import Foundation
import UIKit
import Charts

class PageOne: UIViewController {
    
    @IBOutlet weak var barChartView: BarChartView!
    var months: [String]!
    var selectedReport: Report?
    var selectedExpenses: [Expense]?
    
    override func viewDidLoad() {

        
        
        buildChart()
    }


    func buildChart(){
        barChartView.noDataText = "No data available"
        var count = 0.0
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
            for s in dates{
                var d = 0.0
                
                for e in selectedExpenses!{
                    if e.expenseDate.elementsEqual(s){
                        d += e.expenseTotal
                    }
                }
                count += 1.0
                var newEntry = BarChartDataEntry(x: count, y: d)
                entries.append(newEntry)
            }
            
          
        }
        let finalEntries:[BarChartDataEntry]?
        finalEntries = entries
        let dataSet = BarChartDataSet(values:finalEntries, label: "Per Day Spending")
        dataSet.colors = ChartColorTemplates.joyful()
        let data = BarChartData(dataSets: [dataSet])
        barChartView.data = data
        barChartView.chartDescription?.text = ""

        barChartView.notifyDataSetChanged()
    }
    
}
