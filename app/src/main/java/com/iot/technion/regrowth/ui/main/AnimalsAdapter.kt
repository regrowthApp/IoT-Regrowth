package com.iot.technion.regrowth.ui.main

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.iot.technion.regrowth.R
import com.iot.technion.regrowth.MainActivity
import com.iot.technion.regrowth.databinding.AddNodeBinding
import com.iot.technion.regrowth.databinding.FragmentTabbedBinding
import com.iot.technion.regrowth.model.AnimalModel
import com.iot.technion.regrowth.model.NodeModel
import com.faskn.lib.PieChart
import com.faskn.lib.Slice
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aachartcreator.aa_toAAOptions
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAScrollablePlotArea
import com.google.firebase.database.FirebaseDatabase


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class AnimalsAdapter(private val context: Context, private val animalList: ArrayList<AnimalModel>):
    RecyclerView.Adapter<AnimalsAdapter.MyVH>() {
    val TAG = "SectionsPagerAdapter"
    var animals: ArrayList<AnimalModel> = ArrayList<AnimalModel>()
    val  activity:MainActivity = context as MainActivity

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
        if (position< (animals.size-2)) {
            var animal=animals[position]
            holder.binding.root.setBackgroundColor(Color.parseColor(animal.color))
            loadPieCharts(holder.binding)
            setupChart(holder.binding)
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
                    var myRef = database.getReference("users/Pier/${animal.name}/nodes/${node.gatewayId}")
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


    private fun setupChart(binding: FragmentTabbedBinding) {
        val aaChartModel = context.let {
            AAChartModel.Builder(it)
                .setChartType(AAChartType.Bar)
                .setTitle("")
                .setYAxisTitle("")
                .setLegendEnabled(false)
                .setYAxisGridLineWidth(0f)
                .setScrollablePlotArea(
                    AAScrollablePlotArea()
                        .minWidth(3000)
                        .scrollPositionX(1f))
                .setSeries(
                    AASeriesElement()
                        .name("Tokyo")
                        .data(arrayOf(50, 320, 230, 370, 230, 400))
                )
                .build()
        }

        val element1 = AASeriesElement()
            .name("Tokyo")
            .step(true)
            .data(arrayOf(149.9, 171.5, 106.4, 129.2, 144.0, 176.0, 135.6, 188.5, 276.4, 214.1, 95.6, 54.4))

        val element2 = AASeriesElement()
            .name("NewYork")
            .step(true)
            .data(arrayOf(83.6, 78.8, 188.5, 93.4, 106.0, 84.5, 105.0, 104.3, 131.2, 153.5, 226.6, 192.3))

        val element3 = AASeriesElement()
            .name("London")
            .step(true)
            .data(arrayOf(48.9, 38.8, 19.3, 41.4, 47.0, 28.3, 59.0, 69.6, 52.4, 65.2, 53.3, 72.2))

        aaChartModel.series(arrayOf(element1, element2, element3))

        aaChartModel.aa_toAAOptions()
            .let { binding.animalsChart.aa_drawChartWithChartOptions(it) }

    }

    private fun loadPieCharts(binding: FragmentTabbedBinding) {
        val activityChart = PieChart(
            slices = provideSlices("ACTIVITY"), clickListener = null, sliceStartPoint = 30f, sliceWidth = 30f
        ).build()

        val heatChart = PieChart(
            slices = provideSlices("HEAT"), clickListener = null, sliceStartPoint = 30f, sliceWidth = 30f
        ).build()

        val weightChart = PieChart(
            slices = provideSlices("WEIGHT"), clickListener = null, sliceStartPoint = 30f, sliceWidth = 30f
        ).build()

        binding.activityChart.setPieChart(activityChart)
        binding.activityChart.showLegend(binding.root)

        binding.heatChart.setPieChart(heatChart)
        binding.heatChart.showLegend(binding.root)

        binding.weightChart.setPieChart(weightChart)
        binding.weightChart.showLegend(binding.root)
    }

    private fun provideSlices(chartType : String): ArrayList<Slice> {
        val x = 70
        val y = 30

        when(chartType){
            "ACTIVITY" -> return arrayListOf(
                Slice(
                    y.toFloat(),
                    R.color.white,
                    ""
                ),
                Slice(
                    x.toFloat(),
                    R.color.bright_orange,
                    ""
                ),
            )

            "HEAT" -> return arrayListOf(
                Slice(
                    x.toFloat(),
                    R.color.bright_red,
                    ""
                )
            )

            "WEIGHT" -> return arrayListOf(
                Slice(
                    x.toFloat(),
                    R.color.bright_green,
                    ""
                )
            )
            else ->{
                activity?.finish()
            }
        }

        return arrayListOf()
    }
}