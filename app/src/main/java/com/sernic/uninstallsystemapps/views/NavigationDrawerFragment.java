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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sernic.uninstallsystemapps.Constants;
import com.sernic.uninstallsystemapps.R;
import com.sernic.uninstallsystemapps.databinding.FragmentNavigationDrawerBinding;
import com.sernic.uninstallsystemapps.helpers.CustomAlertDialog;
import com.sernic.uninstallsystemapps.models.App;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class NavigationDrawerFragment extends com.google.android.material.bottomsheet.BottomSheetDialogFragment
        implements View.OnClickListener {

    private static final int WRITE_REQUEST_CODE = 43;
    private static final int READ_REQUEST_CODE = 42;

    private FragmentNavigationDrawerBinding binding;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_navigation_drawer, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.applicationVersion.setText(String.format(getString(R.string.menu_application_version), getString(R.string.app_version_code)));
        setOnclickListener();
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_navigation_drawer, null);
        dialog.setContentView(view);
    }

    private void setOnclickListener() {
        binding.boxInfoDeveloper.setOnClickListener(this);
        binding.boxLeaveFeedback.setOnClickListener(this);
        binding.boxImportSelected.setOnClickListener(this);
        binding.boxExportSelected.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.box_info_developer:
                String url = Constants.MY_WEB_SITE;
                openWebSite(url);
                break;
            case R.id.box_leave_feedback:
                String mail = Constants.MAIL;
                String subject = Constants.SUBJECT;
                openMailFeedback(mail, subject);
                break;
            case R.id.box_import_selected:
                performFileSearch();
                break;
            case R.id.box_export_selected:
                if(atLeasOneAppSelected()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.ITALY);
                    Date now = new Date();
                    createFile("text/uninsSystemApp", formatter.format(now) + ".uninsSystemApp");
                } else {
                    CustomAlertDialog.showAlertDialogWithOneButton(
                            getContext(),
                            getResources().getString(R.string.alert_dialog_title_no_app_selected),
                            getResources().getString(R.string.alert_dialog_message_no_app_select),
                            getResources().getString(R.string.button_ok));
                }
                break;
        }
    }

    private void openWebSite(String url) {
        Uri uri = Uri.parse(url);
        Intent openWebsite = new Intent(Intent.ACTION_VIEW);
        openWebsite.setData(uri);
        startActivity(openWebsite);
    }

    private void openMailFeedback(String mailtTo, String subject) {
        String subjectEncode = Uri.encode(subject);
        String uriText = "mailto:" + mailtTo + "?SUBJECT=" + subjectEncode;
        Uri uri = Uri.parse(uriText);
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(uri);
        startActivity(Intent.createChooser(sendIntent, "Send email"));
    }

    private boolean atLeasOneAppSelected() {
        ArrayList<App> apps = ((MainActivity)getActivity()).getAllInstalledApps();
        for(App app : apps) {
            if(app.isSelected())
                return true;
        }
        return false;
    }
    private void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Filter to show only images, using the image MIME data type.
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    // Open browser to save the file
    private void createFile(String mimeType, String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        // Filter to only show results that can be "opened"
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Create a file with the requested MIME type.
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    // Create or upload the file according to the code shown
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if(resultCode == Activity.RESULT_OK && resultData != null) {
            switch (requestCode) {
                case READ_REQUEST_CODE:
                    try {
                        readFileContent(resultData.getData(), ((MainActivity)getActivity()).getAllInstalledApps());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case WRITE_REQUEST_CODE:
                    writeFileContent(resultData.getData(), ((MainActivity)getActivity()).getAllInstalledApps());
                    break;
            }
        }
    }

    // I write the selected apps within the newly created file
    private void writeFileContent(Uri uri, ArrayList<App> apps) {
        String selectedApp = "";
        int count = 0;
        for(App app : apps) {
            if(app.isSelected()) {
                selectedApp = selectedApp + app.getPackageName() + ",";
                count++;
            }
        }
        try{
            ParcelFileDescriptor pfd = getContext().getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
            fileOutputStream.write(selectedApp.getBytes());
            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CustomAlertDialog.showAlertDialogWithOneButton(
                getContext(),
                getResources().getString(R.string.alert_dialog_title_exported_correctly),
                getResources().getString(R.string.alert_dialog_message_exported_correctly),
                getResources().getString(R.string.button_ok)
        );
    }

    // I read the selected apps in the file that is passed to me and update the recyclerView
    private void readFileContent(Uri uri, ArrayList<App> apps) throws IOException {
        InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String currentline;
        while ((currentline = reader.readLine()) != null) {
            stringBuilder.append(currentline);
        }
        inputStream.close();
        ArrayList<String> selectedApp = new ArrayList<String>(Arrays.asList(stringBuilder.toString().split(",")));
        int count = 0;
        for(App app : apps) {
            if(selectedApp.contains(app.getPackageName())) {
                app.setSelected(true);
                count++;
            }
        }
        ((MainActivity)getActivity()).setAllInstalledApps(apps);
        if(count == 0) {
            CustomAlertDialog.showAlertDialogWithOneButton(
                    getContext(),
                    getResources().getString(R.string.alert_dialog_title_no_app_selected_present),
                    getResources().getString(R.string.alert_dialog_message_no_app_selected_present),
                    getResources().getString(R.string.button_ok)
            );
        } else {
            CustomAlertDialog.showAlertDialogWithOneButton(
                    getContext(),
                    getResources().getString(R.string.alert_dialog_title_imported_correctly),
                    count + " " + getResources().getString(R.string.alert_dialog_message_imported_correctly),
                    getResources().getString(R.string.button_ok)
            );
        }
    }
}
