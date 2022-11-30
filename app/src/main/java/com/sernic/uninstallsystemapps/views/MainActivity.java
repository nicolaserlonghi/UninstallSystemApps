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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Application;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.pixplicity.easyprefs.library.Prefs;
import com.sernic.uninstallsystemapps.Constants;
import com.sernic.uninstallsystemapps.DataRepository;
import com.sernic.uninstallsystemapps.R;
import com.sernic.uninstallsystemapps.UninstallSystemApps;
import com.sernic.uninstallsystemapps.adapters.AppRecyclerAdapter;
import com.sernic.uninstallsystemapps.databinding.ActivityMainBinding;
import com.sernic.uninstallsystemapps.helpers.CustomAlertDialog;
import com.sernic.uninstallsystemapps.helpers.SingleLiveEvent;
import com.sernic.uninstallsystemapps.models.App;
import com.sernic.uninstallsystemapps.helpers.InsetDivider;
import com.sernic.uninstallsystemapps.models.RootState;
import com.sernic.uninstallsystemapps.services.RootManager;
import com.sernic.uninstallsystemapps.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        BottomSheetFragment.IsSelectedBottomSheetFragment {

    private MainViewModel mainViewModel;
    private ActivityMainBinding binding;
    private RecyclerView recyclerView;
    private AppRecyclerAdapter appRecyclerAdapter;
    private ArrayList<App> installedApps;

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
            DataRepository dataRepository = ((UninstallSystemApps)application).getDataRepository();
            RootManager rootManager = dataRepository.getRootManager();
            MainViewModel.Factory factory = new MainViewModel.Factory(application, rootManager);
            mainViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
        }
        return mainViewModel;
    }

    @Override
    protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                removeAppClick();
                break;
        }
    }

    private void removeAppClick() {
        RootState rootState = checkRootState();
        if(rootState == RootState.HAVE_ROOT)
            checkAppsAreSelected();
    }

    private void checkAppsAreSelected() {
        boolean anAppIsSelected = getViewModel().atLeastAnAppIsSelected(installedApps);
        if(anAppIsSelected)
            askPermissionToUninstallSelectedApps();
        else
            CustomAlertDialog.showAlertDialogWithOneButton(
                    this,
                    getResources().getString(R.string.alert_dialog_no_app_selected_title),
                    getResources().getString(R.string.alert_dialog_no_app_selected_message),
                    getResources().getString(R.string.button_ok),
                    null
            );
    }

    private void askPermissionToUninstallSelectedApps() {
        DialogInterface.OnClickListener positiveListener = (dialog, which) -> startUninstallProcess();
        CustomAlertDialog.showAlertDialogWithTwoButton(
                this,
                getResources().getString(R.string.alert_dialog_ask_permission_to_remove_apps_title),
                getResources().getString(R.string.alert_dialog_ask_permission_to_remove_apps_message),
                getResources().getString(R.string.button_yes),
                positiveListener,
                getResources().getString(R.string.button_no),
                null
        );
    }

    private void startUninstallProcess() {
        AlertDialog uninstallAnimation = CustomAlertDialog.showProgressDialog(
                this,
                getResources().getString(R.string.progress_dialog_removing_apps)
        );
        SingleLiveEvent<Boolean> uninstallResult = getViewModel().removeApps(installedApps);
        uninstallResult.observe(this, uninstall -> {
            if(uninstall == null)
                return;
            stopUninstallProcess(uninstall, uninstallAnimation);
        });
    }

    private void stopUninstallProcess(Boolean uninstallResult, AlertDialog uninstallAnimation) {
        CustomAlertDialog.stopProgressDialog(uninstallAnimation);
        binding.swipeRefresh.setRefreshing(true);
        onRefresh();
        if(uninstallResult)
            uninstallSuccessful();
        else
            uninstallError();

    }

    private void uninstallSuccessful() {
        DialogInterface.OnClickListener rebootButton = (dialog, which) -> getViewModel().rebootDevice();
        CustomAlertDialog.showAlertDialogWithTwoButton(
                this,
                getResources().getString(R.string.alert_dialog_title_reboot_now),
                getResources().getString(R.string.alert_dialog_message_reboot_now),
                getResources().getString(R.string.button_reboot),
                rebootButton,
                getResources().getString(R.string.button_no),
                null
        );
    }

    private void uninstallError() {
        CustomAlertDialog.showAlertDialogWithOneButton(
                this,
                getResources().getString(R.string.alert_dialog_title_error_remove_apps),
                getResources().getString(R.string.alert_dialog_message_error_remove_apps),
                getResources().getString(R.string.button_ok),
                null
        );
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
                if(installedApps == null)
                    return false;
                String query = newText.toLowerCase().trim();
                List<App> filteredApps = getViewModel().filterApps(query, installedApps);
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
        setOnclickListener();
        setOnRefreshListener();
        startLoadingAnimation();
    }

    private void setOnclickListener() {
        binding.fab.setOnClickListener(this);
    }

    private void setOnRefreshListener() {
        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.progressCircularBackground));
        binding.swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.accent));
        binding.swipeRefresh.setOnRefreshListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getViewModel().getInstalledApps().observe(this, installedApps -> {
            if(installedApps == null)
                return;
            this.installedApps = (ArrayList) installedApps;
            hideAppStoredFlag();
            orderAppInStoredOrder();
            updateRecyclerView();
            stopLoadingAnimation();
            binding.swipeRefresh.setRefreshing(false);
        });
        checkRootState();
    }

    private void hideAppStoredFlag() {
        boolean isHideSystemApps = Prefs.getBoolean(Constants.FLAG_HIDE_SYSTEM_APPS, false);
        boolean isHideUserApps = Prefs.getBoolean(Constants.FLAG_HIDE_USER_APPS, false);
        if(isHideSystemApps && !isHideUserApps) {
            this.installedApps = (ArrayList<App>) getViewModel().hideSystemApps(installedApps);
        } else if(isHideUserApps && !isHideSystemApps) {
            this.installedApps = (ArrayList<App>) getViewModel().hideUserApps(installedApps);
        }
    }

    private void orderAppInStoredOrder() {
        boolean isAlphabeticalOrder = Prefs.getBoolean(Constants.FLAG_ALPHABETICAL_ORDER, true);
        boolean isInstallationDateOrder = Prefs.getBoolean(Constants.FLAG_INSTALLATION_DATE, false);
        if(isAlphabeticalOrder && !isInstallationDateOrder) {
            installedApps = (ArrayList<App>) getViewModel().orderAppInAlfabeticalOrder(installedApps);
        } else {
            installedApps = (ArrayList<App>) getViewModel().orderAppForInstallationDateDesc(installedApps);
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
        appRecyclerAdapter = new AppRecyclerAdapter(installedApps);
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

    private RootState checkRootState() {
        RootState rootState = getViewModel().checkRootPermission();
        switch (rootState) {
            case NO_ROOT:
                setRootAccessAlreadyObtained(false);
                generateRootStateAlertDialog(
                        getResources().getString(R.string.alert_dialog_title_no_root_permission),
                        getResources().getString(R.string.alert_dialog_message_no_root_permission)
                );
                break;
            case BE_ROOT:
                setRootAccessAlreadyObtained(false);
                generateRootStateAlertDialog(
                        getResources().getString(R.string.alert_dialog_title_be_root),
                        getResources().getString(R.string.alert_dialog_message_be_root)
                );
                break;
            case HAVE_ROOT:
                boolean rootAccessAlreadyObtained = isRootAccessAlreadyObtained();
                if(rootAccessAlreadyObtained)
                    break;
                setRootAccessAlreadyObtained(true);
                generateRootStateAlertDialog(
                        getResources().getString(R.string.alert_dialog_title_have_root),
                        getResources().getString(R.string.alert_dialog_message_have_root)
                );
                break;
        }
        return rootState;
    }

    private void setRootAccessAlreadyObtained(boolean status) {
        String key = Constants.FLAG_ROOT_ACCESS_ALREADY_OBTAINED;
        Prefs.putBoolean(key, status);
    }

    private boolean isRootAccessAlreadyObtained() {
        String key = Constants.FLAG_ROOT_ACCESS_ALREADY_OBTAINED;
        boolean rootAccessAlreadyObtained = Prefs.getBoolean(key, false);
        return rootAccessAlreadyObtained;
    }

    private void generateRootStateAlertDialog(String title, String message) {
        CustomAlertDialog.showAlertDialogWithOneButton(
                this,
                title,
                message,
                getResources().getString(R.string.button_ok),
                null
        );
    }

    @Override
    public void onRefresh() {
        getViewModel().reloadAppsList();
    }


    @Override
    public void onSelectedAlphabeticalOrder() {
        installedApps = (ArrayList<App>) getViewModel().orderAppInAlfabeticalOrder(installedApps);
        updateRecyclerView();
    }

    @Override
    public void onSelectInstallationDateOrder() {
        installedApps = (ArrayList<App>) getViewModel().orderAppForInstallationDateDesc(installedApps);
        updateRecyclerView();
    }

    @Override
    public void onSelectedHideSystemApps() {
        installedApps = (ArrayList<App>) getViewModel().uncheckedAllApps(installedApps);
        installedApps = (ArrayList<App>) getViewModel().hideSystemApps(installedApps);
        updateRecyclerView();
    }

    @Override
    public void onSelectedHideUserApps() {
        installedApps = (ArrayList<App>) getViewModel().uncheckedAllApps(installedApps);
        installedApps = (ArrayList<App>) getViewModel().hideUserApps(installedApps);
        updateRecyclerView();
    }

    @Override
    public void onSelectedShowAllApps() {
        installedApps = (ArrayList<App>) getViewModel().uncheckedAllApps(installedApps);
        installedApps = (ArrayList<App>) getViewModel().showAllApps(installedApps);
        updateRecyclerView();
    }

    public ArrayList<App> getInstalledApps() {
        return installedApps;
    }

    public void setInstalledApps(ArrayList<App> installedApps) {
        this.installedApps = installedApps;
        hideAppStoredFlag();
        orderAppInStoredOrder();
    }
}