package com.sernic.uninstallsystemapps;

import android.content.Context;
import android.os.AsyncTask;
import android.os.ConditionVariable;
import android.os.Handler;
import android.view.View;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by nicola on 02/12/17.
 */

public class CircleAnimation extends AsyncTask<Boolean, Void, Boolean> {

    private final Handler handler;
    private CircleProgressView mCircleView;

    public ConditionVariable conditionVariable = new ConditionVariable();
    public CircleAnimation(Context context, CircleProgressView mCircleView) {
        handler = new Handler(context.getMainLooper());
        this.mCircleView = mCircleView;
    }

    @Override
    protected Boolean doInBackground(final Boolean... status) {




        handler.post(new Runnable() {
            @Override
            public void run() {

                mCircleView.setSeekModeEnabled(false);
                mCircleView.setSpinningBarLength(80);
                mCircleView.setSpinSpeed(2);
                mCircleView.setShowTextWhileSpinning(true);

                //Status = true loading animation
                //Stautus = false execute animation
                if(status[0]) {
                    mCircleView.setText("Loading...");
                    mCircleView.setTextMode(TextMode.TEXT);
                    mCircleView.setUnitVisible(false);
                    mCircleView.spin();
               } else {
                    mCircleView.setValue(0);
               }
            }
        });


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return status[0];
    }

    @Override
    protected void onPostExecute(final Boolean status) {

        if(status)
            mCircleView.stopSpinning();
        else
            mCircleView.setValueAnimated(100);


        mCircleView.setVisibility(View.INVISIBLE);
    }
}

