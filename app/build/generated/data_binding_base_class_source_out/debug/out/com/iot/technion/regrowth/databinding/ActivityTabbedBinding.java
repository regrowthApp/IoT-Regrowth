// Generated by view binder compiler. Do not edit!
package com.iot.technion.regrowth.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.iot.technion.regrowth.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityTabbedBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final AppBarLayout appBarLayout;

  @NonNull
  public final FloatingActionButton fab;

  @NonNull
  public final TabLayout tabs;

  @NonNull
  public final BottomNavigationView topNaviBar;

  @NonNull
  public final ViewPager2 viewPager;

  private ActivityTabbedBinding(@NonNull ConstraintLayout rootView,
      @NonNull AppBarLayout appBarLayout, @NonNull FloatingActionButton fab,
      @NonNull TabLayout tabs, @NonNull BottomNavigationView topNaviBar,
      @NonNull ViewPager2 viewPager) {
    this.rootView = rootView;
    this.appBarLayout = appBarLayout;
    this.fab = fab;
    this.tabs = tabs;
    this.topNaviBar = topNaviBar;
    this.viewPager = viewPager;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityTabbedBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityTabbedBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_tabbed, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityTabbedBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.appBarLayout;
      AppBarLayout appBarLayout = ViewBindings.findChildViewById(rootView, id);
      if (appBarLayout == null) {
        break missingId;
      }

      id = R.id.fab;
      FloatingActionButton fab = ViewBindings.findChildViewById(rootView, id);
      if (fab == null) {
        break missingId;
      }

      id = R.id.tabs;
      TabLayout tabs = ViewBindings.findChildViewById(rootView, id);
      if (tabs == null) {
        break missingId;
      }

      id = R.id.top_navi_bar;
      BottomNavigationView topNaviBar = ViewBindings.findChildViewById(rootView, id);
      if (topNaviBar == null) {
        break missingId;
      }

      id = R.id.view_pager;
      ViewPager2 viewPager = ViewBindings.findChildViewById(rootView, id);
      if (viewPager == null) {
        break missingId;
      }

      return new ActivityTabbedBinding((ConstraintLayout) rootView, appBarLayout, fab, tabs,
          topNaviBar, viewPager);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}