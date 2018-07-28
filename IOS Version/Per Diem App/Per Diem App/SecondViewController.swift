//
//  SecondViewController.swift
//  Per Diem App
//
//  Created by Jason Claxton on 7/2/18.
//  Copyright © 2018 Jason Claxton. All rights reserved.
//

import UIKit
import Floaty
import Firebase
import FirebaseDatabase
import FirebaseUI


class SecondViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

    let authUI = FUIAuth.defaultAuthUI()
    var ref: DatabaseReference!
    let cellReuseIdentifier = "cell1"
    var expenses:[Expense] = []
    var floaty = Floaty()
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var userNameField: UILabel!
    @IBOutlet weak var userEmailFiield: UILabel!
    var SelectedExpense:Expense!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        fillInTop()
        tableView.delegate = self
        tableView.dataSource = self
        getData()
        signInUI()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.navigationBar.topItem?.title = "Expenses"
        Floaty.global.show()
    }
    
    func signIn(){
        do {
            try authUI?.signOut()
        } catch  {
            
        }
    }
    
    func layoutFAB() {
        let camera = FloatyItem()
        camera.hasShadow = false
        camera.iconImageView.bounds = camera.bounds
        camera.title = "New Receipt Photo"
        camera.icon = UIImage(named: "005-camera")
        camera.handler = { item in
            self.performSegue(withIdentifier: "toNewCameraExpense", sender: self)
        }
        
        let mileage = FloatyItem()
        mileage.hasShadow = false
        mileage.iconImageView.bounds = mileage.bounds
        mileage.title = "Mileage"
        mileage.icon = UIImage(named: "006-distance")
        mileage.handler = { item in
            self.performSegue(withIdentifier: "toNewMileage", sender: self)
        }
        
        let manual = FloatyItem()
        manual.hasShadow = false
        manual.iconImageView.bounds = manual.bounds
        manual.title = "New Expense"
        manual.icon = UIImage(named: "003-bill")
        manual.handler = { item in
            self.performSegue(withIdentifier: "toNewExpense", sender: self)
        }
        
        let report = FloatyItem()
        report.hasShadow = false
        report.iconImageView.bounds = report.bounds
        report.title = "Report"
        report.icon = UIImage(named: "007-invoice")
        report.handler = { item in
            self.performSegue(withIdentifier: "toNewReport", sender: self)
        }
        
        floaty.addItem(item: report)
        floaty.addItem(item: manual)
        floaty.addItem(item: mileage)
        floaty.addItem(item: camera)
        floaty.paddingX = 16
        
        self.view.addSubview(floaty)
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.expenses.count
    }
    
    // create a cell for each table view row
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell:CustomCell = self.tableView.dequeueReusableCell(withIdentifier: cellReuseIdentifier) as! CustomCell
        let repotName = self.expenses[indexPath.row].associatedReport
        let endIndex = repotName.index(repotName.endIndex, offsetBy: -10)
        let realName = repotName.prefix(upTo: endIndex)

        cell.rName.text = self.expenses[indexPath.row].expenseName+" ("+realName+")"
        cell.rDate.text = self.expenses[indexPath.row].expenseDate
        cell.rTotal.text = "$"+String(format:"%.2f", self.expenses[indexPath.row].expenseTotal)
        return cell
    }
    
    // method to run when table view cell is tapped
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {

        SelectedExpense = self.expenses[indexPath.row]
        if SelectedExpense.expenseName.starts(with: "Mile"){
            self.performSegue(withIdentifier: "toMileageDetails", sender: self)
        }else{
            self.performSegue(withIdentifier: "toExpenseDetails", sender: self)
        }
        
        
        print(expenses[indexPath.row].expenseName)
    }
    
    func fillInTop(){
        if Auth.auth().currentUser != nil {
            // User is signed in.
            userNameField.text = Auth.auth().currentUser?.displayName
            userEmailFiield.text = Auth.auth().currentUser?.email
            getData()
        } else {
            
            //self.present(authViewController!, animated:true, completion:nil)
        }
    }

    func getData(){
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
                self.tableView.reloadData()
            }

        }) { (error) in
            print(error.localizedDescription)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?)
    {
        if segue.destination is ExpenseDetails
        {
            let vc = segue.destination as? ExpenseDetails
            vc?.name = SelectedExpense.expenseName
            vc?.memo = SelectedExpense.expenseMemo
            vc?.date = SelectedExpense.expenseDate
            vc?.category = SelectedExpense.expenseCategory
            vc?.report = SelectedExpense.associatedReport
            
           let temp:String = "$"+String(format:"%.2f", SelectedExpense.expenseTotal)
            
            vc?.cost = temp
        } else if segue.destination is MileageExpenseDetails {
            let vc = segue.destination as? MileageExpenseDetails
            vc?.toAddress = SelectedExpense.expenseName
            vc?.fromAddress = SelectedExpense.mileageFromAddress
            vc?.date = SelectedExpense.expenseDate
            vc?.perMile = SelectedExpense.mileagePerMileRate.description
            vc?.totalMiles = SelectedExpense.mileageTotalMiles.description
            vc?.totalCost = SelectedExpense.expenseTotal.description
            vc?.category = SelectedExpense.expenseCategory
            vc?.report = SelectedExpense.associatedReport
            vc?.expenseID = SelectedExpense.expenseID

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

}

