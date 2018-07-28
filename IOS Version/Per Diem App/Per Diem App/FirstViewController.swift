//
//  FirstViewController.swift
//  Per Diem App
//
//  Created by Jason Claxton on 7/2/18.
//  Copyright © 2018 Jason Claxton. All rights reserved.
//

import UIKit
import Floaty
import FirebaseUI
import Firebase
import FirebaseDatabase
import PopupDialog




class FirstViewController: UIViewController, FUIAuthDelegate ,UITableViewDelegate, UITableViewDataSource{

    var floaty = Floaty()
    let authUI = FUIAuth.defaultAuthUI()
    var ref: DatabaseReference!
    let cellReuseIdentifier = "cell"
    var reports:[Report] = []
    var expenses:[Expense] = []
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var userNameField: UILabel!
    @IBOutlet weak var userEmailFiield: UILabel!
    var selectedReport: Report?
    

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
        layoutFAB()
        signInUI()
        //
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.navigationBar.topItem?.title = "Reports"
        self.navigationController?.navigationBar.barTintColor = #colorLiteral(red: 0.4509803922, green: 0.6, blue: 0.5294117647, alpha: 1)
        let rightButton = UIBarButtonItem(title: "Right", style: .plain, target: self, action: nil)
        self.navigationController?.navigationItem.rightBarButtonItem = rightButton
        Floaty.global.show()
        signInUI()
        UITabBar.appearance().barTintColor = #colorLiteral(red: 0.4509803922, green: 0.6, blue: 0.5294117647, alpha: 1)
    

    }
    
    func signIn(){
                do {
                    try authUI?.signOut()
                } catch  {
        
                }
    }
 
    func signInUI(){
        let providers: [FUIAuthProvider] = [
            FUIGoogleAuth()
        ]
        self.authUI?.providers = providers
        
        func application(_ app: UIApplication, open url: URL,
                         options: [UIApplicationOpenURLOptionsKey : Any]) -> Bool {
            let sourceApplication = options[UIApplicationOpenURLOptionsKey.sourceApplication] as! String?
            if FUIAuth.defaultAuthUI()?.handleOpen(url, sourceApplication: sourceApplication) ?? false {
                return true
            }
            // other URL handling goes here.
            return false
        }
        if Auth.auth().currentUser != nil {
            // User is signed in.
           userNameField.text = Auth.auth().currentUser?.displayName
           userEmailFiield.text = Auth.auth().currentUser?.email
            getData()
        } else {
            let authViewController = authUI?.authViewController()
            self.present(authViewController!, animated:true, completion:nil)
            Floaty.global.hide()
        }
    }

    func layoutFAB() {
        let camera = FloatyItem()
        camera.hasShadow = false
        camera.iconImageView.bounds = camera.bounds
        camera.title = "New Receipt Photo"
        camera.icon = UIImage(named: "005-camera")
        camera.handler = { item in
            if self.reports.count > 0{
               self.performSegue(withIdentifier: "toNewCameraExpense", sender: self)
            }else{
                self.noReportsFound()
            }
           
        }
        
        let mileage = FloatyItem()
        mileage.hasShadow = false
        mileage.iconImageView.bounds = mileage.bounds
        mileage.title = "Mileage"
        mileage.icon = UIImage(named: "006-distance")
        mileage.handler = { item in
            if self.reports.count > 0{
            self.performSegue(withIdentifier: "toNewMileage", sender: self)
            }else{
                self.noReportsFound()
            }
        }
        
        let manual = FloatyItem()
        manual.hasShadow = false
        manual.iconImageView.bounds = manual.bounds
        manual.title = "New Expenxe"
        manual.icon = UIImage(named: "003-bill")
        manual.handler = { item in
            if self.reports.count > 0{
            self.performSegue(withIdentifier: "toNewExpense", sender: self)
            }else{
                self.noReportsFound()
            }
        }
        
        let report = FloatyItem()
        report.hasShadow = false
        report.iconImageView.bounds = report.bounds
        report.title = "Report"
        report.icon = UIImage(named: "007-invoice")
        report.handler = { item in
            self.performSegue(withIdentifier: "toNewReport", sender: self)
        }
        
        Floaty.global.button.buttonColor = #colorLiteral(red: 0.5647058824, green: 0.7490196078, blue: 0.662745098, alpha: 1)
        Floaty.global.button.addItem(item: report)
        Floaty.global.button.addItem(item: manual)
        Floaty.global.button.addItem(item: mileage)
        Floaty.global.button.addItem(item: camera)
        Floaty.global.button.paddingX = 16
        Floaty.global.button.paddingY = 64

    }
    
    
    internal func authUI(_ authUI: FUIAuth, didSignInWith user: User?, error: Error?) {
        // handle user and error as necessary
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.reports.count
    }
    
    // create a cell for each table view row
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell:CustomCell = self.tableView.dequeueReusableCell(withIdentifier: cellReuseIdentifier) as! CustomCell
        cell.rName.text = self.reports[indexPath.row].name
        cell.rDate.text = self.reports[indexPath.row].startDate + " - " + self.reports[indexPath.row].startDate
        cell.rTotal.text = "$"+String(format:"%.2f", self.reports[indexPath.row].total)
        return cell
    }
    
    // method to run when table view cell is tapped
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print(reports[indexPath.row].name)
//        passData()
//        let controller = ReportDetails()
//        controller.report = reports[indexPath.row]
//        controller.allExpenses = expenses
        selectedReport = reports[indexPath.row]
        
        self.performSegue(withIdentifier: "report_details", sender: self)
    }


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
                self.tableView.reloadData()
            }
            self.tableView.reloadData()

        }) { (error) in
            print(error.localizedDescription)
        }
       getData2()
    }
    
    func getData2(){
        let userID = Auth.auth().currentUser?.uid
        ref = Database.database().reference()
        
        ref.child("Expenses").child(userID!).observe(.value, with: { (snapshot) in
            // Get user value
            let value = snapshot.value as? NSDictionary
            self.expenses.removeAll()
            if value != nil {
                let itemsArray: [NSObject] = value?.allValues as! [NSObject]
                for (item) in itemsArray {
                    let addressToImage = item.value(forKey: "addressToImage")! as? String
                    let associatedReport = item.value(forKey: "associatedReport")! as? String
                    let expenseCategory = item.value(forKey: "expenseCategory")! as? String
                    let expenseDate = item.value(forKey: "expenseDate")! as? String
                    let expenseID = item.value(forKey: "expenseID")! as? String
                    let expenseLocation = item.value(forKey: "expenseLocation")! as? String
                    let expenseMemo = item.value(forKey: "expenseMemo")! as? String
                    let expenseName = item.value(forKey: "expenseName")! as? String
                    let expenseTotal = item.value(forKey: "expenseTotal")! as? Double
                    let mileageFromAddress = item.value(forKey: "mileageFromAddress")! as? String
                    let mileagePerMileRate = item.value(forKey: "mileagePerMileRate")! as? Double
                    let mileageToAddress = item.value(forKey: "mileageToAddress")! as? String
                    let mileageTotalMiles = item.value(forKey: "mileageTotalMiles")! as? Double
                    
                    self.expenses.append(Expense(ExpenseID: expenseID,ExpenseName: expenseName,ExpenseTotal: expenseTotal,ExpenseDate: expenseDate,ExpenseLocation: expenseLocation,ExpenseMemo: expenseMemo,ExpenseCategory: expenseCategory,AddressToImage: addressToImage
                        ,AssociatedReport: associatedReport, MileageFromAddress: mileageFromAddress,MileageToAddress: mileageToAddress, MileagePerMileRate: mileagePerMileRate,MileageTotalMiles: mileageTotalMiles))
                    print(item)
                }
            }
            
        }) { (error) in
            print(error.localizedDescription)
        }
    }
    
    
    func passData(){
       
    
       // let vc = NewReportVC()
       // vc.nameOfReport.text = "My test memo"
        
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?)
    {
        if segue.destination is ReportDetails
        {
            let vc = segue.destination as? ReportDetails

            vc?.report = selectedReport
            vc?.allExpenses = expenses
            vc?.temp = expenses
            
        }
        
        
        
    }

    
    func noReportsFound(){
        // Prepare the popup assets
        let title = "No Reports Found"
        let message = "You Must FIrst Create A Report Before Creating an Expense"
        
        // Create the dialog
        let popup = PopupDialog(title: title, message: message)
        
        // Create buttons
        let buttonOne = CancelButton(title: "OK") {
            print("You canceled the car dialog.")
        }
        
       
        
        // Add buttons to dialog
        // Alternatively, you can use popup.addButton(buttonOne)
        // to add a single button
        popup.addButtons([buttonOne])
        
        // Present dialog
        self.present(popup, animated: true, completion: nil)
    }
    
    
}

