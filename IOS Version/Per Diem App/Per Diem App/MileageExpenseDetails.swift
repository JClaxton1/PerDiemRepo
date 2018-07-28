//
//  MileageExpenseDetails.swift
//  Per Diem App
//
//  Created by Jason Claxton on 7/23/18.
//  Copyright © 2018 Jason Claxton. All rights reserved.
//

import Foundation
import UIKit
import Floaty
import AAPickerView
import GooglePlaces
import GooglePlacePicker
import Firebase
import FirebaseDatabase
import PDTSimpleCalendar
import FormValidatorSwift



class MileageExpenseDetails: UIViewController, UITextFieldDelegate,GMSPlacePickerViewControllerDelegate, PDTSimpleCalendarViewDelegate{
    
    
    
    
    var toAddress = ""
    var fromAddress = ""
    var date = ""
    var perMile = ""
    var totalMiles = ""
    var totalCost = ""
    var category = ""
    var report = ""
    var expenseID = ""
    
    @IBOutlet weak var toAddressTextFeild: UITextField!
    @IBOutlet weak var fromAddressTextFeild: UITextField!
    @IBOutlet weak var dateTextFeild: UITextField!
    @IBOutlet weak var perMileTextFeild: UITextField!
    @IBOutlet weak var totalMilesTextFeild: UITextField!
    @IBOutlet weak var totalCostTextFeild: UITextField!
    @IBOutlet weak var catPicker: AAPickerView!
    @IBOutlet weak var reportPicker: AAPickerView!


    var ref: DatabaseReference!
    var reports:[Report] = []
    var isKeyboardAppear = false
    
    var text = ""
    var place1: CLLocationCoordinate2D?
    var place2: CLLocationCoordinate2D?
    var mileageDistance: Double?
    var perMileRate: Double?
    var mileageTotalCost: Double?
    
    
    func fillForm(){
        toAddressTextFeild.text = toAddress
       fromAddressTextFeild.text = fromAddress
        dateTextFeild.text = date
        perMileTextFeild.text = perMile
        totalMilesTextFeild.text = totalMiles
        totalMilesTextFeild.isEnabled = false
       totalCostTextFeild.text = totalCost
        totalCostTextFeild.isEnabled = false
        catPicker.text = category
        catPicker.isEnabled = false
        reportPicker.text = report
        reportPicker.isEnabled = false
    }
    
    
    
