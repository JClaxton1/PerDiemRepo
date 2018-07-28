//
//  NewExpenseVC.swift
//  Per Diem App
//
//  Created by Jason Claxton on 7/4/18.
//  Copyright © 2018 Jason Claxton. All rights reserved.
//

import Foundation
import UIKit
import Floaty
import AAPickerView
import Firebase
import FirebaseDatabase
import GooglePlaces
import GooglePlacePicker
import PDTSimpleCalendar
import FormValidatorSwift
import ImagePicker



class NewExpenseVC: UIViewController, UITextFieldDelegate,GMSPlacePickerViewControllerDelegate,PDTSimpleCalendarViewDelegate, ImagePickerDelegate{


    
    @IBOutlet weak var headerView: UIView!
    @IBOutlet weak var nameOfBusiness: UITextField!
    @IBOutlet weak var expenseMemo: UITextField!
    @IBOutlet weak var date: UITextField!
    @IBOutlet weak var totalCost: UITextField!
    @IBOutlet weak var categoryPicker: AAPickerView!
    @IBOutlet weak var reportPicker: AAPickerView!
    var ref: DatabaseReference!
    var reports:[Report] = []
    var isKeyboardAppear = false
    
    @IBOutlet weak var imageView: UIImageView!
    var myImage: UIImage!
    var resultsText = ""

    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupHeaderView()
        getData()
        date.delegate = self
        
