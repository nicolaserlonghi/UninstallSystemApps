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

package com.sernic.uninstallsystemapps;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by nicola on 03/12/17.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<App> mApps;
    private MainActivity mainActivity;

    public MyAdapter(ArrayList<App> apps, MainActivity mainActivity) {
        mApps = apps;
        this.mainActivity = mainActivity;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name_text_view;
        private TextView packageName_text_view;
        private ImageView icon_image_view;
        private CheckBox checkBox_image_view;
        private TextView textView_Selected;

        public ViewHolder(View v) {
            super(v);
            name_text_view = (TextView) v.findViewById(R.id.primaryText);
            packageName_text_view = (TextView) v.findViewById(R.id.secondaryText);
            icon_image_view = (ImageView) v.findViewById(R.id.imageView);
            checkBox_image_view = (CheckBox) v.findViewById(R.id.checkbox);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final App app = mApps.get(position);

        holder.name_text_view.setText(app.getName());
        holder.packageName_text_view.setText(app.getPackageName());
        holder.icon_image_view.setImageDrawable(app.getIcon());

        // Management of the checkbox
        holder.checkBox_image_view.setOnCheckedChangeListener(null);
        holder.checkBox_image_view.setChecked(app.isSelected());
        holder.checkBox_image_view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean Checked) {
                if(buttonView.isChecked()) {
                    app.setSelected(true);
                    mainActivity.addSelectApp();
                } else {
                    app.setSelected(false);
                    mainActivity.remSelectApp();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mApps.size();
    }

    public void updateList(ArrayList<App> apps){
        mApps = apps;
        notifyDataSetChanged();
    }
}
