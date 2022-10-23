package com.iot.technion.regrowth.model

import java.io.Serializable
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class NodeModel :Serializable{

    var nodeId: String = ""
    var battery: String = ""
    var tension: String = ""
    var time : String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

    constructor(gatewayId: String, battery: String, tension: String) {
        this.nodeId = gatewayId
        this.battery = battery
        this.tension = tension
    }

    constructor(gatewayId: String) {
        this.nodeId = gatewayId
    }

    constructor()
}