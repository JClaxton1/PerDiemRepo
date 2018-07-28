//
//  PageTwo.swift
//  Per Diem App
//
//  Created by Jason Claxton on 7/27/18.
//  Copyright © 2018 Jason Claxton. All rights reserved.
//

import Foundation
import UIKit
import Charts

class PageTwo: UIViewController {
    
    var months: [String]!
    var selectedReport: Report?
    var selectedExpenses: [Expense]?
    
    @IBOutlet weak var pieChartView: PieChartView!
    
    override func viewDidLoad() {
        
        
        buildChart()
    }
    
    

    func buildChart(){
        pieChartView.noDataText = "No data available"
        var category = [""]
        var entries = [PieChartDataEntry.init(value: 0.0)]
        category.removeAll()
        for i in selectedExpenses! {
            
            if (category.contains(i.expenseCategory)){
                
            }else{
                category.append(i.expenseCategory)
            }
            
            
        }
        
        if (category.count > 0){
            entries.removeAll()
            category.sort()
            var count = 0.0
            
            for s in category{
                var d = 0.0
                for e in selectedExpenses!{
                    if e.expenseCategory.elementsEqual(s){
                        d += e.expenseTotal
                    }
                }
                count += 1
                let entry = PieChartDataEntry(value: d, label: s)
                entries.append(entry)
            }
            
            
        }
        let finalEntries:[PieChartDataEntry]?
        finalEntries = entries
        let dataSet = PieChartDataSet(values:finalEntries, label: "Category Spending")
        dataSet.colors = ChartColorTemplates.joyful()
        let data = PieChartData(dataSets: [dataSet])
        pieChartView.data = data
        
        pieChartView.chartDescription?.text = ""
        pieChartView.legend.font = UIFont(name: "Futura", size: 10)!
        
        pieChartView.chartDescription?.font = UIFont(name: "Futura", size: 12)!
        pieChartView.chartDescription?.textColor = #colorLiteral(red: 0.4509803922, green: 0.6, blue: 0.5294117647, alpha: 1)
        pieChartView.chartDescription?.xOffset = pieChartView.frame.width + 30
        pieChartView.chartDescription?.yOffset = pieChartView.frame.height * (2/3)
        pieChartView.chartDescription?.textAlign = NSTextAlignment.left
        pieChartView.backgroundColor = #colorLiteral(red: 0.4509803922, green: 0.6, blue: 0.5294117647, alpha: 1)
        pieChartView.holeColor = UIColor.clear
        pieChartView.chartDescription?.textColor = UIColor.white
        pieChartView.legend.textColor = UIColor.white

        pieChartView.notifyDataSetChanged()
    }
    
}
