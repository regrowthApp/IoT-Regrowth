package com.iot.technion.regrowth.model

import android.telephony.PhoneNumberUtils
import com.iot.technion.regrowth.R
import java.io.Serializable
import java.util.*

class ProfileModel {
    var name : String = ""
    var image : String = ""
    var birthday : String = ""
    var phone_number : String = ""
    var email : String = ""

    constructor(name : String, image : String, birthday : String, phone : String, email: String){
        this.name = name
        this.image = image
        this.birthday = birthday
        this.phone_number = phone
        this.email = email
    }
}