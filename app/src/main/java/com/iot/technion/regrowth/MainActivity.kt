package com.iot.technion.regrowth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import com.iot.technion.regrowth.databinding.ActivityTabbedBinding
import com.iot.technion.regrowth.model.AnimalModel
import com.iot.technion.regrowth.model.NodeModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.*
import com.iot.technion.regrowth.adapters.AnimalsAdapter
import com.iot.technion.regrowth.model.ThreshHoldsModel
import kotlinx.android.synthetic.main.dialog_create_animal.view.*
import kotlinx.android.synthetic.main.fragment_tabbed.view.*
import kotlinx.android.synthetic.main.item_node.*
import kotlinx.android.synthetic.main.item_tab.view.*
import kotlinx.android.synthetic.main.thresh_holds.view.*


class MainActivity : AppCompatActivity() {

    var selectedAnimal: AnimalModel? = null
    private lateinit var binding: ActivityTabbedBinding
    var animals: ArrayList<AnimalModel> = ArrayList<AnimalModel>()
    lateinit var tabs: TabLayout
    var animalsAdapter: AnimalsAdapter? = null
    val TAG = "TabbedActivity"
    var database = FirebaseDatabase.getInstance()
    lateinit var myRef: DatabaseReference
    private var uid: String = ""
    var animals_list = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTabbedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uid = intent.getStringExtra("id").toString()
        myRef = database.getReference("users/${uid}")

        getFromFirebase()

        fillAnimalsList()
        for(animal in animals_list){
            getGateWay(animal)
        }

        tabs = binding.tabs

