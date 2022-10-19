package com.iot.technion.regrowth.model

import android.telephony.PhoneNumberUtils
import com.iot.technion.regrowth.R
import java.io.Serializable
import java.util.*

class ProfileModel {
    var farm_name : String = ""
    var farm_image : String = ""
    var logo_image : String = ""
    var phone_number : String = ""
    var email : String = ""
    var address : String = ""

    constructor(name : String, farm_image : String, logo_image : String, phone : String, email: String,address : String){
        this.farm_name = name
        this.farm_image = farm_image
        this.logo_image = logo_image
        this.phone_number = phone
        this.email = email
        this.address = address
    }
}