        self.nameOfBusiness.delegate = self
        self.expenseMemo.delegate = self
        self.totalCost.delegate = self
        
        
        
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name:NSNotification.Name.UIKeyboardWillShow, object: nil);
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name:NSNotification.Name.UIKeyboardWillHide, object: nil);

        
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
    
    
    override func viewWillAppear(_ animated: Bool) {
        self.title = "New Expense"
        Floaty.global.hide()
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.view.endEditing(true)
        return false
    }
    
    
    //Actions
    @IBAction func mapAction(_ sender: Any) {
        let config = GMSPlacePickerConfig(viewport: nil)
        let placePicker = GMSPlacePickerViewController(config: config)
        placePicker.delegate = self
        placePicker.modalPresentationStyle = .popover
        present(placePicker, animated: true, completion: nil)
    }

    @IBAction func dateClicked(_ sender: Any) {
       
        
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        if textField == date{
            let  calendarPicker = PDTSimpleCalendarViewController()
            calendarPicker.delegate = self
            present(calendarPicker, animated: true, completion: nil)
        }

    }
    
    @IBAction func saveExpense(_ sender: Any) {
        validateFields()
    }
    
    @IBAction func addPhoto(_ sender: Any) {
    }
    
    func validateFields (){
        let name = nameOfBusiness.text
        let Date1 = date.text
        let cost = totalCost.text!
        let costdouble = Double(totalCost.text!)
        var memo = expenseMemo.text
        let cat = categoryPicker.text
        let reportID = reportPicker.text
        
        var alertTitle: String
        var alertMessage: String
        var validated = true
        
        
        
        let validator = PresentValidator()
        var result = validator.checkConditions(name)
        if result != nil {
            alertTitle = NSLocalizedString("Error", comment: "")
            alertMessage = NSLocalizedString("Please Enter Name Of Buisness", comment: "")
            let doneAction = UIAlertAction(title: NSLocalizedString("Done", comment: ""), style: .default, handler: nil)
            let alertController = UIAlertController(title: alertTitle, message: alertMessage, preferredStyle: .alert)
            alertController.addAction(doneAction)
            present(alertController, animated: true, completion: nil)
            validated = false
        }
        
        result = validator.checkConditions(Date1)
        if result != nil {
            alertTitle = NSLocalizedString("Error", comment: "")
            alertMessage = NSLocalizedString("Please Select A Date", comment: "")
            let doneAction = UIAlertAction(title: NSLocalizedString("Done", comment: ""), style: .default, handler: nil)
            let alertController = UIAlertController(title: alertTitle, message: alertMessage, preferredStyle: .alert)
            alertController.addAction(doneAction)
            present(alertController, animated: true, completion: nil)
            validated = false
        }
        
        result = validator.checkConditions(String(cost))
        if result != nil {
            alertTitle = NSLocalizedString("Error", comment: "")
            alertMessage = NSLocalizedString("Please Enter Total For Expense", comment: "")
            let doneAction = UIAlertAction(title: NSLocalizedString("Done", comment: ""), style: .default, handler: nil)
            let alertController = UIAlertController(title: alertTitle, message: alertMessage, preferredStyle: .alert)
            alertController.addAction(doneAction)
            present(alertController, animated: true, completion: nil)
            validated = false
        }
        
        result = validator.checkConditions(cat)
        if result != nil {
            alertTitle = NSLocalizedString("Error", comment: "")
            alertMessage = NSLocalizedString("Please Select A Report Category", comment: "")
            let doneAction = UIAlertAction(title: NSLocalizedString("Done", comment: ""), style: .default, handler: nil)
            let alertController = UIAlertController(title: alertTitle, message: alertMessage, preferredStyle: .alert)
            alertController.addAction(doneAction)
            present(alertController, animated: true, completion: nil)
            validated = false
        }
        
        result = validator.checkConditions(reportID)
        if result != nil {
            alertTitle = NSLocalizedString("Error", comment: "")
            alertMessage = NSLocalizedString("Please Select A Report", comment: "")
            let doneAction = UIAlertAction(title: NSLocalizedString("Done", comment: ""), style: .default, handler: nil)
            let alertController = UIAlertController(title: alertTitle, message: alertMessage, preferredStyle: .alert)
            alertController.addAction(doneAction)
            present(alertController, animated: true, completion: nil)
            validated = false
        }

        result = validator.checkConditions(memo)
        if result != nil {
            memo = name! + " Memo"
        }
        
        
        if validated == true{
            let dateFormatter : DateFormatter = DateFormatter()
            dateFormatter.dateFormat = "yyyyMMddHHmmss"
            let date = Date()
            let dateString = dateFormatter.string(from: date)
            let selectedReport = reports.first{$0.reportID == reportID}
            
            let ID = name! + " \(dateString)"
            
            
            let expense = Expense(ExpenseID: ID, ExpenseName: name, ExpenseTotal: costdouble, ExpenseDate: Date1, ExpenseLocation: "None Provided", ExpenseMemo: memo, ExpenseCategory: cat, AddressToImage: "None Provided", AssociatedReport: reportID, MileageFromAddress: "None Provided", MileageToAddress: "None Provided", MileagePerMileRate: 0.00, MileageTotalMiles: 0.00)
            
            var ref: DatabaseReference!
            ref = Database.database().reference()
            
            var currenttotal = selectedReport?.total
            var newTotal = currenttotal! + costdouble!
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
                                                                                                            "expenseName": expense.expenseName,
                                                                                                            "expenseTotal": expense.expenseTotal,
                                                                                                            "mileageFromAddress": expense.mileageFromAddress,
                                                                                                            "mileagePerMileRate": expense.mileagePerMileRate,
                                                                                                            "mileageToAddress": expense.mileageToAddress,
                                                                                                            "mileageTotalMiles": expense.mileageTotalMiles, ])
            _ = navigationController?.popToRootViewController(animated: true)
            
        }
        
        
    }
    
    //Setting Up Header Top View
    func setupHeaderView(){
        headerView.layer.masksToBounds = false
        headerView.layer.shadowColor = UIColor.black.cgColor
        headerView.layer.shadowOpacity = 0.5
        headerView.layer.shadowOffset = CGSize(width: -1, height: 1)
        headerView.layer.shadowRadius = 1.25
        
        headerView.layer.shadowPath = UIBezierPath(rect: headerView.bounds).cgPath
        headerView.layer.shouldRasterize = true
        headerView.layer.rasterizationScale =  headerView.layer.contentsScale;
    }

    @IBAction func takePhoto(_ sender: Any) {
        let imagePickerController = ImagePickerController()
        imagePickerController.delegate = self
        present(imagePickerController, animated: true, completion: nil)
    }
    
    //Configuring Picker
    func configPicker() {
        categoryPicker.pickerType = .StringPicker
        
        let stringData = [ "Computer Related Equiptment", "Education, Research, and Conferences",
        "Employee Incentives and Gifts","Food/Snacks/Beverage - Not Show/Travel Related","GST/HST Tax",
        "Information Services","Lodging","Marketing Expense","Meals-Travel", "Mileage","Office Supplies","Research and Development",
        "Show Lodging","Show Meals","Show Transportation","Taxes and Licenses","Transportation - Non Show","Travel - Non Show","Travel - Sales",
        "Travel Advance"]
        categoryPicker.stringPickerData = stringData
        categoryPicker.pickerRow.font = UIFont(name: "American Typewriter", size: 30)
        
        categoryPicker.toolbar.barTintColor = .darkGray
        categoryPicker.toolbar.tintColor = .black
        
        categoryPicker.stringDidChange = { index in
            
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
    
    //Place Picker Delegate Stuff
    func placePicker(_ viewController: GMSPlacePickerViewController, didPick place: GMSPlace) {
        viewController.dismiss(animated: true, completion: nil)
        
        nameOfBusiness.text = place.name
        expenseMemo.text = place.formattedAddress
    }
    func placePickerDidCancel(_ viewController: GMSPlacePickerViewController) {
        viewController.dismiss(animated: true, completion: nil)
        
        print("No place selected")
    }
    
    
    func simpleCalendarViewController(_ controller: PDTSimpleCalendarViewController!, didSelect date: Date!) {
    
    
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/yyyy"
        let todaysDate = dateFormatter.string(from: date)
        
        self.date.text = todaysDate
        controller.dismiss(animated: true, completion: nil)
    }
    
    func wrapperDidPress(_ imagePicker: ImagePickerController, images: [UIImage]) {
        imagePicker.dismiss(animated: true, completion: nil)
        
    }
    
    func doneButtonDidPress(_ imagePicker: ImagePickerController, images: [UIImage]) {
        imagePicker.dismiss(animated: true, completion: nil)
        
        imageView.image = images[0]
        myImage = images[0]
        
    }
    
    func cancelButtonDidPress(_ imagePicker: ImagePickerController) {
        imagePicker.dismiss(animated: true, completion: nil)
        
    }
}