        tabs.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    Log.d(TAG, "onTabSelected: ${tab?.position}")
                    performTabSelected(tab)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    Log.e("TabbedActivity", "onTabUnselected: ${tab?.position}")
                    //performTabSelected(tab)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    Log.e("TabbedActivity", "onTabReselected: ${tab?.position}")
                    performTabSelected(tab)
                }

                fun performTabSelected(tab: TabLayout.Tab?) {

                    Log.e(
                        "TabbedActivity",
                        "onTabSelected: ${tab?.position} AnimalSize: ${animalsAdapter?.animals?.size}"
                    )
                    if (tab?.position == animals.size - 2) {
                        Log.e("TabbedActivity", "onTabSelected: ${tab?.position}")
                        //show dialog to create new animal
                        var dialog = AlertDialog.Builder(this@MainActivity)
                        dialog.setTitle("Add new animal")
                        var view = layoutInflater.inflate(R.layout.dialog_create_animal, null)
                        var animalModel = AnimalModel(Color.green(0))
                        dialog.setView(view)
                        dialog.setPositiveButton("Add") { dialog,
                                                          which ->
                            animalModel.name = view.animalNameSpinner.selectedItem.toString()
                            animalModel.color = getAnimalColor(animalModel)
                            if (view.animalNameSpinner.selectedItemPosition == -1) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Please select an animal",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@setPositiveButton
                            }
                            animalModel.icon = getAnimalIcon(animalModel.name)
                            animals_list.add(animalModel.name)
                            addAnimalToFirebase(animalModel)
                        }
                        dialog.setNegativeButton("Cancel") { dialog, which ->
                            dialog.dismiss()
                        }
                        dialog.show()
                    } else if (tab?.position == animals.size - 1) {
                        //show dialog to delete animal
                        var dialog = AlertDialog.Builder(this@MainActivity)
                        dialog.setTitle("Delete animal")
                        var view = layoutInflater.inflate(R.layout.dialog_remove_animal, null)
                        view.animalNameSpinner.adapter =
                            ArrayAdapter<String>(
                                this@MainActivity,
                                android.R.layout.simple_spinner_dropdown_item,
                                animals.subList(0, animals.size - 2)
                                    .map { animalModel -> animalModel.name }.toMutableList()
                            )

                        dialog.setView(view)
                        dialog.setPositiveButton("Delete") { dialog, which ->
                            val removed_animal = view.animalNameSpinner.selectedItem.toString()
                            myRef.child(removed_animal)
                                .removeValue()
                            animals_list.remove(removed_animal)
                            dialog.dismiss()
                        }
                        dialog.setNegativeButton("Cancel") { dialog, which ->
                            dialog.dismiss()
                        }
                        dialog.show()

                    } else {
                        //Proceed as normal
                    }
                }

            }
        )

        binding.fab.setOnClickListener {
            var dialog = AlertDialog.Builder(this@MainActivity)
            dialog.setTitle("Edit Animals Thresh holds")
            val animals = layoutInflater.inflate(R.layout.dialog_remove_animal,null)
            val picked_animal = animals.animalNameSpinner.selectedItem.toString()
            dialog.setNegativeButton("Cancel"){ dialog,which ->
                dialog.dismiss()
            }
            dialog.setPositiveButton("Continue"){dialog,which ->
                changeAnimalsThreshHold(picked_animal)
                dialog.dismiss()
            }
            dialog.setView(animals)
            dialog.show()
        }

        binding.topNaviBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.profile -> {
                    moveToProfile()
                }

                R.id.notification -> {
                    moveToNotification()}
            }
            true
        }

        binding.topNaviBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.profile -> {
                    moveToProfile()
                }
                R.id.notification -> {
                    moveToNotification()}
            }
            true
        }
    }

    private fun addAnimalToFirebase(animalModel: AnimalModel) {
        Log.e(TAG, "addAnimalToFirebase: ")
        myRef.child(animalModel.name).setValue(animalModel)
        val thresh_holds = ThreshHoldsModel()
        myRef.child("${animalModel.name}/thresh-holds").setValue(thresh_holds)

    }


    fun getAnimalColor(animalModel: AnimalModel): String {
        when (animalModel.name) {
            "Chicken" -> return "#F4B942"
            "Pig" -> return "#F95A10"
            "Sheep" -> return "#47A025"
            "Goat" -> return "#28BB11"
        }
        return "#FFFFFF"
    }

    fun getAnimalIcon(animal_name: String): Int {
        when (animal_name) {
            "Chicken" -> return R.drawable.ic_chicken
            "Goat" -> return R.drawable.ic_goat
            "Sheep" -> return R.drawable.ic_sheep
            "Pig" -> return R.drawable.ic_pig
        }
        return R.drawable.ic_chicken
    }

    fun checkIfAnimal(key: String?): Boolean {
        when (key) {
            "Chicken" -> return true
            "Pig" -> return true
            "Sheep" -> return true
            "Goat" -> return true
        }
        return false
    }

    private fun getFromFirebase() {
        myRef.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    animals.clear()
                    animalsAdapter?.removeAllAnimals()
                    animalsAdapter?.notifyDataSetChanged()
                    animalsAdapter = null
                    animalsAdapter = AnimalsAdapter(this@MainActivity, animals, uid)
                    binding.viewPager.adapter = null
                    binding.viewPager.adapter = animalsAdapter
                    TabLayoutMediator(binding.tabs, binding.viewPager,
                        TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                            val itemTab = LayoutInflater.from(this@MainActivity)
                                .inflate(R.layout.item_tab, null)
                            itemTab.animalIcon.setImageResource(animals[position].icon)
                            itemTab.animalName.text = animals[position].name
                            tab.customView = itemTab
                            tab.view.setBackgroundColor(Color.parseColor(animals[position].color))
                        }).attach()
                    Handler().postDelayed({
                        dataSnapshot.children.forEach {
                            if (checkIfAnimal(it.key)) {
                                val animal = AnimalModel()
                                animal.name = it.child("name").value.toString()
                                animal.color = it.child("color").value.toString()
                                animal.icon = getAnimalIcon(animal.name)

                                it.child("nodes").children.forEach {
                                    var node = NodeModel()
                                    node.gatewayId = it.child("gatewayId").value.toString()
                                    node.battery = it.child("battery").value.toString().toInt()
                                    node.connection = it.child("connection").value.toString()
                                    node.tension = it.child("tension").value.toString().toFloat()
                                    animal.nodes.put(node.gatewayId, node)
                                }

                                animals.add(animal)
                                Log.e(TAG, "onDataChange: $dataSnapshot")
                                Log.e(TAG, "onDataChange: ${animal.toString()}")

                            }
                        }
                        animals.add(AnimalModel("Add", R.drawable.ic_baseline_add_24, "#FFFFFF"))
                        animals.add(AnimalModel("Remove", R.drawable.ic_baseline_remove_24, "#FFFFFF"))
                        animalsAdapter?.resetAnimals(animals)
                        animalsAdapter?.notifyDataSetChanged()
                    }, 100)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            }
        )
    }

    private fun moveToProfile(){
        val intent = Intent(this@MainActivity,ProfileActivity::class.java)
        intent.putExtra("id",uid)
        startActivity(intent)
    }

    private fun moveToNotification(){
        val intent = Intent(this@MainActivity,NotificationListActivity::class.java)
        intent.putExtra("id",uid)
        startActivity(intent)
    }

    private fun fillAnimalsList(){
        database.reference.child("users/$uid").get().addOnCompleteListener {
            it.result.children.forEach {
                if(checkIfAnimal(it.key)){
                    it.key?.let { it1 -> animals_list.add(it1) }
                }
            }
        }
    }

    private fun getGateWay(animal_name: String){
        database.reference.child("users/$uid").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.hasChild(animal_name+"/gateway"))
                    {
                        val path = animal_name + "/gateway"
                        if(dataSnapshot.hasChild(path+"/id")){
                            binding.viewPager.tableRowHeading.gatewayId.text = dataSnapshot.child(path+"/id").value.toString()
//                            binding.viewPager.tableRowHeading.gatewayId.setText(database.reference.child(full_path + "/id").get().result.value.toString())
                        }

                        if(dataSnapshot.hasChild(path+"/battery")){
                            binding.viewPager.tableRowHeading.gateway_battery.setText(dataSnapshot.child(path+"/battery").value.toString())
                        }

                        if(dataSnapshot.hasChild(path+"/tension")){
                            binding.viewPager.tableRowHeading.gateway_tension.setText(dataSnapshot.child(path+"/tension").value.toString())
                        }

                        binding.viewPager.tableRowHeading.gateway_connection.setText("connected: yes")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            }
        )
    }

    private fun changeAnimalsThreshHold(animal : String){
        val view = layoutInflater.inflate(R.layout.thresh_holds,null)
        val dialog = AlertDialog.Builder(this@MainActivity)
        dialog.setView(view)

        dialog.setNegativeButton("Cancel"){dialog,which ->
            dialog.dismiss()
        }

        dialog.setPositiveButton("Save Changes"){dialog,which ->
            database.reference.child("users/$uid/$animal").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        fillThreshHoldsTexts(view,animal)
                        fillThreshHoldsEdit(view,dataSnapshot)
                        val thresh_holds = ThreshHoldsModel()
                        thresh_holds.max_weight = view.maximum_weight.text.toString()
                        thresh_holds.min_weight = view.minimum_weight.text.toString()
                        thresh_holds.max_temp = view.maximum_temp.text.toString()
                        thresh_holds.min_temp = view.minimum_temp.text.toString()
                        thresh_holds.max_hum = view.maximum_humidity.text.toString()
                        thresh_holds.min_hum = view.minimum_humidity.text.toString()

                        dataSnapshot.ref.setValue("thresh-holds").addOnSuccessListener {
                            Toast.makeText(this@MainActivity, "Thresh holds has been saved", Toast.LENGTH_SHORT).show()
                        }

                    }


                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException())
                    }
                }
            )
        }

        dialog.show()
    }

    private fun fillThreshHoldsTexts(view: View, animal: String){
        view.max_weight.text = "$animal Max Weight"
        view.min_weight.text = "$animal Min Weight"
        view.max_temp.text = "$animal Farm Max Temperature"
        view.min_temp.text = "$animal Farm Min Temperature"
        view.max_hum.text = "$animal Farm Max Humidity"
        view.min_hum.text = "$animal Farm Min Humidity"
    }

    private fun fillThreshHoldsEdit(view: View,dataSnapshot: DataSnapshot){
        if(dataSnapshot.hasChild("thresh-holds")){
            val gateway = dataSnapshot.child("thresh-holds")
            var temp_vals : String
            if(gateway.hasChild("max_weight")){
                temp_vals = gateway.child("max_weight").value.toString()
                if(temp_vals != "NaN") {
                    view.maximum_weight?.setText(temp_vals)
                }
            }

            if(gateway.hasChild("min_weight")){
                temp_vals = gateway.child("min_weight").value.toString()
                if(temp_vals != "NaN") {
                    view.minimum_weight?.setText(temp_vals)
                }
            }

            if(gateway.hasChild("max_temp")){
                temp_vals = gateway.child("max_temp").value.toString()
                if(temp_vals != "NaN") {
                    view.maximum_temp?.setText(temp_vals)
                }
            }

            if(gateway.hasChild("min_temp")){
                temp_vals = gateway.child("min_temp").value.toString()
                if(temp_vals != "NaN") {
                    view.minimum_temp?.setText(temp_vals)
                }
            }

            if(gateway.hasChild("max_hum")){
                temp_vals = gateway.child("max_hum").value.toString()
                if(temp_vals != "NaN") {
                    view.maximum_humidity?.setText(temp_vals)
                }
            }

            if(gateway.hasChild("min_hum")){
                temp_vals = gateway.child("min_hum").value.toString()
                if(temp_vals != "NaN") {
                    view.minimum_humidity?.setText(temp_vals)
                }
            }
        }
    }
}

