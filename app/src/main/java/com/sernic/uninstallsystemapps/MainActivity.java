package com.sernic.uninstallsystemapps;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import at.grabner.circleprogress.CircleProgressView;

public class MainActivity extends FragmentActivity {

    private CircleAnimation circleAnimation;
    private CircleProgressView circleProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circleProgressView = (CircleProgressView)findViewById(R.id.circleView);

        // Start initial animation of load
        CircleAnimation circleAnimation = new CircleAnimation(this, circleProgressView);
        circleAnimation.execute(true);


    }
}
