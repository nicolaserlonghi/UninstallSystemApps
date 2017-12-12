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
import java.util.HashMap;

/**
 * Created by nicola on 03/12/17.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<App> mApps;
    private HashMap<String, Boolean> mAppsChecked = new HashMap<>();

    public MyAdapter(ArrayList<App> apps) {
        mApps = apps;
        mAppsChecked = new HashMap<>();
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final App app = mApps.get(position);

        holder.name_text_view.setText(app.getName());
        holder.path_text_view.setText(app.getPath());
        holder.icon_image_view.setImageDrawable(app.getIcon());
        holder.checkBox_image_view.setOnCheckedChangeListener(null);

        //Gestione delle checkbox
        if(mAppsChecked.containsKey(app.getPath())) {
            holder.checkBox_image_view.setChecked(mAppsChecked.get(app.getPath()));
        } else {
            holder.checkBox_image_view.setChecked(false);
        }

        holder.checkBox_image_view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean Checked) {
                if(buttonView.isChecked()) {
                    mAppsChecked.put(app.getPath(), true);
                    app.setSelected(true);
                } else {
                    mAppsChecked.put(app.getPath(), false);
                    app.setSelected(false);
                }
            }
        });
    }

    public void updateList(ArrayList<App> apps){
        mApps = apps;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mApps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name_text_view;
        private TextView path_text_view;
        private ImageView icon_image_view;
        private CheckBox checkBox_image_view;

        public ViewHolder(View v) {
            super(v);
            name_text_view = (TextView) v.findViewById(R.id.primaryText);
            path_text_view = (TextView) v.findViewById(R.id.secondaryText);
            icon_image_view = (ImageView) v.findViewById(R.id.imageView);
            checkBox_image_view = (CheckBox) v.findViewById(R.id.checkbox);
        }
    }
}