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

package com.sernic.uninstallsystemapps.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sernic.uninstallsystemapps.R;
import com.sernic.uninstallsystemapps.databinding.RecyclerRowItemBinding;
import com.sernic.uninstallsystemapps.models.App;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class AppRecyclerAdapter extends RecyclerView.Adapter<AppRecyclerAdapter.ViewHolder> {

    private RecyclerRowItemBinding binding;
    private ControllerAppsSelected controllerAppsSelected;
    private ArrayList<App> installedApps;

    public AppRecyclerAdapter(ControllerAppsSelected controllerAppsSelected, ArrayList<App> installedApps) {
        this.controllerAppsSelected = controllerAppsSelected;
        this.installedApps = installedApps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.recycler_row_item,
                parent,
                false);
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_row_item, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView title;
        private TextView appPackage;
        private CheckBox selected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            icon = binding.imageView;
            title = binding.primaryText;
            appPackage = binding.secondaryText;
            selected = binding.checkbox;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        App app = installedApps.get(position);
        holder.icon.setImageDrawable(app.getIcon());
        holder.title.setText(app.getName());
        holder.appPackage.setText(app.getPackageName());
        holder.selected.setOnCheckedChangeListener((buttonView, isChecked) -> {
            app.setSelected(isChecked);
            if(isChecked)
                controllerAppsSelected.isSelectApp(app);
            else
                controllerAppsSelected.isDeselctApp(app);
        });
        boolean appIsSelected = app.isSelected();
        holder.selected.setChecked(appIsSelected);
    }

    @Override
    public int getItemCount() {
        return installedApps.size();
    }

    public void updataList(ArrayList<App> installedApps) {
        this.installedApps = installedApps;
        notifyDataSetChanged();
    }
}
