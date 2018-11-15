/*
 * MIT License
 *
 * Copyright (c) 2018 Nicola Serlonghi
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

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.sernic.uninstallsystemapps.helpers.ObtainRoot;
import com.sernic.uninstallsystemapps.R;
import com.sernic.uninstallsystemapps.RemoveApps;
import com.sernic.uninstallsystemapps.SearchApp;
import com.sernic.uninstallsystemapps.adapters.MyAdapter;
import com.sernic.uninstallsystemapps.models.Application;
import com.sernic.uninstallsystemapps.viewmodels.BaseViewModel;
import com.sernic.uninstallsystemapps.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends BaseActivity {
    private ArrayList<Application> mApplications, tempMApplications;
    private RecyclerView mRecyclerView;
    private View view;
    private Boolean rootAccess;
    private FloatingActionButton fab;
    private TextView numApps;
    private ImageButton selectAll;
    private int selectApps = 0;
    private Boolean busyBox = true;
    private boolean shouldExecuteOnResume = true;
    // Unique request code
    private static final int WRITE_REQUEST_CODE = 43;
    private static final int READ_REQUEST_CODE = 42;
    private static final String KEY_PREF_HIDE_SYSTEM_APPS = "hide_system_apps";

    private ActivityMainBinding binding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setToolbar() {
        setSupportActionBar((Toolbar)binding.toolbar);
    }

    @Override
    protected BaseViewModel getViewModel() {
        return null;
    }

    @Override
    protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = findViewById(R.id.coordinatorLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        numApps = (TextView) findViewById(R.id.num_apps);
        selectAll = (ImageButton) findViewById(R.id.select_all);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Load apps
        final SearchApp searchApp = new SearchApp(this);
        searchApp.execute();


        // execute...

        // floating button action
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rootAccess) {
                    // Elimino le app se almeno una è selezionata
                    if(selectApps == 0) {
                        Snackbar.make(view, getResources().getString(R.string.snackBar_no_app_selected), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                        if(!busyBox) {
                            // Add message for busybox
                            builder.setMessage(R.string.no_busyBox_message);
                            builder.setNeutralButton(R.string.button_install_busybox, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("market://details?id=stericson.busybox"));
                                    startActivity(intent);
                                }
                            });
                        }
                        // Create allert to ask to remove apps
                        builder.setTitle(getResources().getString(R.string.confirm_remove))
                                .setPositiveButton(getResources().getString(R.string.button_yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with remove
                                        RemoveApps removeApps = new RemoveApps(MainActivity.this);
                                        removeApps.execute();
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.button_no), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .show();

                    }
                } else {
                    Snackbar.make(view, getResources().getString(R.string.snackBar_no_root_remove_apps), Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.button_obtain), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ObtainRoot obtainRoot = new ObtainRoot(MainActivity.this);
                                    obtainRoot.execute();
                                }
                            }).show();
                }
            }
        });

        // floatingActionButton scroll animation
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectApps == tempMApplications.size()) {
                    for(Application application : tempMApplications) {
                        application.setSelected(false);
                    }
                    selectAll.setImageResource(R.drawable.select_all_white_24px);
                    selectApps = 0;
                } else {
                    for(Application application : tempMApplications) {
                        application.setSelected(true);
                    }
                    selectAll.setImageResource(R.drawable.deselect_all_white_24px);
                    selectApps = tempMApplications.size();
                }
                numApps.setText(selectApps + " " + getResources().getString(R.string.num_apps_of) + " "+ tempMApplications.size());
                mRecyclerView.getAdapter().notifyDataSetChanged();
                if(fab.getVisibility() != View.VISIBLE)
                    fab.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(shouldExecuteOnResume){
            // Obtain root
            ObtainRoot obtainRoot = new ObtainRoot(this);
            obtainRoot.execute();
        } else{
            shouldExecuteOnResume = true;
        }
    }

    // Application counter utility selected
    public void addSelectApp() {
        selectApps++;
        numApps.setText(selectApps + " " + getResources().getString(R.string.num_apps_of) + " " + tempMApplications.size());
        if(selectApps == tempMApplications.size())
            selectAll.setImageResource(R.drawable.deselect_all_white_24px);
        if(fab.getVisibility() != View.VISIBLE)
            fab.show();
    }

    public void remSelectApp() {
        if(selectApps == tempMApplications.size())
            selectAll.setImageResource(R.drawable.select_all_white_24px);
        selectApps--;
        numApps.setText(selectApps + " " + getResources().getString(R.string.num_apps_of) + " " + tempMApplications.size());
        if(fab.getVisibility() != View.VISIBLE)
            fab.show();
    }

    // Search view
    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem actionSearch = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) actionSearch.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    filter("");
                    // listView.clearTextFilter();
                } else {
                    filter(newText);
                }
                return true;
            }
        });
        // Set the checkBox with saved value
        MenuItem hideSystemApps = menu.findItem(R.id.hide_system_apps);
        hideSystemApps.setChecked(readBooleanPreference(KEY_PREF_HIDE_SYSTEM_APPS, false));

        return true;
    }


    // Menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id) {
            case R.id.import_menu:
                shouldExecuteOnResume = false;
                performFileSearch();
                break;
            case R.id.export_menu:
                if(selectApps == 0) {
                    Snackbar.make(view, getResources().getString(R.string.snackBar_no_app_selected), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    shouldExecuteOnResume = false;
                    SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.ITALY);
                    Date now = new Date();
                    createFile("text/uninsSystemApp", formatter.format(now) + ".uninsSystemApp");
                }
                break;
            case R.id.hide_system_apps:
                if(item.isChecked()){
                    item.setChecked(false);
                    hideApp(true, false);
                    saveBooleanPreference(KEY_PREF_HIDE_SYSTEM_APPS, false);
                } else {
                    item.setChecked(true);
                    hideApp(true, true);
                    saveBooleanPreference(KEY_PREF_HIDE_SYSTEM_APPS, true);
                }
                break;
            case R.id.settings_menu:
                shouldExecuteOnResume = false;
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

        }
        return false;
    }

    // Remove from the recyclerView type of app indicated by the parameters
    private void hideApp(Boolean toSystem, Boolean toHide) {
        if(toHide) {
            for(Application application : mApplications) {
                if (toSystem == application.isSystemApp()) {
                    application.setSelected(false);
                    tempMApplications.remove(application);
                }
            }
        } else if(mApplications.size() != tempMApplications.size()){
            for(Application application : mApplications) {
                if (toSystem == application.isSystemApp()) {
                    tempMApplications.add(application);
                }
            }
        }

        // Sort apps in alphabetical order
        Collections.sort(tempMApplications, new Comparator<Application>() {
            @Override
            public int compare(Application application, Application t1) {
                return application.getName().compareToIgnoreCase(t1.getName());
            }
        });
        for(Application application : mApplications) {
            application.setSelected(false);
        }
        selectApps = 0;
        selectAll.setImageResource(R.drawable.select_all_white_24px);
        numApps.setText(selectApps + " " + getResources().getString(R.string.num_apps_of) + " " + tempMApplications.size());
        if(fab.getVisibility() != View.VISIBLE)
            fab.show();
        ((MyAdapter)mRecyclerView.getAdapter()).updateList(tempMApplications);
    }

    private void saveBooleanPreference(String key, Boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private Boolean readBooleanPreference(String key, Boolean defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void setApplicationList(ArrayList<Application> applications) {
        mApplications = new ArrayList<>(applications);
        tempMApplications = new ArrayList<>(applications);
        // Hide the applications based on checkbox stored value
        hideApp(true, readBooleanPreference(KEY_PREF_HIDE_SYSTEM_APPS, false));
    }

    public ArrayList<Application> getApplicationList() {
        return mApplications;
    }

    public void setRootAccess(Boolean rootAccess) {
        this.rootAccess = rootAccess;
    }

    // Filter the apps and update the recyclerView for the floatingSearchBar
    void filter(String text) {
        ArrayList<Application> temp = new ArrayList();

        for(Application application : tempMApplications){
            if(application.getName().toLowerCase().contains(text.toLowerCase()))
                temp.add(application);
        }
        // Update recyclerview
        ((MyAdapter)mRecyclerView.getAdapter()).updateList(temp);
    }


    // Open browser to have the user select the file
    public void performFileSearch() {
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
                        readFileContent(resultData.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case WRITE_REQUEST_CODE:
                    writeFileContent(resultData.getData());
                    break;
            }
        }
    }


    // I write the selected apps within the newly created file
    private void writeFileContent(Uri uri) {
        String selectedApp = "";

        int count = 0;
        for(Application application : mApplications) {
            if(application.isSelected()) {
                selectedApp = selectedApp + application.getPackageName() + ",";
                count++;
            }
        }
        try{
            ParcelFileDescriptor pfd = this.getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
            fileOutputStream.write(selectedApp.getBytes());
            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Snackbar.make(view, count + " " + getResources().getString(R.string.snackBar_export), Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    
    // I read the selected apps in the file that is passed to me and update the recyclerView
    private void readFileContent(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String currentline;
        while ((currentline = reader.readLine()) != null) {
            stringBuilder.append(currentline);
        }
        inputStream.close();
        ArrayList<String> selectedApp = new ArrayList<String>(Arrays.asList(stringBuilder.toString().split(",")));

        int count = 0;
        selectApps = 0;
        for(Application application : mApplications) {
            if(selectedApp.contains(application.getPackageName())) {
                if(application.isSystemApp()) {
                    if(!readBooleanPreference(KEY_PREF_HIDE_SYSTEM_APPS, false)) {
                        application.setSelected(true);
                        addSelectApp();
                        count++;
                    } else {
                        application.setSelected(false);
                    }
                } else {
                    application.setSelected(true);
                    addSelectApp();
                    count++;
                }

            } else {
                application.setSelected(false);
            }
        }
        mRecyclerView.getAdapter().notifyDataSetChanged();

        if(count == 0) {
            Snackbar.make(view, getResources().getString(R.string.snackBar_no_app_selected_present), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            Snackbar.make(view, count + " " + getResources().getString(R.string.snackBar_import), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    // Controll back press to exit
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.toast_to_exit), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2500);
    }

    public void setBusyBox(Boolean busyBox) {
        this.busyBox = busyBox;
    }
}