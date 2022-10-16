package com.iot.technion.regrowth

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.iot.technion.regrowth.databinding.AddNodeBinding
import com.iot.technion.regrowth.databinding.FragmentTabbedBinding
import com.iot.technion.regrowth.model.AnimalModel
import com.iot.technion.regrowth.model.NodeModel
import org.nield.kotlinstatistics.median


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class AnimalsAdapter(private val context: Context, private val animalList: ArrayList<AnimalModel>,private val uid: String):
    RecyclerView.Adapter<AnimalsAdapter.MyVH>() {
    val TAG = "SectionsPagerAdapter"
    var animals: ArrayList<AnimalModel> = ArrayList<AnimalModel>()
    val activity: MainActivity = context as MainActivity
    val database = FirebaseDatabase.getInstance()

    public fun resetAnimals(animals: ArrayList<AnimalModel>) {
        this.animals.clear()
        this.animals.addAll(animals)
    }

    public fun removeAllAnimals() {
        this.animals = ArrayList<AnimalModel>()
    }

    override fun getItemCount(): Int {
        return animals.size
    }

    override fun onBindViewHolder(holder: MyVH, position: Int) {
        if (position < (animals.size-2)) {
            var animal=animals[position]
            holder.binding.root.setBackgroundColor(Color.parseColor(animal.color))
            loadPieCharts(holder.binding,animal.name)
            setupAnimalsChart(holder.binding,animal.name)
            holder.binding.nodeBtn.setOnClickListener {
                addNode(animal)
            }
            setUpNodes(holder.binding, animal,context)
        }
    }

    private fun setUpNodes(binding: FragmentTabbedBinding, animal: AnimalModel, context: Context) {

        val adapter = NodesAdapter(context, animal.nodes)
        binding.nodesRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        binding.nodesRecyclerView.adapter = adapter
    }

    private fun addNode(animal: AnimalModel) {
        var b=AddNodeBinding.inflate(LayoutInflater.from(context))
        var node=NodeModel()
        AlertDialog.Builder(context)
            .setTitle("Add Node")
            .setView(b.root)
            .setPositiveButton("Add") { dialog, which ->
                if (b.nodeId.text.toString().isEmpty()) {
                    Toast.makeText(context, "Please enter a node id", Toast.LENGTH_SHORT).show()
                }else if (b.batteryPercentage.text.toString().isEmpty()) {
                    Toast.makeText(context, "Please enter a battery percentage", Toast.LENGTH_SHORT).show()
                }else if (b.batteryTension.text.toString().isEmpty()) {
                    Toast.makeText(context, "Please enter a battery tension", Toast.LENGTH_SHORT).show()
                }else{
                    node.gatewayId=b.nodeId.text.toString()
                    node.battery=b.batteryPercentage.text.toString().toInt()
                    node.tension=b.batteryTension.text.toString().toFloat()
                    node.connection=b.nodeConnection.selectedItem.toString()
                    Log.e(TAG, "addNode:${node.connection}", )


                    var database = FirebaseDatabase.getInstance()
                    var myRef = database.getReference("users/${uid}/${animal.name}/nodes/${node.gatewayId}")
                    myRef.setValue(node).addOnSuccessListener {
                        Toast.makeText(context, "Node added successfully", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Log.e(TAG, "addNode: ${it.message}", )
                    }
                }

            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
            .show()
    }

    class MyVH(view: FragmentTabbedBinding) : RecyclerView.ViewHolder(view.root) {
        val binding:FragmentTabbedBinding = view
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
        val view = FragmentTabbedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyVH(view)
    }

    private fun setupAnimalsChart(binding: FragmentTabbedBinding, animal : String) {
        var barlist1 = ArrayList<BarEntry>()
        var barlsit2 = ArrayList<BarEntry>()
        var xAxis = ArrayList<String>()
        val database = FirebaseDatabase.getInstance()
        database.reference.child("users/${uid}/Data").orderByKey().addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children.forEach {
                        val animal_id = it.key.toString().toFloat()
                        xAxis.add(animal_id.toString())
                        barlist1.add(
                            BarEntry(
                                animal_id,
                                it.child("animal_weight").value.toString().toFloat(),
                                animal_id.toString()
                            )
                        )
                        barlsit2.add(
                            BarEntry(
                                animal_id,
                                it.child("activity").value.toString().toFloat(),
                                animal_id.toString()
                            )
                        )
                    }
                    val chart = binding.animalsChart
                    val barDataSet1 = BarDataSet(barlist1, "weight")
                    barDataSet1.color = Color.parseColor("#006400")
                    val barDataSet2 = BarDataSet(barlsit2, "activity")
                    barDataSet2.color = Color.parseColor("#F44336")
                    val barData = BarData(barDataSet1, barDataSet2)

                    val barSpace = 0.2f
                    val groupSpace = 0.03f
                    barData.barWidth = 0.4f

                    chart.data = barData
                    chart.xAxis.axisMinimum = 0f
                    chart.axisLeft.axisMinimum = 0f
                    chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxis)
                    chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    chart.groupBars(0f,groupSpace,barSpace)
                    chart.enableScroll()
                    chart.invalidate()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            }
        )
    }

    private fun loadPieCharts(binding: FragmentTabbedBinding,animal : String) {
        loadActivityChart(binding,animal)
//        loadHeatChart(binding,animal)
//        loadWeightChart(binding,animal)

    }

    private fun loadActivityChart(binding: FragmentTabbedBinding, animal: String) {
        database.reference.child("Kamal_User/Data/Sheep").orderByKey().addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var totalActivity = 0
                    val activities = arrayListOf<Int>()
                    dataSnapshot.children.forEach {
                        val singleActivity = it.child("15-10-2022").child("activity")
                            .value.toString().toInt()
                        totalActivity += singleActivity
                        activities.add(singleActivity)
                    }

                    val median = activities.median()
                    val pieEntries = arrayListOf<PieEntry>()
                    val pieChart = binding.activityChart

                    if (totalActivity == 0) {
                        totalActivity = 1
                    }

                    val complement = (100 - median).toFloat()

                    pieEntries.add(PieEntry(20f, ""))
                    pieEntries.add(PieEntry(40f, ""))
                    pieChart.centerText = "Activity Level\n$median%"
                    val pieDataSet = PieDataSet(pieEntries, "")
                    pieDataSet.setColors(Color.parseColor("#006400"), Color.parseColor("#FFFFFF"))
                    val pieData = PieData(pieDataSet)
                    pieData.setDrawValues(false)
                    pieData.setValueFormatter(PercentFormatter())
                    pieChart.animateX(1000)
                    pieChart.setUsePercentValues(true)
                    pieChart.setCenterTextSize(15f)
                    pieChart.legend.isEnabled = false
                    pieChart.description.isEnabled = false
                    pieChart.holeRadius = 85f
                    pieChart.data = pieData
                    pieChart.invalidate()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            }
        )
    }

    private fun loadHeatChart(binding: FragmentTabbedBinding, animal: String){
        database.reference.child("Kamal_User/Data/Sheep").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            }
        )
    }

    private fun loadWeightChart(binding: FragmentTabbedBinding, animal: String){
        database.reference.child("Kamal_User/Data/Sheep").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            }
        )
    }

    }
