//
//  NewReportVC.swift
//  Per Diem App
//
//  Created by Jason Claxton on 7/4/18.
//  Copyright © 2018 Jason Claxton. All rights reserved.
//

import Foundation
import UIKit
import CalendarDateRangePickerViewController
import Floaty
import FormValidatorSwift
import Firebase
import FirebaseDatabase
import GooglePlacePicker

class NewReportVC: UIViewController, CalendarDateRangePickerViewControllerDelegate, UITextFieldDelegate, ValidatorControlDelegate, GMSPlacePickerViewControllerDelegate{
    

    

    @IBOutlet weak var nameOfReport: ValidatorTextField!
    @IBOutlet weak var reportMemo: ValidatorTextField!
    @IBOutlet weak var sDate: ValidatorTextField!
    @IBOutlet weak var eDate: ValidatorTextField!
    @IBOutlet weak var memo: ValidatorTextField!
    
    @IBAction func sDateAction(_ sender: Any){calCall()}
    @IBAction func eDateAction(_ sender: Any){calCall()}
    @IBOutlet weak var headerview: UIView!
    var form = ControlForm()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        setupHeaderView()
        self.nameOfReport.delegate = self
        self.reportMemo.delegate = self
        Floaty.global.hide()
    


       
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.title = "New Report"
        Floaty.global.hide()
        
        

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

    func didTapCancel() {
        self.navigationController?.dismiss(animated: true, completion: nil)
    }
    
    func didTapDoneWithDateRange(startDate: Date!, endDate: Date!) {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/yy"
        sDate.text = dateFormatter.string(from: startDate)
        eDate.text = dateFormatter.string(from: endDate)
        self.navigationController?.dismiss(animated: true, completion: nil)
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.view.endEditing(true)
        return false
    }
    func calCall(){
        let dateRangePickerViewController = CalendarDateRangePickerViewController(collectionViewLayout: UICollectionViewFlowLayout())
        dateRangePickerViewController.delegate = self
        dateRangePickerViewController.minimumDate = Date()
        dateRangePickerViewController.maximumDate = Calendar.current.date(byAdding: .year, value: 2, to: Date())
        dateRangePickerViewController.selectedStartDate = Date()
        dateRangePickerViewController.selectedEndDate = Calendar.current.date(byAdding: .day, value: 10, to: Date())

        let navigationController = UINavigationController(rootViewController: dateRangePickerViewController)
        self.navigationController?.present(navigationController, animated: true, completion: nil)
    }
    
    @IBAction func saveToFireBase(_ sender: Any) {
validateFields()
        

    }
    
    func validateFields (){
        let name = nameOfReport.text
        let startDate = sDate.text
        let endDate = eDate.text
        var memo = self.memo.text
        var alertTitle: String
        var alertMessage: String
        var reportID: String
        var validated = true

        
        
        let validator = PresentValidator()
        var result = validator.checkConditions(name)
        if result != nil {
            alertTitle = NSLocalizedString("Error", comment: "")
            alertMessage = NSLocalizedString("Please Enter Name Of Report", comment: "")
            let doneAction = UIAlertAction(title: NSLocalizedString("Done", comment: ""), style: .default, handler: nil)
            let alertController = UIAlertController(title: alertTitle, message: alertMessage, preferredStyle: .alert)
            alertController.addAction(doneAction)
            present(alertController, animated: true, completion: nil)
            validated = false
        }
        
        result = validator.checkConditions(startDate)
        if result != nil {
            alertTitle = NSLocalizedString("Error", comment: "")
            alertMessage = NSLocalizedString("Please Enter Start Date", comment: "")
            let doneAction = UIAlertAction(title: NSLocalizedString("Done", comment: ""), style: .default, handler: nil)
            let alertController = UIAlertController(title: alertTitle, message: alertMessage, preferredStyle: .alert)
            alertController.addAction(doneAction)
            present(alertController, animated: true, completion: nil)
            validated = false
        }

        result = validator.checkConditions(endDate)
        if result != nil {
            alertTitle = NSLocalizedString("Error", comment: "")
            alertMessage = NSLocalizedString("Please Enter End Date", comment: "")
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
            reportID = name! + " "+Date().description
            
            var report = Report (Name: name, StartDate: startDate, EndDate: endDate, Memo: memo, ReportID: reportID, Status: "Created", Total: 0.00)
            var ref: DatabaseReference!
            ref = Database.database().reference()
            
            ref.child("Reports").child((Auth.auth().currentUser?.uid)!).child(reportID).setValue(["endDate": report.endDate,
                                                                                                "memo":report.memo,
                                                                                                "name": report.name,
                                                                                                "reportID":report.reportID,
                                                                                                "startDate": report.startDate,
                                                                                                "status":report.status,
                                                                                                "total":report.total, ])
            _ = navigationController?.popToRootViewController(animated: true)

        }


    }
    
    
    
    func validatorControlDidChange(_ validatorControl: ValidatorControl) {

    }
    
    func validatorControl(_ validatorControl: ValidatorControl, changedValidState validState: Bool) {
        guard let controlView = validatorControl as? UIView else {
            return
        }
        if validState {
            controlView.layer.borderColor = nil
            controlView.layer.borderWidth = 0.0
        } else {
            controlView.layer.borderColor = UIColor.red.cgColor
            controlView.layer.borderWidth = 2.0
        }
    }
    
    func validatorControl(_ validatorControl: ValidatorControl, violatedConditions conditions: [Condition]) {
        var errorText = ""
        for condition in conditions {
            errorText += condition.localizedViolationString
        }

    }
    
    func placePicker(_ viewController: GMSPlacePickerViewController, didPick place: GMSPlace) {
        viewController.dismiss(animated: true, completion: nil)
    
//        print("Place name \(place.name)")
//        print("Place address \(place.formattedAddress)")
//        print("Place attributions \(place.attributions)")
    }
    
    func placePickerDidCancel(_ viewController: GMSPlacePickerViewController) {
        viewController.dismiss(animated: true, completion: nil)
    }
}

