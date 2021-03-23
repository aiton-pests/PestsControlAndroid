package com.aiton.pestscontrolandroid.ui.myjob;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aiton.pestscontrolandroid.R;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.ui.pests.PestsViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.aiton.pestscontrolandroid.AppConstance.TAG;

public class PestsAdapter  extends RecyclerView.Adapter<PestsAdapter.PestsViewHolder> {
    private List<Pests> pests = new ArrayList<>();
    PestsViewModel pestsViewModel;

    public List<Pests> getPests() {
        return pests;
    }

    public void setPests(List<Pests> pests) {
        this.pests = pests;
    }

    public PestsAdapter(PestsViewModel viewModel) {
        this.pestsViewModel = viewModel;
    }

    @NonNull
    @Override
    public PestsAdapter.PestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_my_job_card,parent,false);
        final PestsAdapter.PestsViewHolder viewHolder = new PestsAdapter.PestsViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pests p = (Pests) view.getTag();

                Log.e(TAG, "onClick: " + p.toString() );
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PestsAdapter.PestsViewHolder holder, int position) {
        final Pests p = pests.get(position);
        holder.tv_bag_number.setText(String.valueOf(p.getBagNumber()));
        holder.tv_db.setText(p.getDb());
        holder.tv_operator.setText(p.getOperator());
        holder.tv_town.setText(p.getTown());
        holder.tv_village.setText(p.getVillage());
        holder.tv_xb.setText(p.getXb());
        holder.tv_db.setText(p.getDb());
        holder.tv_stime.setText(p.getStime());
        holder.tvDeviceId.setText(p.getDeviceId());
        holder.tv_treeWalk.setText(p.getTreeWalk());
        holder.tv_position_error.setText(p.getPositionError());
        holder.tv_pests_type.setText(p.getPestsType());
        holder.tv_latitude.setText(String.valueOf(p.getLatitude()));
        holder.tv_longitude.setText(String.valueOf(p.getLongitude()));
        holder.swUpdate.setChecked(p.isUpdateServer());
        holder.itemView.setTag(p);
    }

    @Override
    public int getItemCount() {
        return pests.size();
    }

    public class PestsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDeviceId,tv_treeWalk,tv_town,tv_village,tv_operator,tv_db,tv_xb,tv_position_error,tv_bag_number,tv_pests_type,tv_stime,tv_longitude,tv_latitude;
        private Switch swUpdate;

        public PestsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.swUpdate = itemView.findViewById(R.id.myjob_card_update);
            this.tvDeviceId = itemView.findViewById(R.id.myjob_card_deviceId);
            this.tv_treeWalk = itemView.findViewById(R.id.myjob_card_count);
            this.tv_town = itemView.findViewById(R.id.myjob_card_town);
            this.tv_village = itemView.findViewById(R.id.myjob_card_village);
            this.tv_operator = itemView.findViewById(R.id.myjob_card_operator);
            this.tv_db = itemView.findViewById(R.id.myjob_card_db);
            this.tv_xb = itemView.findViewById(R.id.myjob_card_xb);
            this.tv_position_error = itemView.findViewById(R.id.myjob_card_position_error);
            this.tv_bag_number = itemView.findViewById(R.id.myjob_card_remark);
            this.tv_pests_type = itemView.findViewById(R.id.myjob_card_lure_replace);
            this.tv_stime = itemView.findViewById(R.id.myjob_card_stime);
            this.tv_longitude = itemView.findViewById(R.id.myjob_card_longitude);
            this.tv_latitude = itemView.findViewById(R.id.myjob_card_latitude);
        }
    }
}
