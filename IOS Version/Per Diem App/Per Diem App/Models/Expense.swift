//
//  Expense.swift
//  Per Diem App
//
//  Created by Jason Claxton on 7/7/18.
//  Copyright © 2018 Jason Claxton. All rights reserved.
//

import Foundation
import UIKit

class Expense {
    
    
    var  expenseID:String
    var  expenseName:String
    var  expenseTotal:Double
    var  expenseDate:String
    var  expenseLocation:String
    var  expenseMemo:String
    var  expenseCategory:String
    var  addressToImage:String
    var  associatedReport:String
    var  mileageFromAddress:String
    var  mileageToAddress:String
    var  mileagePerMileRate:Double
    var  mileageTotalMiles:Double
    
    
    init(ExpenseID:String?,ExpenseName:String?,ExpenseTotal:Double?,ExpenseDate:String?,ExpenseLocation:String?,ExpenseMemo:String?,ExpenseCategory:String?,AddressToImage:String?,AssociatedReport:String?,MileageFromAddress:String?,MileageToAddress:String?,MileagePerMileRate:Double?,MileageTotalMiles:Double?) {
        
        self.expenseID = ExpenseID  ?? ""
        self.expenseName = ExpenseName  ?? ""
        self.expenseTotal = ExpenseTotal  ?? 0.0
        self.expenseDate = ExpenseDate ?? ""
        self.expenseLocation = ExpenseLocation ?? ""
        self.expenseMemo = ExpenseMemo ?? ""
        self.expenseCategory = ExpenseCategory ?? ""
        self.addressToImage = AddressToImage ?? ""
        self.associatedReport = AssociatedReport ?? ""
        self.mileageFromAddress = MileageFromAddress ?? ""
        self.mileageToAddress = MileageToAddress ?? ""
        self.mileagePerMileRate = MileagePerMileRate ?? 0.0
        self.mileageTotalMiles = MileageTotalMiles ?? 0.0
        
    }
    
    
}
