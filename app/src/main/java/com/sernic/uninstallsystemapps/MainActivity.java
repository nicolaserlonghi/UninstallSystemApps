package com.sernic.uninstallsystemapps;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;

import at.grabner.circleprogress.CircleProgressView;

public class MainActivity extends AppCompatActivity {

    private CircleProgressView circleProgressView;
    private FloatingSearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circleProgressView = (CircleProgressView) findViewById(R.id.circleView);

        // Start initial animation of load
        CircleAnimation circleAnimation = new CircleAnimation(this, circleProgressView);
        circleAnimation.execute(true);

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

       /* mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                System.out.println(newQuery);
                mSearchView.showProgress();
                filter(newQuery);
                //mSearchView.swapSuggestions();
            }
        });*/

        final Context context = this;  // Da rimuovere successivamente
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.import_menu:
                        Toast.makeText(context, "Volevi rimuoverle automaticamente! :( ", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.export_menu:
                        Toast.makeText(context, "Volevi rimuoverle manualmente! :( ", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}