/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Nicola Serlonghi <nicolaserlonghi@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sernic.uninstallsystemapps.views;

import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.sernic.uninstallsystemapps.R;
import com.sernic.uninstallsystemapps.adapters.AppRecyclerAdapter;
import com.sernic.uninstallsystemapps.adapters.ControllerAppsSelected;
import com.sernic.uninstallsystemapps.databinding.ActivityMainBinding;
import com.sernic.uninstallsystemapps.models.App;
import com.sernic.uninstallsystemapps.helpers.InsetDivider;
import com.sernic.uninstallsystemapps.viewmodels.MainViewModel;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private MainViewModel mainViewModel;
    private ActivityMainBinding binding;
    private RecyclerView recyclerView;
    private AppRecyclerAdapter appRecyclerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setBottomAppBar() {
        BottomAppBar bottomAppBar = binding.bar;
        setSupportActionBar(bottomAppBar);
    }

    @Override
    protected MainViewModel getViewModel() {
        if(mainViewModel == null) {
            Application application = getApplication();
            MainViewModel.Factory factory = new MainViewModel.Factory(application);
            mainViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
        }
        return mainViewModel;
    }

    @Override
    protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getViewModel().getInstalledApps(getApplicationContext()).observe(this, installedApps -> {
            if(installedApps == null)
                return;
            updateRecyclerView((ArrayList<App>) installedApps);
        });
    }

    private void updateRecyclerView(ArrayList<App> installedApps) {
        if(recyclerView == null)
            setRecyclerView(installedApps);
        else
            appRecyclerAdapter.updataList(installedApps);
    }

    private void setRecyclerView(ArrayList<App> installedApps) {
        recyclerView = binding.recyclerView;
        RecyclerView.ItemDecoration divider = getInsetDivider();
        recyclerView.addItemDecoration(divider);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        appRecyclerAdapter = new AppRecyclerAdapter(
                new ControllerAppsSelected(this),
                installedApps
        );
        recyclerView.setAdapter(appRecyclerAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
    }

    private RecyclerView.ItemDecoration getInsetDivider() {
        int dividerHeight = getResources().getDimensionPixelSize(R.dimen.divider_height);
        int dividerColor = getResources().getColor(R.color.divider);
        int marginLeft = getResources().getDimensionPixelSize(R.dimen.divider_inset);
        RecyclerView.ItemDecoration insetDivider = new InsetDivider.Builder(this)
                .orientation(InsetDivider.VERTICAL_LIST)
                .dividerHeight(dividerHeight)
                .color(dividerColor)
                .insets(marginLeft, 0)
                .build();
        return insetDivider;
    }

    // Called by AppRecyclerAdapter when an item is selected
    public void itemSelected(int appPosition, Boolean isChecked) {
    }
}