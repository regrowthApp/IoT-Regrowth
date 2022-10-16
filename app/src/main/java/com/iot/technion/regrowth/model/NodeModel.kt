package com.iot.technion.regrowth.model

import java.io.Serializable

class NodeModel :Serializable{

    var gatewayId: String = ""
    var battery: Int = 0
    var tension: Float = 0f
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