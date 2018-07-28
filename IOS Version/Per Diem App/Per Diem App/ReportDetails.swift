//
//  ReportDetails.swift
//  Per Diem App
//
//  Created by Jason Claxton on 7/25/18.
//  Copyright © 2018 Jason Claxton. All rights reserved.
//

import Foundation
import UIKit
import BmoViewPager
import Floaty
import SimplePDF
import PopupDialog
import MessageUI


class ReportDetails: UIViewController, BmoViewPagerDataSource, UITableViewDelegate, UITableViewDataSource, MFMailComposeViewControllerDelegate{

    
    
     let mailComposer = MFMailComposeViewController()

    var report: Report?
    var allExpenses: [Expense]?
     var temp: [Expense]?
    let cellReuseIdentifier = "MyCell"
    var selectedExp : [Expense]?
    
    @IBOutlet weak var tableView: UITableView!
    override func viewDidLoad() {
        Floaty.global.hide()
        pager.dataSource = self
        self.tableView.register(UITableViewCell.self, forCellReuseIdentifier: cellReuseIdentifier)
        tableView.delegate = self
        tableView.dataSource = self
        mailComposer.mailComposeDelegate = self
        selectedExp = self.getSelectedExpenses()
    }
    func bmoViewPagerDataSourceNumberOfPage(in viewPager: BmoViewPager) -> Int {
        return 3
    }
    @IBOutlet weak var pager: BmoViewPager!
    func bmoViewPagerDataSource(_ viewPager: BmoViewPager, viewControllerForPageAt page: Int) -> UIViewController {
        
        var vc: UIViewController?
        
        switch page {
        case 0:
            let bc = storyboard?.instantiateViewController(withIdentifier: "tryme") as? PageOne
            bc?.selectedReport = report
            bc?.selectedExpenses = getSelectedExpenses()
            vc = bc
        case 1:
            let bc = storyboard?.instantiateViewController(withIdentifier: "tryme2") as? PageTwo
            bc?.selectedReport = report
            bc?.selectedExpenses = getSelectedExpenses()
            vc = bc
        case 2:
            let bc = storyboard?.instantiateViewController(withIdentifier: "tryme3") as? PageThree
            bc?.selectedReport = report
            bc?.selectedExpenses = getSelectedExpenses()
            vc = bc
        default:
            let bc = storyboard?.instantiateViewController(withIdentifier: "tryme") as? PageOne
            bc?.selectedReport = report
            bc?.selectedExpenses = getSelectedExpenses()
            vc = bc
        }
       
        
        
        return vc!
        
    }
    
    func getSelectedExpenses() -> [Expense] {
        temp?.removeAll()
        let id = report?.reportID
        if (allExpenses?.count)! > 0{
            for i in allExpenses! {
                if i.associatedReport.elementsEqual(id!){
                     var exp = i
                    temp?.append(exp)
                }
            }
        }
        
        
        return temp!
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?)
    {
        if segue.destination is PageOne
        {
            let vc = segue.destination as? PageOne
            vc?.selectedExpenses = getSelectedExpenses()
            vc?.selectedReport = report

        }
        
        
        
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return  (selectedExp?.count)!
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        // create a new cell if needed or reuse an old one
        let cell:UITableViewCell = self.tableView.dequeueReusableCell(withIdentifier: cellReuseIdentifier) as UITableViewCell!
        
        // set the text from the data model
        cell.textLabel?.text = self.selectedExp?[indexPath.row].expenseName
        
        return cell
    }
    
    @IBAction func emailButton(_ sender: Any) {
       
        if( MFMailComposeViewController.canSendMail() ) {
            
             SendPdf()
        }
        
    }
    
    @IBAction func netsuiteButton(_ sender: Any) {
    }
    
 
    
    func sendPDFEmail(){
        let pdfURL = (FileManager.default.urls(for: .documentDirectory, in: .allDomainsMask)).last! as URL

        let a4PaperSize = CGSize(width: 595, height: 842)
        let pdf = SimplePDF(pageSize: a4PaperSize)
        
        pdf.setContentAlignment(.center)
        
        // add logo image
//        let logoImage = UIImage(named:"simple_pdf_logo")!
//        pdf.addImage(logoImage)
//        
        pdf.addLineSpace(30)
        
        pdf.setContentAlignment(.left)
        pdf.addText("Expense Name: "+(report?.name)!)
        pdf.addText("Expense Date: "+(report?.startDate)!+" - "+(report?.endDate)!)
        pdf.addText("Report ID: "+(report?.reportID)!)
        pdf.addText("Report Memo"+(report?.memo)!)
        pdf.addText("Report Total:"+(report?.total.description)!)
        pdf.addLineSeparator()
        pdf.addLineSpace(20.0)
        
        for expense in getSelectedExpenses(){
            pdf.addText("Expense Name"+expense.expenseName)
            pdf.addText("Expense Date"+expense.expenseDate)
            pdf.addText("Expense Memo"+expense.expenseMemo)
            pdf.addText("Expense Category"+expense.expenseCategory)
            pdf.addText("Expense Location"+expense.expenseLocation)
            pdf.addText("Expense Name"+expense.associatedReport)
            pdf.addText("Associated Report ID: "+expense.expenseID)
            pdf.addText("Associated Total: "+expense.expenseTotal.description)
            pdf.addText("Address To: "+expense.mileageToAddress)
            pdf.addText("Address From: "+expense.mileageFromAddress)
            pdf.addText("Per Mile Rate: "+expense.mileagePerMileRate.description)
            pdf.addText("Total Miles: "+expense.mileageTotalMiles.description)
            pdf.addLineSpace(20.0)

        }
        
       
        // Generate PDF data and save to a local file.
        if let documentDirectories = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first {
            
            let fileName = (report?.name)!+".pdf"
            let folderName = "PDF Folder"
            let documentsFileName = documentDirectories + "/" + fileName
            
            let pdfData = pdf.generatePDFdata()
           
            mailComposer.setSubject("Have you heard a swift?")
            mailComposer.setMessageBody("This is what they sound like.", isHTML: false)
            mailComposer.addAttachmentData(pdfData, mimeType: "application/pdf", fileName: fileName)
            self.present(mailComposer, animated: true, completion: nil)


//            do{
//                try pdfData.write(to: pdfURL, options: .atomic)
//                print("\nThe generated pdf can be found at:")
//                print("\n\t\(documentsFileName)\n")
//            }catch{
//                print(error)
//            }
        }
    }
    
    func SendPdf(){
        // Prepare the popup assets
        let title = "Send Report"
        let message = "Are you sure you want to submit this report?"
        
        // Create the dialog
        let popup = PopupDialog(title: title, message: message)
        
        // Create buttons
        let buttonOne = CancelButton(title: "CANCEL") {
            print("You canceled the car dialog.")
        }
        
        // This button will not the dismiss the dialog
        let buttonTwo = DefaultButton(title: "Send", dismissOnTap: true) {
            self.sendPDFEmail()
            
        }
        
        // Add buttons to dialog
        // Alternatively, you can use popup.addButton(buttonOne)
        // to add a single button
        popup.addButtons([buttonOne, buttonTwo])
        
        // Present dialog
        self.present(popup, animated: true, completion: nil)
    }

    func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
       dismiss(animated: true) {}
    }
    
}

