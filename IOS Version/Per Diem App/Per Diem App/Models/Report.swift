//
//  Report.swift
//  Per Diem App
//
//  Created by Jason Claxton on 7/6/18.
//  Copyright © 2018 Jason Claxton. All rights reserved.
//

import Foundation
import UIKit

class Report {


    var name:String
    var startDate:String
    var endDate:String
    var memo:String
    var reportID:String
    var status:String
    var total:Double
    
    init(Name:String?,StartDate:String?,EndDate:String?,Memo:String?,ReportID:String?,
         Status:String?,Total:Double?) {
        self.name = Name ?? ""
        self.startDate = StartDate ?? ""
        self.endDate = EndDate ?? ""
        self.memo = Memo ?? ""
        self.reportID = ReportID ?? ""
        self.status = Status ?? ""
        self.total = Total!
        
    }

}
