package com.iot.technion.regrowth.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NodeModel :Serializable{
    @SerializedName("Gateway Id")
    var gatewayId: String = ""
    @SerializedName("battery")
    var battery: Int = 0
    @SerializedName("tension")
    var tension: Float = 0f
    @SerializedName("connection")
    var connection:String = ""

    constructor(gatewayId: String, battery: Int, tension: Float, connection: String) {
        this.gatewayId = gatewayId
        this.battery = battery
        this.tension = tension
        this.connection = connection
    }

    constructor(gatewayId: String) {
        this.gatewayId = gatewayId
    }

    constructor()
}