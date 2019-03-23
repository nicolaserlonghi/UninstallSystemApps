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

import android.app.Dialog;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

public class BottomSheetFragment extends com.google.android.material.bottomsheet.BottomSheetDialogFragment
        implements View.OnClickListener {

    private FragmentBottomSheetBinding binding;
    private IsSelectedBottomSheetFragment isSelectedBottomSheetFragment;

    public interface IsSelectedBottomSheetFragment {
        void onSelectedAlphabeticalOrder();
        void onSelectInstallationDateOrder();
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
        setOnclickListener();
    }

    private void readCheckedStored() {
        boolean storedStatusAlphabeticalOrder = Prefs.getBoolean(Constants.flag_alphabetical_order, true);
        boolean storedStatusInstallationDate = Prefs.getBoolean(Constants.flag_installation_date, false);
        setStateStoredOfcheckedAlphabeticalOrder(storedStatusAlphabeticalOrder);
        setStateStoredOfcheckedInstallationDate(storedStatusInstallationDate);
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

    private void setOnclickListener() {
        binding.alphabeticalOrder.setOnClickListener(this);
        binding.installationDate.setOnClickListener(this);
        binding.systemApps.setOnClickListener(this);
        binding.userApps.setOnClickListener(this);
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
                break;
            case R.id.user_apps:
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
        Prefs.putBoolean(Constants.flag_alphabetical_order, true);
        Prefs.putBoolean(Constants.flag_installation_date, false);
    }

    private void storedInstallationDateClicked() {
        Prefs.putBoolean(Constants.flag_alphabetical_order, false);
        Prefs.putBoolean(Constants.flag_installation_date, true);
    }
}
