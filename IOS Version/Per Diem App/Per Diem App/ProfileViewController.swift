//
//  ProfileViewController.swift
//  Per Diem App
//
//  Created by Jason Claxton on 7/25/18.
//  Copyright © 2018 Jason Claxton. All rights reserved.
//

import Foundation
import UIKit
import FirebaseUI
import Firebase
import ImageLoader
import Floaty
import PopupDialog



class ProfileViewController: UIViewController{
    
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var profileImageView: UIImageView!
    let authUI = FUIAuth.defaultAuthUI()
    @IBOutlet weak var nameEntryField: UITextField!
    @IBOutlet weak var emailEntryField: UITextField!
    
    
    override func viewDidLoad() {
        Floaty.global.hide()
        nameLabel.text = authUI?.auth?.currentUser?.displayName
        nameEntryField.text = authUI?.auth?.currentUser?.displayName
        emailEntryField.text = authUI?.auth?.currentUser?.email
        

        let imageurl = authUI?.auth?.currentUser?.photoURL
        if imageurl != nil {
            profileImageView.load.request(with: imageurl!)
        }else{
            profileImageView.image = UIImage.init(named: "007-invoice")
        }
  
        
        
 
    }
    
    @IBAction func sendPasswordButton(_ sender: Any) {
        Auth.auth().sendPasswordReset(withEmail: (authUI?.auth?.currentUser?.email)!) { (error) in
            // ...
        }
    }
    
    
    @IBAction func updateProfileButton(_ sender: Any) {
        
        let changeRequest = Auth.auth().currentUser?.createProfileChangeRequest()
        changeRequest?.displayName = nameEntryField.text
        changeRequest?.commitChanges { (error) in
            // ...
        }
        
        Auth.auth().currentUser?.updateEmail(to: emailEntryField.text!) { (error) in
            // ...
        }
        _ = navigationController?.popToRootViewController(animated: true)
    }
    
    @IBAction func logOutButton(_ sender: Any) {
        do {
            try authUI?.signOut()
            _ = navigationController?.popToRootViewController(animated: true)
        } catch  {
            
        }
    }
    
    
    @IBAction func deleteAccount(_ sender: Any) {
        confirmDelete()
    }
    
    func confirmDelete(){
        // Prepare the popup assets
        let title = "Delete Account?"
        let message = "Are you sure you want to delete your account all data will be lost"
        
        // Create the dialog
        let popup = PopupDialog(title: title, message: message)
        
        // Create buttons
        let buttonOne = CancelButton(title: "CANCEL") {
            print("You canceled the car dialog.")
        }
        
        // This button will not the dismiss the dialog
        let buttonTwo = DefaultButton(title: "DELETE", dismissOnTap: true) {
            let user = Auth.auth().currentUser
            
            user?.delete { error in
                if error != nil {
                    // An error happened.
                } else {
                }
                _ = self.navigationController?.popToRootViewController(animated: true)
            }
            
        }
        
        // Add buttons to dialog
        // Alternatively, you can use popup.addButton(buttonOne)
        // to add a single button
        popup.addButtons([buttonOne, buttonTwo])
        
        // Present dialog
        self.present(popup, animated: true, completion: nil)
    }
    
}