    @IBOutlet weak var headerview: UIView!
    override func viewDidLoad() {
        super.viewDidLoad()
        fillForm()
        toAddressTextFeild.delegate = self
        fromAddressTextFeild.delegate = self
        dateTextFeild.delegate = self
        perMileTextFeild.text = "1"
        reportPicker.isEnabled = false
        catPicker.isEnabled = false
        
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name:NSNotification.Name.UIKeyboardWillShow, object: nil);
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name:NSNotification.Name.UIKeyboardWillHide, object: nil);
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        self.title = "New Mileage"
        Floaty.global.hide()
        getData()
       // setupHeaderView()
    }
    
    
    
    
    @objc func keyboardWillShow(sender: NSNotification) {
        if !isKeyboardAppear {
            if let keyboardSize = (sender.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
                if self.view.frame.origin.y == 0{
                    self.view.frame.origin.y -= keyboardSize.height
                }
            }
            isKeyboardAppear = true
        }
    }
    @objc func keyboardWillHide(sender: NSNotification) {
        if isKeyboardAppear {
            if let keyboardSize = (sender.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
                if self.view.frame.origin.y != 0{
                    self.view.frame.origin.y += keyboardSize.height
                }
            }
            isKeyboardAppear = false
        }
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        if textField == toAddressTextFeild{
            text = "toAddressTextFeild"
            let config = GMSPlacePickerConfig(viewport: nil)
            let placePicker = GMSPlacePickerViewController(config: config)
            placePicker.delegate = self
            placePicker.modalPresentationStyle = .popover
            present(placePicker, animated: true, completion: nil)
        }
        
        if textField == fromAddressTextFeild{
            text = "fromAddressTextFeild"
            let config = GMSPlacePickerConfig(viewport: nil)
            let placePicker = GMSPlacePickerViewController(config: config)
            placePicker.delegate = self
            placePicker.modalPresentationStyle = .popover
            present(placePicker, animated: true, completion: nil)
        }
        
        if textField == dateTextFeild{
            let  calendarPicker = PDTSimpleCalendarViewController()
            calendarPicker.delegate = self
            present(calendarPicker, animated: true, completion: nil)
        }
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.view.endEditing(true)
        return false
    }
    
    @IBAction func save(_ sender: Any) {
        validateFields()
    }
    
    func setupHeaderView(){
        headerview.layer.masksToBounds = false
        headerview.layer.shadowColor = UIColor.black.cgColor
        headerview.layer.shadowOpacity = 0.5
        headerview.layer.shadowOffset = CGSize(width: -1, height: 1)
        headerview.layer.shadowRadius = 1.25
        
        headerview.layer.shadowPath = UIBezierPath(rect: headerview.bounds).cgPath
        headerview.layer.shouldRasterize = true
        headerview.layer.rasterizationScale =  headerview.layer.contentsScale;
    }
    
    func checkMileage(){
        
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.maximumFractionDigits = 2
        formatter.roundingMode = .up
        
        if place1 != nil && place2 != nil{
            
            let rate = perMileTextFeild.text
            perMileRate = Double(rate!)
            
            let coordinate0 = CLLocation(latitude: (place1?.latitude)!, longitude: (place1?.longitude)!)
            let coordinate1 = CLLocation(latitude: (place2?.latitude)!, longitude: (place2?.longitude)!)
            let distanceInMeters = coordinate0.distance(from: coordinate1)
            
            _ = Double(distanceInMeters.description)!
            let tempDouble = distanceInMeters * 0.00062137
            mileageDistance = tempDouble
            
            mileageTotalCost = mileageDistance! * perMileRate!
            
            
            totalMilesTextFeild.text = tempDouble.description
            totalCostTextFeild.text = "$\(String(describing: formatter.string(from: mileageTotalCost! as NSNumber)!))"
        }
    }
    
    
    func placePicker(_ viewController: GMSPlacePickerViewController, didPick place: GMSPlace) {
        viewController.dismiss(animated: true, completion: nil)
        
        if text == "toAddressTextFeild" {
            toAddressTextFeild.text = "\(place.name)  \(place.formattedAddress!)"
            place1 = place.coordinate
            checkMileage()
        } else if text == "fromAddressTextFeild" {
            fromAddressTextFeild.text = "\(place.name)  \(place.formattedAddress!)"
            place2 = place.coordinate
            checkMileage()
        }
        
        
    }
    func placePickerDidCancel(_ viewController: GMSPlacePickerViewController) {
        viewController.dismiss(animated: true, completion: nil)
        
        print("No place selected")
    }
    
    //Configuring Picker
    func configPicker() {
        catPicker.pickerType = .StringPicker
        
        let stringData = [ "Computer Related Equiptment", "Education, Research, and Conferences",
                           "Employee Incentives and Gifts","Food/Snacks/Beverage - Not Show/Travel Related","GST/HST Tax",
                           "Information Services","Lodging","Marketing Expense","Meals-Travel", "Mileage","Office Supplies","Research and Development",
                           "Show Lodging","Show Meals","Show Transportation","Taxes and Licenses","Transportation - Non Show","Travel - Non Show","Travel - Sales",
                           "Travel Advance"]
        catPicker.stringPickerData = stringData
        catPicker.pickerRow.font = UIFont(name: "American Typewriter", size: 30)
        
        catPicker.toolbar.barTintColor = .darkGray
        catPicker.toolbar.tintColor = .black
        
        catPicker.stringDidChange = { index in
            
            print("selectedString ", stringData[index])
            
        }
        var stringReportData:[String] = []
        reportPicker.pickerType = .StringPicker
        stringReportData.removeAll()
        for i in reports{
            stringReportData.append(i.reportID)
        }
        
        reportPicker.stringPickerData = stringReportData
        reportPicker.pickerRow.font = UIFont(name: "American Typewriter", size: 30)
        
        reportPicker.stringDidChange = { index in
            print("selectedString ", stringReportData[index])
            
        }
        
        
    }
    
    //Get Report Data From Firebase To Load Picker
    func getData(){
        let userID = Auth.auth().currentUser?.uid
        ref = Database.database().reference()
        ref.child("Reports").child(userID!).observe(.value, with: { (snapshot) in
            // Get user value
            let value = snapshot.value as? NSDictionary
            self.reports.removeAll()
            if value != nil{
                let itemsArray: [NSObject] = value?.allValues as! [NSObject]
                for (item) in itemsArray {
                    let rName = item.value(forKey: "name")! as! String
                    let rStartDate = item.value(forKey: "startDate")! as! String
                    let rEndDate = item.value(forKey: "endDate")! as! String
                    let rMemo = item.value(forKey: "memo")! as! String
                    let rReportID = item.value(forKey: "reportID")! as! String
                    let rTotal = item.value(forKey: "total")! as! Double
                    let rStatus = item.value(forKey: "status")! as! String
                    self.reports.append(Report(Name: rName,StartDate: rStartDate,EndDate: rEndDate,Memo: rMemo,ReportID: rReportID,Status: rStatus,Total: rTotal))
                }
                self.configPicker()
            }
            
        }) { (error) in
            print(error.localizedDescription)
        }
    }
    func simpleCalendarViewController(_ controller: PDTSimpleCalendarViewController!, didSelect date: Date!) {
        
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/yyyy"
        let todaysDate = dateFormatter.string(from: date)
        
        self.dateTextFeild.text = todaysDate
        controller.dismiss(animated: true, completion: nil)
    }
    
    func validateFields (){
        let toAddress = toAddressTextFeild.text
        let fromAddress = fromAddressTextFeild.text
        let expenseDate = dateTextFeild.text
        let perMileRate = perMileTextFeild.text
        let expenseDistance = totalMilesTextFeild.text
        let expensePerMile = perMileTextFeild.text
        let expenseTotalCost = mileageTotalCost

        
        
        if let cost = Double(totalCostTextFeild.text!) {
            let expenseTotalCost = cost
        }
        
        
        let cat = catPicker.text
        let reportID = reportPicker.text
        
        var alertTitle: String
        var alertMessage: String
        var validated = true
        
        
        
        let validator = PresentValidator()
        var result = validator.checkConditions(toAddress)
        if result != nil {
            alertTitle = NSLocalizedString("Error", comment: "")
            alertMessage = NSLocalizedString("Please Select A To Address", comment: "")
            let doneAction = UIAlertAction(title: NSLocalizedString("Done", comment: ""), style: .default, handler: nil)
            let alertController = UIAlertController(title: alertTitle, message: alertMessage, preferredStyle: .alert)
            alertController.addAction(doneAction)
            present(alertController, animated: true, completion: nil)
            validated = false
        }
        
        result = validator.checkConditions(fromAddress)
        if result != nil {
            alertTitle = NSLocalizedString("Error", comment: "")
            alertMessage = NSLocalizedString("Please Select A From Address", comment: "")
            let doneAction = UIAlertAction(title: NSLocalizedString("Done", comment: ""), style: .default, handler: nil)
            let alertController = UIAlertController(title: alertTitle, message: alertMessage, preferredStyle: .alert)
            alertController.addAction(doneAction)
            present(alertController, animated: true, completion: nil)
            validated = false
        }
        
        result = validator.checkConditions(mileageTotalCost?.description)
        if result != nil {
            alertTitle = NSLocalizedString("Error", comment: "")
            alertMessage = NSLocalizedString("Please Select To And From Location", comment: "")
            let doneAction = UIAlertAction(title: NSLocalizedString("Done", comment: ""), style: .default, handler: nil)
            let alertController = UIAlertController(title: alertTitle, message: alertMessage, preferredStyle: .alert)
            alertController.addAction(doneAction)
            present(alertController, animated: true, completion: nil)
            validated = false
        }
        
        result = validator.checkConditions(expenseDate)
        if result != nil {
            alertTitle = NSLocalizedString("Error", comment: "")
            alertMessage = NSLocalizedString("Please Select A Date", comment: "")
            let doneAction = UIAlertAction(title: NSLocalizedString("Done", comment: ""), style: .default, handler: nil)
            let alertController = UIAlertController(title: alertTitle, message: alertMessage, preferredStyle: .alert)
            alertController.addAction(doneAction)
            present(alertController, animated: true, completion: nil)
            validated = false
        }
        
        if validated == true{
            let dateFormatter : DateFormatter = DateFormatter()
            dateFormatter.dateFormat = "yyyyMMddHHmmss"
            let date = Date()
            let dateString = dateFormatter.string(from: date)
            let selectedReport = reports.first{$0.reportID == reportID}
            
            let ID = toAddress! + " \(dateString)"
            
            
            let expense = Expense(ExpenseID: expenseID, ExpenseName: toAddress, ExpenseTotal: expenseTotalCost, ExpenseDate: expenseDate, ExpenseLocation: fromAddress, ExpenseMemo: "None Provided Mileage", ExpenseCategory: cat, AddressToImage: "None Provided", AssociatedReport: selectedReport?.reportID, MileageFromAddress: fromAddress, MileageToAddress: expenseDistance?.description, MileagePerMileRate: 1.00, MileageTotalMiles: expenseTotalCost)
            
            var ref: DatabaseReference!
            ref = Database.database().reference()
            
            let currenttotal = selectedReport?.total
            let newTotal = currenttotal! - expenseTotalCost!
            selectedReport?.total = newTotal
            
            
            ref.child("Reports").child((Auth.auth().currentUser?.uid)!).child((selectedReport?.reportID)!).setValue(["endDate": selectedReport?.endDate,
                                                                                                                     "memo":selectedReport?.memo,
                                                                                                                     "name": selectedReport?.name,
                                                                                                                     "reportID":selectedReport?.reportID,
                                                                                                                     "startDate": selectedReport?.startDate,
                                                                                                                     "status":selectedReport?.status,
                                                                                                                     "total":selectedReport?.total, ])
            
            ref.child("Expenses").child((Auth.auth().currentUser?.uid)!).child(ID).setValue(["addressToImage": expense.addressToImage,
                                                                                             "associatedReport":expense.associatedReport,
                                                                                             "expenseCategory": expense.expenseCategory,
                                                                                             "expenseDate": expense.expenseDate,
                                                                                             "expenseID": expense.expenseID,
                                                                                             "expenseLocation": expense.expenseLocation,
                                                                                             "expenseMemo": expense.expenseMemo,
                                                                                             "expenseName": "Mileage: \(expense.expenseName)",
                "expenseTotal": expense.expenseTotal,
                "mileageFromAddress": expense.mileageFromAddress,
                "mileagePerMileRate": expense.mileagePerMileRate,
                "mileageToAddress": expense.mileageToAddress,
                "mileageTotalMiles": expense.mileageTotalMiles, ])
            _ = navigationController?.popToRootViewController(animated: true)
            
        }
        
        
    }
    
    
    func prepdeleteFields (){
        let toAddress = toAddressTextFeild.text
        let fromAddress = fromAddressTextFeild.text
        let expenseDate = dateTextFeild.text
        let perMileRate = perMileTextFeild.text
        let expenseDistance = totalMilesTextFeild.text
        let expensePerMile = 0.0
        var expenseTotalCost = 0.0
        if let total = Double(totalCostTextFeild.text!){
           expenseTotalCost = total
            
        }
        let cat = catPicker.text
        let reportID = reportPicker.text
        
        var alertTitle: String
        var alertMessage: String
        var validated = true
        
        
        
        let validator = PresentValidator()
        var result = validator.checkConditions(toAddress)
        if result != nil {
            alertTitle = NSLocalizedString("Error", comment: "")
            alertMessage = NSLocalizedString("Please Select A To Address", comment: "")
            let doneAction = UIAlertAction(title: NSLocalizedString("Done", comment: ""), style: .default, handler: nil)
            let alertController = UIAlertController(title: alertTitle, message: alertMessage, preferredStyle: .alert)
            alertController.addAction(doneAction)
            present(alertController, animated: true, completion: nil)
            validated = false
        }
        
        result = validator.checkConditions(fromAddress)
        if result != nil {
            alertTitle = NSLocalizedString("Error", comment: "")
            alertMessage = NSLocalizedString("Please Select A From Address", comment: "")
            let doneAction = UIAlertAction(title: NSLocalizedString("Done", comment: ""), style: .default, handler: nil)
            let alertController = UIAlertController(title: alertTitle, message: alertMessage, preferredStyle: .alert)
            alertController.addAction(doneAction)
            present(alertController, animated: true, completion: nil)
            validated = false
        }
        
        result = validator.checkConditions(expenseTotalCost.description)
        if result != nil {
            alertTitle = NSLocalizedString("Error", comment: "")
            alertMessage = NSLocalizedString("Please Select To And From Location", comment: "")
            let doneAction = UIAlertAction(title: NSLocalizedString("Done", comment: ""), style: .default, handler: nil)
            let alertController = UIAlertController(title: alertTitle, message: alertMessage, preferredStyle: .alert)
            alertController.addAction(doneAction)
            present(alertController, animated: true, completion: nil)
            validated = false
        }
        
        result = validator.checkConditions(expenseDate)
        if result != nil {
            alertTitle = NSLocalizedString("Error", comment: "")
            alertMessage = NSLocalizedString("Please Select A Date", comment: "")
            let doneAction = UIAlertAction(title: NSLocalizedString("Done", comment: ""), style: .default, handler: nil)
            let alertController = UIAlertController(title: alertTitle, message: alertMessage, preferredStyle: .alert)
            alertController.addAction(doneAction)
            present(alertController, animated: true, completion: nil)
            validated = false
        }
        
        if validated == true{
            let dateFormatter : DateFormatter = DateFormatter()
            dateFormatter.dateFormat = "yyyyMMddHHmmss"
            let date = Date()
            let dateString = dateFormatter.string(from: date)
            let selectedReport = reports.first{$0.reportID == reportID}
            
            let ID = toAddress! + " \(dateString)"
            
            
            let expense = Expense(ExpenseID: ID, ExpenseName: toAddress, ExpenseTotal: expenseTotalCost, ExpenseDate: expenseDate, ExpenseLocation: fromAddress, ExpenseMemo: "None Provided Mileage", ExpenseCategory: cat, AddressToImage: "None Provided", AssociatedReport: selectedReport?.reportID, MileageFromAddress: fromAddress, MileageToAddress: expenseDistance?.description, MileagePerMileRate: 1.00, MileageTotalMiles: expenseTotalCost)
            
            var ref: DatabaseReference!
            ref = Database.database().reference()
            
            let currenttotal = selectedReport?.total
            let newTotal = currenttotal! - expenseTotalCost
            selectedReport?.total = newTotal
            
            
            ref.child("Reports").child((Auth.auth().currentUser?.uid)!).child((selectedReport?.reportID)!).updateChildValues(["endDate": selectedReport?.endDate,
                                                                                                                     "memo":selectedReport?.memo,
                                                                                                                     "name": selectedReport?.name,
                                                                                                                     "reportID":selectedReport?.reportID,
                                                                                                                     "startDate": selectedReport?.startDate,
                                                                                                                     "status":selectedReport?.status,
                                                                                                                     "total":selectedReport?.total, ])
            
            ref.child("Expenses")
                .child((Auth.auth().currentUser?.uid)!)
                .child(expenseID)
                .removeValue()
          
            _ = navigationController?.popToRootViewController(animated: true)
            
        }
        
        
    }
    
    
    
    @IBAction func deleteButton(_ sender: Any) {
      prepdeleteFields ()
        
    }
    
}
