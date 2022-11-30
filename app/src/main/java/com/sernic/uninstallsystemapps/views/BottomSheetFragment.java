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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pixplicity.easyprefs.library.Prefs;
import com.sernic.uninstallsystemapps.Constants;
import com.sernic.uninstallsystemapps.Logger;
import com.sernic.uninstallsystemapps.R;
import com.sernic.uninstallsystemapps.databinding.FragmentBottomSheetBinding;
import com.sernic.uninstallsystemapps.helpers.CustomAlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;

public class BottomSheetFragment extends com.google.android.material.bottomsheet.BottomSheetDialogFragment
        implements View.OnClickListener {

    private FragmentBottomSheetBinding binding;
    private IsSelectedBottomSheetFragment isSelectedBottomSheetFragment;

    public interface IsSelectedBottomSheetFragment {
        void onSelectedAlphabeticalOrder();
        void onSelectInstallationDateOrder();
        void onSelectedHideSystemApps();
        void onSelectedHideUserApps();
        void onSelectedShowAllApps();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            isSelectedBottomSheetFragment = (IsSelectedBottomSheetFragment) getActivity();
        }
        catch (ClassCastException e) {
            Logger.d(BottomSheetFragment.class.getSimpleName(), "Activity doesn't implement all interface method");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        //Set the custom view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_bottom_sheet, null);
        dialog.setContentView(view);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readCheckedStored();
        setSavedAppTheme();
        setOnclickListener();
    }

    private void readCheckedStored() {
        boolean storedStatusAlphabeticalOrder = Prefs.getBoolean(Constants.FLAG_ALPHABETICAL_ORDER, true);
        boolean storedStatusInstallationDate = Prefs.getBoolean(Constants.FLAG_INSTALLATION_DATE, false);
        boolean storedStatusHideSystemApps = Prefs.getBoolean(Constants.FLAG_HIDE_SYSTEM_APPS, false);
        boolean storedStatusHideUserApps = Prefs.getBoolean(Constants.FLAG_HIDE_USER_APPS, false);
        setStateStoredOfcheckedAlphabeticalOrder(storedStatusAlphabeticalOrder);
        setStateStoredOfcheckedInstallationDate(storedStatusInstallationDate);
        setStateStoredOfHideSystemApps(storedStatusHideSystemApps);
        setStateStoredOfHideUserApps(storedStatusHideUserApps);
    }

    private void setStateStoredOfcheckedAlphabeticalOrder(boolean status) {
        if(status)
            binding.checkedAlphabeticalOrder.setVisibility(View.VISIBLE);
        else
            binding.checkedAlphabeticalOrder.setVisibility(View.GONE);
    }

    private void setStateStoredOfcheckedInstallationDate(boolean status) {
        if(status)
            binding.checkedInstallationDate.setVisibility(View.VISIBLE);
        else
            binding.checkedInstallationDate.setVisibility(View.GONE);
    }

    private void setStateStoredOfHideSystemApps(boolean status) {
        if(status)
            binding.checkedSystemApps.setVisibility(View.VISIBLE);
        else
            binding.checkedSystemApps.setVisibility(View.GONE);
    }

    private void setStateStoredOfHideUserApps(boolean status) {
        if(status)
            binding.checkedUserApps.setVisibility(View.VISIBLE);
        else
            binding.checkedUserApps.setVisibility(View.GONE);
    }

    private void setSavedAppTheme() {
        int index = getIndexOfSavingNightMode();
        String[] appThemeOptions =  getResources().getStringArray(R.array.alert_dialog_app_theme_options);
        binding.subtitleAppTheme.setText(appThemeOptions[index]);
    }

    private void setOnclickListener() {
        binding.alphabeticalOrder.setOnClickListener(this);
        binding.installationDate.setOnClickListener(this);
        binding.systemApps.setOnClickListener(this);
        binding.userApps.setOnClickListener(this);
        binding.chooseAppTheme.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.alphabetical_order:
                manageClickAppOrder(binding.checkedAlphabeticalOrder, binding.checkedInstallationDate);
                storedAlphabeticalOrderClicked();
                isSelectedBottomSheetFragment.onSelectedAlphabeticalOrder();
                dismiss();
                break;
            case R.id.installation_date:
                manageClickAppOrder(binding.checkedInstallationDate, binding.checkedAlphabeticalOrder);
                storedInstallationDateClicked();
                isSelectedBottomSheetFragment.onSelectInstallationDateOrder();
                dismiss();
                break;
            case R.id.system_apps:
                hideSystemAppsClicked();
                dismiss();
                break;
            case R.id.user_apps:
                hideUserAppsClicked();
                dismiss();
                break;
            case R.id.choose_app_theme:
                manageClickChooseAppTheme();
                dismiss();
                break;
        }
    }

    private void manageClickAppOrder(ImageView clicked, ImageView alreadyClicked) {
        if(clicked.getVisibility() == View.VISIBLE)
            return;
        clicked.setVisibility(View.VISIBLE);
        alreadyClicked.setVisibility(View.GONE);
    }

    private void storedAlphabeticalOrderClicked() {
        Prefs.putBoolean(Constants.FLAG_ALPHABETICAL_ORDER, true);
        Prefs.putBoolean(Constants.FLAG_INSTALLATION_DATE, false);
    }

    private void storedInstallationDateClicked() {
        Prefs.putBoolean(Constants.FLAG_ALPHABETICAL_ORDER, false);
        Prefs.putBoolean(Constants.FLAG_INSTALLATION_DATE, true);
    }

    private void hideSystemAppsClicked() {
        ImageView systemApps = binding.checkedSystemApps;
        ImageView userApps = binding.checkedUserApps;
        if(systemApps.getVisibility() == View.VISIBLE) {
            systemApps.setVisibility(View.GONE);
            Prefs.putBoolean(Constants.FLAG_HIDE_SYSTEM_APPS, false);
            isSelectedBottomSheetFragment.onSelectedShowAllApps();
        } else {
            systemApps.setVisibility(View.VISIBLE);
            userApps.setVisibility(View.GONE);
            Prefs.putBoolean(Constants.FLAG_HIDE_SYSTEM_APPS, true);
            Prefs.putBoolean(Constants.FLAG_HIDE_USER_APPS, false);
            isSelectedBottomSheetFragment.onSelectedHideSystemApps();
        }
    }

    private void hideUserAppsClicked() {
        ImageView systemApps = binding.checkedSystemApps;
        ImageView userApps = binding.checkedUserApps;
        if(userApps.getVisibility() == View.VISIBLE) {
            userApps.setVisibility(View.GONE);
            Prefs.putBoolean(Constants.FLAG_HIDE_USER_APPS, false);
            isSelectedBottomSheetFragment.onSelectedShowAllApps();
        } else {
            userApps.setVisibility(View.VISIBLE);
            systemApps.setVisibility(View.GONE);
            Prefs.putBoolean(Constants.FLAG_HIDE_USER_APPS, true);
            Prefs.putBoolean(Constants.FLAG_HIDE_SYSTEM_APPS, false);
            isSelectedBottomSheetFragment.onSelectedHideUserApps();
        }
    }

    private void manageClickChooseAppTheme() {
        String title = getResources().getString(R.string.alert_dialog_app_theme_title);
        String[] appThemeOptions =  getResources().getStringArray(R.array.alert_dialog_app_theme_options);
        int itemSelected = getIndexOfSavingNightMode();
        DialogInterface.OnClickListener onClickListener = (dialogInterface, i) -> {
            setAppThemeFromIndex(i);
            dialogInterface.dismiss();
        };

        CustomAlertDialog.showAlertDialogWithRadio(
                getContext(),
                title,
                appThemeOptions,
                itemSelected,
                onClickListener
        );
    }

    private int getIndexOfSavingNightMode() {
        int itemSelected = Prefs.getInt(Constants.NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        switch (itemSelected) {
            case AppCompatDelegate.MODE_NIGHT_YES:
                return 1;
            case AppCompatDelegate.MODE_NIGHT_NO:
                return 0;
            default:
                return 2;
        }
    }

    private void setAppThemeFromIndex(int index) {
        int themeToSet;
        switch (index) {
            case 0:
                themeToSet = AppCompatDelegate.MODE_NIGHT_NO;
                break;
            case 1:
                themeToSet = AppCompatDelegate.MODE_NIGHT_YES;
                break;
            default:
                themeToSet = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
        AppCompatDelegate.setDefaultNightMode(themeToSet);
        savingNighMode(themeToSet);
    }

    private void savingNighMode(int mode) {
        Prefs.putInt(Constants.NIGHT_MODE, mode);
    }
}
