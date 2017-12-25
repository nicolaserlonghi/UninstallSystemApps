package com.sernic.uninstallsystemapps;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.chrisplus.rootmanager.RootManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private FloatingSearchView mSearchView;
    private ArrayList<App> mApps;
    private RecyclerView mRecyclerView;
    private View view;
    // Unique request code.
    private static final int WRITE_REQUEST_CODE = 43;
    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = findViewById(R.id.coordinatorLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                if(RootManager.getInstance().obtainPermission())
                    Snackbar.make(view, "root ottenuto", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                else
                    Snackbar.make(view, "Non ho ottenuto l'acceso root", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });

        // Load apps
        SearchApp searchApp = new SearchApp(this);
        searchApp.execute();
        // execute...

        if(RootManager.getInstance().hasRooted()) {
            Snackbar.make(view, "hai il root", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            Snackbar.make(view, "Non hai il root", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        // Animazione del floatingActionButton durante lo scroll
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

        // Search view
        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mSearchView.setBackgroundColor(Color.parseColor("#787878"));
        mSearchView.setViewTextColor(Color.parseColor("#e9e9e9"));
        mSearchView.setHintTextColor(Color.parseColor("#e9e9e9"));
        mSearchView.setActionMenuOverflowColor(Color.parseColor("#e9e9e9"));
        mSearchView.setMenuItemIconColor(Color.parseColor("#e9e9e9"));
        mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
        mSearchView.setClearBtnColor(Color.parseColor("#e9e9e9"));
        mSearchView.setDividerColor(Color.parseColor("#BEBEBE"));
        mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                mSearchView.showProgress();
                filter(newQuery);
                //mSearchView.swapSuggestions();
            }
        });
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.import_menu:
                        performFileSearch();
                        break;
                    case R.id.export_menu:
                        if(((MyAdapter)mRecyclerView.getAdapter()).getCheckOneApp() == 0) {
                            Snackbar.make(view, "Nessuna app selezionata!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            break;
                        } else {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.ITALY);
                            Date now = new Date();
                            createFile("text/uninsSystemApp", formatter.format(now) + ".uninsSystemApp");
                            break;
                        }
                }
            }
        });
    }


    public void setApplicationList(ArrayList<App> apps) {
        mApps = apps;

        // Do other stuff
    }


    // Filtra le app e aggiorna la recyclerView per la floatingSearchBar
    void filter(String text) {
        ArrayList<App> temp = new ArrayList();

        for(App app: mApps){
            if(app.getName().toLowerCase().contains(text.toLowerCase()))
                temp.add(app);
        }
        // Update recyclerview
        mSearchView.hideProgress();
        ((MyAdapter)mRecyclerView.getAdapter()).updateList(temp);
    }


    // Apra il browser per far selezionare all'utente il file
    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Filter to show only images, using the image MIME data type.
        intent.setType("text/uninsSystemApp");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }


    // Apre il browser per salvare il file
    private void createFile(String mimeType, String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened"
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Create a file with the requested MIME type.
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }


    // Crea o carica il file in base al codice che gli arriva
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


    // Scrivo all'inerno del file appena creato le app selezionate
    private void writeFileContent(Uri uri) {
        String selectedApp = "";

        int count = 0;
        for(App app: mApps) {
            if(app.isSelected()) {
                selectedApp = selectedApp + app.getPackageName() + ",";
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
        Snackbar.make(view, "Esportazione di " + count + " app completata!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    
    // Leggo le app selezionate all'interno del file che mi viene passato e aggiorno la recyclerView
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
        for(App app : mApps) {
            if(selectedApp.contains(app.getPackageName())) {
                app.setSelected(true);
                count++;
            } else {
                app.setSelected(false);
            }
        }
        mRecyclerView.getAdapter().notifyDataSetChanged();

        if(count == 0) {
            Snackbar.make(view, "Nessuna app selezionata presente!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            Snackbar.make(view, count + " app importate!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }
}