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

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.pixplicity.easyprefs.library.Prefs;
import com.sernic.uninstallsystemapps.Constants;
import com.sernic.uninstallsystemapps.R;
import com.sernic.uninstallsystemapps.adapters.AppRecyclerAdapter;
import com.sernic.uninstallsystemapps.adapters.ControllerAppsSelected;
import com.sernic.uninstallsystemapps.databinding.ActivityMainBinding;
import com.sernic.uninstallsystemapps.models.App;
import com.sernic.uninstallsystemapps.helpers.InsetDivider;
import com.sernic.uninstallsystemapps.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements BottomSheetFragment.IsSelectedBottomSheetFragment {

    private MainViewModel mainViewModel;
    private ActivityMainBinding binding;
    private RecyclerView recyclerView;
    private AppRecyclerAdapter appRecyclerAdapter;
    private ArrayList<App> installedApps;
    private List<App> selectedApps = new ArrayList<>();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottomappbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_settings:
                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                bottomSheetFragment.show(getSupportFragmentManager(), "TAG");
                break;
            case android.R.id.home:
                NavigationDrawerFragment navigationDrawerFragment = new NavigationDrawerFragment();
                navigationDrawerFragment.show(getSupportFragmentManager(), "TAG");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startLoadingAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getViewModel().getInstalledApps().observe(this, installedApps -> {
            if(installedApps == null)
                return;
            this.installedApps = (ArrayList) installedApps;
            orderAppInStoredOrder();
            updateRecyclerView();
            stopLoadingAnimation();
        });
    }

    private void orderAppInStoredOrder() {
        boolean isAlphabeticalOrder = Prefs.getBoolean(Constants.flag_alphabetical_order, true);
        boolean isInstallationDateOrder = Prefs.getBoolean(Constants.flag_installation_date, false);
        if(isAlphabeticalOrder && !isInstallationDateOrder) {
            installedApps = (ArrayList<App>) getViewModel().orderAppInAlfabeticalOrder(installedApps);
        } else {
            installedApps  = (ArrayList<App>) getViewModel().orderAppForInstallationDateDesc(installedApps);
        }
    }

    private void updateRecyclerView() {
        if(recyclerView == null)
            setRecyclerView();
        else
            appRecyclerAdapter.updataList(installedApps);
    }

    private void setRecyclerView() {
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

    private void startLoadingAnimation() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
    }

    private void stopLoadingAnimation() {
        binding.progressCircular.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
    }

    // Called by AppRecyclerAdapter when an item is selected
    public void isSelectedApp(App app) {
        boolean appAlreadySelected = appIsAlreadySelected(app);
        if(!appAlreadySelected)
            selectedApps.add(app);
    }

    private boolean appIsAlreadySelected(App app) {
        boolean alreadySelected = selectedApps.contains(app);
        return alreadySelected;
    }

    // Called by AppRecyclerAdapter when an item is deselected
    public void isDeselectedApp(App app) {
        selectedApps.remove(app);
    }

    @Override
    public void onSelectedAlphabeticalOrder() {
        this.installedApps = (ArrayList<App>) getViewModel().orderAppInAlfabeticalOrder(installedApps);
        updateRecyclerView();
    }

    @Override
    public void onSelectInstallationDateOrder() {
        this.installedApps = (ArrayList<App>) getViewModel().orderAppForInstallationDateDesc(installedApps);
        updateRecyclerView();
    }
}