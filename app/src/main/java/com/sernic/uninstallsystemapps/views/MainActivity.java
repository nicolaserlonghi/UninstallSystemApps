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
 * furnished to do so, SUBJECT to the following conditions:
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

import androidx.appcompat.widget.SearchView;
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
    private ArrayList<App> installedAppsShow;
    private ArrayList<App> allInstalledApps;

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
        MenuItem actionSearch = menu.findItem(R.id.app_bar_search);
        manageSearch(actionSearch);
        return super.onCreateOptionsMenu(menu);
    }

    private void manageSearch(MenuItem searchItem) {
        SearchView searchView = (SearchView) searchItem.getActionView();
        manageFabOnSearchItemStatus(searchItem);
        manageInputTextInSearchView(searchView);
    }

    private void manageFabOnSearchItemStatus(MenuItem searchItem) {
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                binding.fab.hide();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                binding.fab.show();
                return true;
            }
        });
    }

    private void manageInputTextInSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(installedAppsShow == null)
                    return false;
                String query = newText.toLowerCase().trim();
                List<App> filteredApps = getViewModel().filterApps(query, installedAppsShow);
                appRecyclerAdapter.updataList((ArrayList<App>) filteredApps);
                return true;
            }
        });
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
            this.allInstalledApps = (ArrayList) installedApps;
            this.installedAppsShow = (ArrayList) installedApps;
            hideAppStoredFlag();
            orderAppInStoredOrder();
            updateRecyclerView();
            stopLoadingAnimation();
        });
    }

    private void hideAppStoredFlag() {
        boolean isHideSystemApps = Prefs.getBoolean(Constants.FLAG_HIDE_SYSTEM_APPS, false);
        boolean isHideUserApps = Prefs.getBoolean(Constants.FLAG_HIDE_USER_APPS, false);
        if(isHideSystemApps && !isHideUserApps) {
            installedAppsShow = (ArrayList<App>) getViewModel().hideSystemApps(allInstalledApps);
        } else if(isHideUserApps && !isHideSystemApps) {
            installedAppsShow = (ArrayList<App>) getViewModel().hideUserApps(allInstalledApps);
        }
    }

    private void orderAppInStoredOrder() {
        boolean isAlphabeticalOrder = Prefs.getBoolean(Constants.FLAG_ALPHABETICAL_ORDER, true);
        boolean isInstallationDateOrder = Prefs.getBoolean(Constants.FLAG_INSTALLATION_DATE, false);
        if(isAlphabeticalOrder && !isInstallationDateOrder) {
            installedAppsShow = (ArrayList<App>) getViewModel().orderAppInAlfabeticalOrder(installedAppsShow);
            allInstalledApps = (ArrayList<App>) getViewModel().orderAppInAlfabeticalOrder(allInstalledApps);
        } else {
            installedAppsShow = (ArrayList<App>) getViewModel().orderAppForInstallationDateDesc(installedAppsShow);
            allInstalledApps = (ArrayList<App>) getViewModel().orderAppForInstallationDateDesc(allInstalledApps);
        }
    }

    private void updateRecyclerView() {
        if(recyclerView == null)
            setRecyclerView();
        else
            appRecyclerAdapter.updataList(installedAppsShow);
    }

    private void setRecyclerView() {
        recyclerView = binding.recyclerView;
        RecyclerView.ItemDecoration divider = getInsetDivider();
        recyclerView.addItemDecoration(divider);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        appRecyclerAdapter = new AppRecyclerAdapter(
                installedAppsShow
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

    @Override
    public void onSelectedAlphabeticalOrder() {
        this.allInstalledApps = (ArrayList<App>) getViewModel().uncheckedAllApps(allInstalledApps);
        this.installedAppsShow = (ArrayList<App>) getViewModel().orderAppInAlfabeticalOrder(installedAppsShow);
        this.allInstalledApps = (ArrayList<App>) getViewModel().orderAppInAlfabeticalOrder(allInstalledApps);
        updateRecyclerView();
    }

    @Override
    public void onSelectInstallationDateOrder() {
        this.allInstalledApps = (ArrayList<App>) getViewModel().uncheckedAllApps(allInstalledApps);
        this.installedAppsShow = (ArrayList<App>) getViewModel().orderAppForInstallationDateDesc(installedAppsShow);
        this.allInstalledApps = (ArrayList<App>) getViewModel().orderAppForInstallationDateDesc(allInstalledApps);
        updateRecyclerView();
    }

    @Override
    public void onSelectedHideSystemApps() {
        this.allInstalledApps = (ArrayList<App>) getViewModel().uncheckedAllApps(allInstalledApps);
        installedAppsShow = (ArrayList<App>) getViewModel().hideSystemApps(allInstalledApps);
        updateRecyclerView();
    }

    @Override
    public void onSelectedHideUserApps() {
        this.allInstalledApps = (ArrayList<App>) getViewModel().uncheckedAllApps(allInstalledApps);
        installedAppsShow = (ArrayList<App>) getViewModel().hideUserApps(allInstalledApps);
        updateRecyclerView();
    }

    @Override
    public void onSelectedShowAllApps() {
        this.allInstalledApps = (ArrayList<App>) getViewModel().uncheckedAllApps(allInstalledApps);
        installedAppsShow = allInstalledApps;
        updateRecyclerView();
    }
}