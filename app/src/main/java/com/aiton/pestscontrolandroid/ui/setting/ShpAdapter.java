package com.aiton.pestscontrolandroid.ui.setting;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aiton.pestscontrolandroid.R;
import com.aiton.pestscontrolandroid.data.model.ShpFile;

import java.util.ArrayList;
import java.util.List;

import static com.aiton.pestscontrolandroid.AppConstance.TAG;

public class ShpAdapter extends RecyclerView.Adapter<ShpAdapter.ShpViewHolder> {
    private List<ShpFile> shpFiles = new ArrayList<>();
    SettingViewModel settingViewModel;
    public List<ShpFile> getShpFiles() {
        return shpFiles;
    }

    public void setShpFiles(List<ShpFile> shpFiles) {
        this.shpFiles = shpFiles;
    }

    public ShpAdapter(SettingViewModel settingViewModel) {
        this.settingViewModel = settingViewModel;
    }

    @NonNull
    @Override
    public ShpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_setting_card,parent,false);
        final ShpAdapter.ShpViewHolder viewHolder = new ShpAdapter.ShpViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShpFile shpFile = (ShpFile) view.getTag();

//                Log.e(TAG, "onClick: " + shpFile.toString() );
            }
        });
        viewHolder.swShp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ShpFile shpFile = (ShpFile) view.getTag();
                if (b){
                    if (shpFiles.contains(shpFile)){
                        shpFile.setSelected(b);
                        settingViewModel.saveShpFile2SP();
                    }
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShpViewHolder holder, int position) {
        final ShpFile shpFile = shpFiles.get(position);
        holder.swShp.setText(String.valueOf(shpFile.getUrl()));
        holder.swShp.setChecked(shpFile.isSelected());
        holder.swShp.setTag(shpFile);
        holder.itemView.setTag(shpFile);
    }

    @Override
    public int getItemCount() {
        return shpFiles.size();
    }

    public class ShpViewHolder extends RecyclerView.ViewHolder {
        private Switch swShp;
        public ShpViewHolder(@NonNull View itemView) {
            super(itemView);
            this.swShp = itemView.findViewById(R.id.sw_shp);
        }
    }
}
