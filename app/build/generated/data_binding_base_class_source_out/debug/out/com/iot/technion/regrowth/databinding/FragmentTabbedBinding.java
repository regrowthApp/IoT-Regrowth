// Generated by view binder compiler. Do not edit!
package com.iot.technion.regrowth.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.button.MaterialButton;
import com.iot.technion.regrowth.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentTabbedBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final TextView Activity;

  @NonNull
  public final TextView HeatStress;

  @NonNull
  public final TextView Weight;

  @NonNull
  public final PieChart activityChart;

  @NonNull
  public final BarChart animalsChart;

  @NonNull
  public final RecyclerView gatewayRecyclerView;

  @NonNull
  public final PieChart heatChart;

  @NonNull
  public final Guideline horizontalLine0;

  @NonNull
  public final Guideline horizontalLine1;

  @NonNull
  public final Guideline horizontalLine11;

  @NonNull
  public final Guideline horizontalLine3;

  @NonNull
  public final Guideline horizontalLine65;

  @NonNull
  public final Guideline horizontalLine7;

  @NonNull
  public final Guideline horizontalLine9;

  @NonNull
  public final ConstraintLayout mainLayout;

  @NonNull
  public final MaterialButton nodeBtn;

  @NonNull
  public final ImageView nodesImg;

  @NonNull
  public final RecyclerView nodesRecyclerView;

  @NonNull
  public final Guideline verticalLine15;

  @NonNull
  public final Guideline verticalLine3;

  @NonNull
  public final Guideline verticalLine6;

  @NonNull
  public final Guideline verticalLine7;

  @NonNull
  public final Guideline verticalLine9;

  @NonNull
  public final PieChart weightChart;

  private FragmentTabbedBinding(@NonNull ScrollView rootView, @NonNull TextView Activity,
      @NonNull TextView HeatStress, @NonNull TextView Weight, @NonNull PieChart activityChart,
      @NonNull BarChart animalsChart, @NonNull RecyclerView gatewayRecyclerView,
      @NonNull PieChart heatChart, @NonNull Guideline horizontalLine0,
      @NonNull Guideline horizontalLine1, @NonNull Guideline horizontalLine11,
      @NonNull Guideline horizontalLine3, @NonNull Guideline horizontalLine65,
      @NonNull Guideline horizontalLine7, @NonNull Guideline horizontalLine9,
      @NonNull ConstraintLayout mainLayout, @NonNull MaterialButton nodeBtn,
      @NonNull ImageView nodesImg, @NonNull RecyclerView nodesRecyclerView,
      @NonNull Guideline verticalLine15, @NonNull Guideline verticalLine3,
      @NonNull Guideline verticalLine6, @NonNull Guideline verticalLine7,
      @NonNull Guideline verticalLine9, @NonNull PieChart weightChart) {
    this.rootView = rootView;
    this.Activity = Activity;
    this.HeatStress = HeatStress;
    this.Weight = Weight;
    this.activityChart = activityChart;
    this.animalsChart = animalsChart;
    this.gatewayRecyclerView = gatewayRecyclerView;
    this.heatChart = heatChart;
    this.horizontalLine0 = horizontalLine0;
    this.horizontalLine1 = horizontalLine1;
    this.horizontalLine11 = horizontalLine11;
    this.horizontalLine3 = horizontalLine3;
    this.horizontalLine65 = horizontalLine65;
    this.horizontalLine7 = horizontalLine7;
    this.horizontalLine9 = horizontalLine9;
    this.mainLayout = mainLayout;
    this.nodeBtn = nodeBtn;
    this.nodesImg = nodesImg;
    this.nodesRecyclerView = nodesRecyclerView;
    this.verticalLine15 = verticalLine15;
    this.verticalLine3 = verticalLine3;
    this.verticalLine6 = verticalLine6;
    this.verticalLine7 = verticalLine7;
    this.verticalLine9 = verticalLine9;
    this.weightChart = weightChart;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentTabbedBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentTabbedBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_tabbed, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentTabbedBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.Activity;
      TextView Activity = ViewBindings.findChildViewById(rootView, id);
      if (Activity == null) {
        break missingId;
      }

      id = R.id.Heat_stress;
      TextView HeatStress = ViewBindings.findChildViewById(rootView, id);
      if (HeatStress == null) {
        break missingId;
      }

      id = R.id.Weight;
      TextView Weight = ViewBindings.findChildViewById(rootView, id);
      if (Weight == null) {
        break missingId;
      }

      id = R.id.activity_chart;
      PieChart activityChart = ViewBindings.findChildViewById(rootView, id);
      if (activityChart == null) {
        break missingId;
      }

      id = R.id.animals_chart;
      BarChart animalsChart = ViewBindings.findChildViewById(rootView, id);
      if (animalsChart == null) {
        break missingId;
      }

      id = R.id.gateway_recycler_view;
      RecyclerView gatewayRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (gatewayRecyclerView == null) {
        break missingId;
      }

      id = R.id.heat_chart;
      PieChart heatChart = ViewBindings.findChildViewById(rootView, id);
      if (heatChart == null) {
        break missingId;
      }

      id = R.id.horizontal_line0;
      Guideline horizontalLine0 = ViewBindings.findChildViewById(rootView, id);
      if (horizontalLine0 == null) {
        break missingId;
      }

      id = R.id.horizontal_line_1;
      Guideline horizontalLine1 = ViewBindings.findChildViewById(rootView, id);
      if (horizontalLine1 == null) {
        break missingId;
      }

      id = R.id.horizontal_line1;
      Guideline horizontalLine11 = ViewBindings.findChildViewById(rootView, id);
      if (horizontalLine11 == null) {
        break missingId;
      }

      id = R.id.horizontal_line_3;
      Guideline horizontalLine3 = ViewBindings.findChildViewById(rootView, id);
      if (horizontalLine3 == null) {
        break missingId;
      }

      id = R.id.horizontal_line_65;
      Guideline horizontalLine65 = ViewBindings.findChildViewById(rootView, id);
      if (horizontalLine65 == null) {
        break missingId;
      }

      id = R.id.horizontal_line_7;
      Guideline horizontalLine7 = ViewBindings.findChildViewById(rootView, id);
      if (horizontalLine7 == null) {
        break missingId;
      }

      id = R.id.horizontal_line_9;
      Guideline horizontalLine9 = ViewBindings.findChildViewById(rootView, id);
      if (horizontalLine9 == null) {
        break missingId;
      }

      id = R.id.main_layout;
      ConstraintLayout mainLayout = ViewBindings.findChildViewById(rootView, id);
      if (mainLayout == null) {
        break missingId;
      }

      id = R.id.node_btn;
      MaterialButton nodeBtn = ViewBindings.findChildViewById(rootView, id);
      if (nodeBtn == null) {
        break missingId;
      }

      id = R.id.nodes_img;
      ImageView nodesImg = ViewBindings.findChildViewById(rootView, id);
      if (nodesImg == null) {
        break missingId;
      }

      id = R.id.nodes_recycler_view;
      RecyclerView nodesRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (nodesRecyclerView == null) {
        break missingId;
      }

      id = R.id.vertical_line_15;
      Guideline verticalLine15 = ViewBindings.findChildViewById(rootView, id);
      if (verticalLine15 == null) {
        break missingId;
      }

      id = R.id.vertical_line_3;
      Guideline verticalLine3 = ViewBindings.findChildViewById(rootView, id);
      if (verticalLine3 == null) {
        break missingId;
      }

      id = R.id.vertical_line_6;
      Guideline verticalLine6 = ViewBindings.findChildViewById(rootView, id);
      if (verticalLine6 == null) {
        break missingId;
      }

      id = R.id.vertical_line_7;
      Guideline verticalLine7 = ViewBindings.findChildViewById(rootView, id);
      if (verticalLine7 == null) {
        break missingId;
      }

      id = R.id.vertical_line_9;
      Guideline verticalLine9 = ViewBindings.findChildViewById(rootView, id);
      if (verticalLine9 == null) {
        break missingId;
      }

      id = R.id.weight_chart;
      PieChart weightChart = ViewBindings.findChildViewById(rootView, id);
      if (weightChart == null) {
        break missingId;
      }

      return new FragmentTabbedBinding((ScrollView) rootView, Activity, HeatStress, Weight,
          activityChart, animalsChart, gatewayRecyclerView, heatChart, horizontalLine0,
          horizontalLine1, horizontalLine11, horizontalLine3, horizontalLine65, horizontalLine7,
          horizontalLine9, mainLayout, nodeBtn, nodesImg, nodesRecyclerView, verticalLine15,
          verticalLine3, verticalLine6, verticalLine7, verticalLine9, weightChart);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
