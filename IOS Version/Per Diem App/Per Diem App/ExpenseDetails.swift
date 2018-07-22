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
        nameField.text = name
        memoField.text = memo
        dateField.text = date
        totalCostField.text = cost
        configPicker()
    }
    override func viewWillAppear(_ animated: Bool) {
        
    }
    
    func configPicker() {
        categoryPicker.pickerType = .StringPicker
        
        let stringData = ["AVFoundation","Accelerate","AddressBook","AddressBookUI","AssetsLibrary"]
        categoryPicker.stringPickerData = stringData
        categoryPicker.pickerRow.font = UIFont(name: "American Typewriter", size: 30)
        
        categoryPicker.toolbar.barTintColor = .darkGray
        categoryPicker.toolbar.tintColor = .black
        
        categoryPicker.stringDidChange = { index in
            
            print("selectedString ", stringData[index])
            
            
        }
    }
}
