package com.iot.technion.regrowth.model

import com.iot.technion.regrowth.R
import java.io.Serializable

class AnimalModel:Serializable {
    var name: String = ""
    var icon: Int = R.drawable.top_background
    var color: String = "#00ff00"
    var nodes: MutableMap<String,NodeModel> = mutableMapOf()


    constructor(name: String, icon: Int, color: String) {
        this.name = name
        this.icon = icon
        this.color = color
    }

    constructor(icon: Int) {
        this.icon = icon
    }

    constructor()
}