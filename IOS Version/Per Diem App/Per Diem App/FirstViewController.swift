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



class FirstViewController: UIViewController, FUIAuthDelegate ,UITableViewDelegate, UITableViewDataSource{

    var floaty = Floaty()
    let authUI = FUIAuth.defaultAuthUI()
    var ref: DatabaseReference!
    let cellReuseIdentifier = "cell"
    var reports:[Report] = []
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var userNameField: UILabel!
    @IBOutlet weak var userEmailFiield: UILabel!
    

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
        layoutFAB()
        signInUI()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.navigationBar.topItem?.title = "Reports"
        self.navigationController?.navigationBar.barTintColor = #colorLiteral(red: 0.4509803922, green: 0.6, blue: 0.5294117647, alpha: 1)

        Floaty.global.show()
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
        }
    }

    func layoutFAB() {
        let camera = FloatyItem()
        camera.hasShadow = false
        camera.iconImageView.bounds = camera.bounds
        camera.title = "Camera"
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
        manual.title = "Manual"
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
//        self.performSegue(withIdentifier: "toNewReport", sender: self)
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

        }) { (error) in
            print(error.localizedDescription)
        }
        self.tableView.reloadData()
    }
    
    func passData(){
       
    
        let vc = NewReportVC()
       // vc.nameOfReport.text = "My test memo"
        
    }

    
    
}

