package com.sernic.uninstallsystemapps;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nicola on 03/12/17.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<ApplicationInfo> mDataset;
    private PackageManager packageManager;
    private HashMap<String, Boolean> finale;
    private HashMap<String, Boolean> isChecked = new HashMap<>();
    private static Context mContext;
    private LayoutInflater inflater;
    private List<String> productList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
            /*
            checkBox_image_view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        Toast.makeText(mContext, "checklilst", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "unchecklilst", Toast.LENGTH_SHORT).show();
                    }
                }
            }); */
        }
    }



    public MyAdapter(List<ApplicationInfo> myDataset, PackageManager packageManager, Context context, List<String> productList) {

        mDataset = myDataset;
        this.packageManager = packageManager;
        this.mContext = context;
        this.productList = productList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView =inflater.inflate(R.layout.recyclerview_row, parent, false);
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //holder.checkBox_image_view.setText(productList.get(position));
        //System.out.println(Arrays.asList(isChecked));
        System.out.println("pos:" + position);
        final ApplicationInfo tmp = mDataset.get(position);
        Drawable icon;

        holder.checkBox_image_view.setOnCheckedChangeListener(null);

        if(isChecked.containsKey(tmp.publicSourceDir)) {
            holder.checkBox_image_view.setChecked(isChecked.get(tmp.publicSourceDir));
        } else {
            holder.checkBox_image_view.setChecked(false);
        }

        holder.checkBox_image_view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean Checked) {
                if(buttonView.isChecked()) {
                    System.out.println("pos int:" + position);
                    isChecked.put(tmp.publicSourceDir, true);
                } else {
                    System.out.println("pos int:" + position);
                    isChecked.put(tmp.publicSourceDir, false);
                }
            }
        });




        try {
            icon = packageManager.getApplicationIcon(tmp.processName);
        } catch (PackageManager.NameNotFoundException e) {
            icon = packageManager.getDefaultActivityIcon();
            e.printStackTrace();
        }

        holder.name_text_view.setText(packageManager.getApplicationLabel(tmp));
        holder.path_text_view.setText(tmp.publicSourceDir);
        holder.icon_image_view.setImageDrawable(icon);

    }


    public Map<String, Boolean> getAppSelected() {
        finale = new HashMap<>();

        for(int i = 0; i < productList.size(); i++) {
            if(isChecked.containsKey(productList.get(i))) {
                finale.put(productList.get(i), isChecked.get(productList.get(i)));
            } else {
                finale.put(productList.get(i), false);
            }
        }
        return finale;
    }

    public void updateList(List<ApplicationInfo> list){
        mDataset = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}