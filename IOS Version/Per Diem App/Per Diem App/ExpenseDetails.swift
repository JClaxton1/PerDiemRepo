//
//  ExpenseDetails.swift
//  Per Diem App
//
//  Created by Jason Claxton on 7/9/18.
//  Copyright © 2018 Jason Claxton. All rights reserved.
//

import Foundation
import UIKit
import AAPickerView
import Floaty
import ImageLoader


class ExpenseDetails: UIViewController {

    var name = ""
    var memo = ""
    var date = ""
    var cost = ""
    var category = ""
    var report = ""
    @IBOutlet weak var nameField: UITextField!
    @IBOutlet weak var memoField: UITextField!
    @IBOutlet weak var dateField: UITextField!
    @IBOutlet weak var totalCostField: UITextField!
    @IBOutlet weak var categoryPicker: AAPickerView!
    @IBOutlet weak var reportPicker: AAPickerView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        Floaty.global.hide()
        nameField.text = name
        memoField.text = memo
        dateField.text = date
        totalCostField.text = cost
        categoryPicker.text = category
        reportPicker.text = report
       
        reportPicker.isEnabled = false
        categoryPicker.isEnabled = false
        //configPicker()
    }
    override func viewWillAppear(_ animated: Bool) {
    }
    
    @IBAction func deleteButton(_ sender: Any) {
    }
    
    
    @IBAction func updateButton(_ sender: Any) {
    }
    
    
    

}
