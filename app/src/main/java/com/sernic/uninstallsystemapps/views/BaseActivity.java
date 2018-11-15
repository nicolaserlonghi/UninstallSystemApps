package com.sernic.uninstallsystemapps.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sernic.uninstallsystemapps.Logger;
import com.sernic.uninstallsystemapps.viewmodels.BaseViewModel;

public abstract class BaseActivity extends AppCompatActivity {
    protected abstract int getLayoutId();

    protected abstract void setToolbar();

    protected abstract BaseViewModel getViewModel();

    protected abstract void setBinding();

    public BaseActivity getCurrentActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getLayoutId() == 0) {
            Logger.w(BaseActivity.class.getSimpleName(), "Layout id is zero");

            return;
        }

        setBinding();
        setToolbar();
    }
}
