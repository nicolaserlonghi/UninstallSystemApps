package com.sernic.uninstallsystemapps;

import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;
import com.arlib.floatingsearchview.FloatingSearchView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FloatingSearchView mSearchView;
    private ArrayList<App> mApps;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Load apps
        SearchApp searchApp = new SearchApp(this);
        searchApp.execute();
        // execute...



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

                System.out.println(newQuery);
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
                        Toast.makeText(getApplicationContext(), "Volevi rimuoverle automaticamente! :( ", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.export_menu:
                        Toast.makeText(getApplicationContext(), "Volevi rimuoverle manualmente! :( ", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void setApplicationList(ArrayList<App> apps) {
        mApps = apps;

        // Do other stuff
    }


    void filter(String text) {

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        ArrayList<App> temp = new ArrayList();
        for(App app: mApps){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(app.getName().toLowerCase().contains(text.toLowerCase()))
                temp.add(app);
        }
        //update recyclerview
        mSearchView.hideProgress();

        ((MyAdapter)mRecyclerView.getAdapter()).updateList(temp);
    }
}