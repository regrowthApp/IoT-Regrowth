package com.iot.technion.regrowth.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.iot.technion.regrowth.MainActivity
import com.iot.technion.regrowth.R
import com.iot.technion.regrowth.databinding.AddNodeBinding
import com.iot.technion.regrowth.databinding.ChartTabbedBinding
import com.iot.technion.regrowth.databinding.FragmentTabbedBinding
import com.iot.technion.regrowth.databinding.RemoveNodeBinding
import com.iot.technion.regrowth.model.AnimalModel
import com.iot.technion.regrowth.model.NodeModel
import kotlinx.android.synthetic.main.activity_tabbed.view.*
import kotlinx.android.synthetic.main.fragment_tabbed.view.*
import kotlinx.android.synthetic.main.remove_node.view.*
import org.nield.kotlinstatistics.median
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class AnimalsAdapter(private val context: Context, private val animalList: ArrayList<AnimalModel>,private val uid: String):
    RecyclerView.Adapter<AnimalsAdapter.MyVH>() {
    val TAG = "SectionsPagerAdapter"
    var animals: ArrayList<AnimalModel> = ArrayList<AnimalModel>()
    val activity: MainActivity = context as MainActivity

    val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    val currentTime = LocalTime.now().hour

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
            setupCharts(holder.binding,animal.name)
            holder.binding.nodeBtn.setOnClickListener {
                setupNodes(holder.binding,context,animal)
            }
            setUpNodes(holder.binding,animal,context)
        }
    }

    private fun setupNodes(binding: FragmentTabbedBinding, context: Context,animal: AnimalModel){
        AlertDialog.Builder(context)
            .setTitle("Edit Nodes")
            .setView(R.layout.edit_nodes)
            .setNeutralButton("Cancel"){dialog,which->
                dialog.dismiss()
            }
            .setPositiveButton("Add Node"){dialog,which->
                addNode(animal.name)
                dialog.dismiss()
            }
            .setNegativeButton("Remove Node"){dialog, which->
                deleteNode(animal.name)
                dialog.dismiss()
            }
            .show()
    }

    private fun setUpNodes(binding: FragmentTabbedBinding, animal: AnimalModel, context: Context) {

        val adapter = NodesAdapter(context,animal.name ,animal.nodes, uid)
        val gateway_adapter = GatewayAdapter(context,animal.name,uid)
        binding.nodesRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        binding.nodesRecyclerView.adapter = adapter
        binding.gatewayRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        binding.gatewayRecyclerView.adapter = gateway_adapter
    }

    private fun deleteNode(animal: String){
        var b=RemoveNodeBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Remove Node")
        database.reference.child("users/${uid}/${animal}").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val nodes = ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item)
                    if(dataSnapshot.hasChild("nodes")){
                        dataSnapshot.child("nodes").children.forEach {
                            nodes.add(it.key)
                        }
                    }
                    b.nodeAvailable.adapter = nodes
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            }
        )
        b.nodeAvailable
        dialog.setView(b.root)
        dialog.setPositiveButton("Delete"){dialog, which->
            val node_id = b.nodeAvailable.selectedItem.toString()
            database.reference.child("users/${uid}/${animal}/nodes/${node_id}").removeValue()
            dialog.dismiss()
        }
        dialog.setNegativeButton("Cancel"){dialog, which ->
                dialog.dismiss()
        }
        dialog.show()
    }

    private fun addNode(animal: String) {
        var b=AddNodeBinding.inflate(LayoutInflater.from(context))
        var node=NodeModel()
        AlertDialog.Builder(context)
            .setTitle("Add Node")
            .setView(b.root)
            .setPositiveButton("Add") { dialog, which ->
                if (b.nodeId.text.toString().isEmpty()) {
                    Toast.makeText(context, "Please enter a node id", Toast.LENGTH_SHORT).show()
                }else{
                    node.nodeId=b.nodeId.text.toString()
                    var database = FirebaseDatabase.getInstance()
                    var myRef = database.getReference("users/${uid}/${animal}/nodes/${node.nodeId}")
                    myRef.setValue(node).addOnSuccessListener {
                        Toast.makeText(context, "Node added successfully", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Log.e(TAG, "addNode: ${it.message}", )
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
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

    private fun setupCharts(binding: FragmentTabbedBinding, animal : String) {
        val barlist1 = ArrayList<BarEntry>()
        val barlist2 = ArrayList<BarEntry>()
        val weights = ArrayList<Float>()
        val xAxis = ArrayList<String>()
        var totalActivity = 0
        val activities = arrayListOf<Int>()

        val database = FirebaseDatabase.getInstance()
        database.reference.child("users/${uid}/Data/${animal}").orderByKey().addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var x_id = 0F
                    dataSnapshot.children.forEach {
                        if (it.hasChild(currentDate)) {
                            val animal_id = it.key.toString()
                            val singleAnimal_activity =
                                it.child("${currentDate}/activity").value.toString()
                            val singleAnimal_weight =
                                it.child("${currentDate}/weight").value.toString().toFloat()
                            xAxis.add(it.key.toString())
                            weights.add(singleAnimal_weight)

                            barlist1.add(
                                BarEntry(
                                    x_id,
                                    singleAnimal_weight,
                                    animal_id
                                )
                            )

                            barlist2.add(
                                BarEntry(
                                    x_id,
                                    singleAnimal_activity.toFloat(),
                                    animal_id
                                )
                            )

                            x_id = x_id+1
                            totalActivity += singleAnimal_activity.toInt()
                            activities.add(singleAnimal_activity.toInt())
                        }

                    }
                        val max_active = activities.withIndex().maxByOrNull { it.value }
                        val min_active = activities.withIndex().minByOrNull { it.value }
                        val max_weight = weights.withIndex().maxByOrNull { it.value }
                        val min_weight = weights.withIndex().minByOrNull { it.value }

                        if(!barlist1.isEmpty() and !barlist2.isEmpty()){
                            loadAnimalsChart(binding, barlist1, barlist2, xAxis)
                        }

                        if(!activities.isEmpty()){
                            loadActivityChart(binding, activities,totalActivity,xAxis[max_active!!.index],
                            max_active.value,xAxis[min_active!!.index],min_active.value)
                        }

                        if(!weights.isEmpty()){
                            loadWeightChart(binding, weights,xAxis[max_weight!!.index],max_weight.value,xAxis[min_weight!!.index],min_weight.value)
                        }


                        loadHeatChart(binding, animal)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            }
        )
    }

    private fun loadAnimalsChart(binding: FragmentTabbedBinding,barlist1 : ArrayList<BarEntry>, barlist2: ArrayList<BarEntry>, xAxis : ArrayList<String>){

        if(barlist1.isEmpty() || barlist2.isEmpty()){
            binding.animalsChart.invalidate()
            return
        }

        val chart = binding.animalsChart
        val barDataSet1 = BarDataSet(barlist1, "weight")
        barDataSet1.color = Color.parseColor("#006400")
        barDataSet1.valueTextSize = 10f
        val barDataSet2 = BarDataSet(barlist2, "activity")
        barDataSet2.color = Color.parseColor("#F44336")
        barDataSet2.valueTextSize = 10f
        val barData = BarData(barDataSet1, barDataSet2)

        val barSpace = 0.02f
        val groupSpace = 0.15f
        barData.barWidth = 0.2f

        chart.data = barData
        chart.xAxis.axisMinimum = 0f
        chart.axisLeft.axisMinimum = 0f
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxis)
        chart.xAxis.setCenterAxisLabels(true)
        chart.xAxis.granularity = 1F
        chart.xAxis.isGranularityEnabled = true
        chart.setVisibleXRangeMaximum(4F)
        chart.setDrawGridBackground(false)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.groupBars(0f,groupSpace,barSpace)
        chart.enableScroll()
        chart.isDragEnabled = true
        chart.isScrollContainer = true
        chart.isClickable = false
        chart.setFitBars(true)
        chart.description.text = ""
        chart.animateY(600)
        chart.isClickable = false

        val legend = chart.legend
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.textSize = 10f
        legend.orientation = Legend.LegendOrientation.HORIZONTAL

        chart.invalidate()

    }

    private fun loadActivityChart(binding: FragmentTabbedBinding,activities : ArrayList<Int>,totalActivity: Int, mostActiveAnimal: String, mostActive: Int,
                                  leastActiveAnimal: String, leastActive : Int){

        if(activities.isEmpty()){
            binding.activityChart.invalidate()
            return
        }

        val median = activities.median().toFloat()
        val pieEntries = arrayListOf(PieEntry(median))
        val pieChart = binding.activityChart

        pieChart.centerText = "$median"
        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.setColors(Color.parseColor("#006400"), Color.parseColor("#FFFFFF"))
        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(false)
        pieChart.animateXY(1000,1000)
        pieChart.setUsePercentValues(true)
        pieChart.setCenterTextSize(25f)
        pieChart.legend.isEnabled = false
        pieChart.isLongClickable = true
        pieChart.description.isEnabled = false
        pieChart.holeRadius = 85f
        pieChart.data = pieData
        pieChart.invalidate()

        binding.activityChart.setOnChartValueSelectedListener (
            object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry, h: Highlight?) {
                    val view = ChartTabbedBinding.inflate(LayoutInflater.from(context))
                    val chart = view.activityChartTabbed
                    chart.data = pieChart.data
                    chart.holeRadius = 90f
                    chart.setCenterTextSize(10f)
                    chart.legend.isEnabled = false
                    chart.description.isEnabled = false
                    chart.animateXY(1000,1000)
                    chart.centerText = "$median"
                    chart.setCenterTextSize(30f)
                    chart.invalidate()
                    view.leastActive.text = "Least Active Animal: $leastActiveAnimal with $leastActive Activity"
                    view.mostActive.text = "Most Active Animal: $mostActiveAnimal with $mostActive Activity"
                    view.totalActivites.text = "Total Activity Number: ${totalActivity}"

                    val dialog = AlertDialog.Builder(context)
                    dialog.setNeutralButton("Back"){ dialog,which->
                        dialog.dismiss()
                    }

                    dialog.setView(view.root)

                    dialog.show()
                }
                override fun onNothingSelected() {}
            }
        )
    }

    private fun loadWeightChart(binding: FragmentTabbedBinding, weights : ArrayList<Float>,maxAnimalWeight: String,maxWeight : Float,minAnimalWeight : String, minWeight : Float){

        if(weights.isEmpty()){
            binding.weightChart.invalidate()
            return
        }

        val median = weights.median().toFloat()
        val pieEntries = arrayListOf(PieEntry(median))
        val pieChart = binding.weightChart

        pieChart.centerText = "$median"+"Kg"
        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.setColors(Color.BLUE, Color.parseColor("#FFFFFF"))
        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(false)
        pieChart.animateXY(1000,1000)
        pieChart.setCenterTextSize(25f)
        pieChart.legend.isEnabled = false
        pieChart.description.isEnabled = false
        pieChart.holeRadius = 85f
        pieChart.data = pieData
        pieChart.invalidate()

        binding.weightChart.setOnChartValueSelectedListener (
            object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry, h: Highlight?) {
                    val view = ChartTabbedBinding.inflate(LayoutInflater.from(context))
                    val chart = view.activityChartTabbed
                    chart.data = pieChart.data
                    chart.holeRadius = 90f
                    chart.setCenterTextSize(10f)
                    chart.legend.isEnabled = false
                    chart.description.isEnabled = false
                    chart.animateXY(1000,1000)
                    chart.centerText = "$median"+"Kg"
                    chart.setCenterTextSize(30f)
                    chart.invalidate()
                    view.leastActive.text = "Max Animal Weight: $maxAnimalWeight wights $maxWeight"
                    view.mostActive.text = "Min Animal Weight: $minAnimalWeight weights $minWeight"
                    view.totalActivites.text = "Weights Median : ${median}Kg"

                    val dialog = AlertDialog.Builder(context)
                    dialog.setNeutralButton("Back"){ dialog,which->
                        dialog.dismiss()
                    }

                    dialog.setView(view.root)

                    dialog.show()
                }
                override fun onNothingSelected() {}
            }
        )


    }

    private fun loadHeatChart(binding: FragmentTabbedBinding, animal: String){
        val pieChart = binding.heatChart
        val pieEntries = ArrayList<PieEntry>()
        database.reference.child("users/${uid}/Environment").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild("$animal/${currentDate}/${getTime()}")) {
                            val values = snapshot.child("$animal/${currentDate}/${getTime()}")
                            val humidity = values.child("humidity").value.toString().toFloat()
                            val temperature = values.child("temperature").value.toString().toFloat()
                            pieEntries.add(PieEntry(temperature))
                            pieEntries.add(PieEntry(humidity))

                            pieChart.centerText = "$temperature" + "C" +"\n$humidity"+"%"
                            val pieDataSet = PieDataSet(pieEntries, "")
                            pieDataSet.setColors(Color.RED, Color.parseColor("#FFA500"))
                            val pieData = PieData(pieDataSet)
                            pieData.setDrawValues(false)
                            pieData.setValueFormatter(PercentFormatter())
                            pieChart.animateXY(1000, 1000)
                            pieChart.setUsePercentValues(true)
                            pieChart.setCenterTextSize(20f)
                            pieChart.legend.isEnabled = false
                            pieChart.description.isEnabled = false
                            pieChart.holeRadius = 85f
                            pieChart.data = pieData
                        }

                        pieChart.invalidate()
                    }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            }
        )
    }

    private fun getTime(): String{
        if(currentTime > 12){
            return "pm"
        }
        else{
            return "am"
        }
    }
}
