package com.aiton.pestscontrolandroid.ui.trap;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aiton.pestscontrolandroid.R;
import com.aiton.pestscontrolandroid.data.persistence.Trap;

import java.util.ArrayList;
import java.util.List;

import static com.aiton.pestscontrolandroid.AppConstance.TAG;

public class TrapAdapter extends RecyclerView.Adapter<TrapAdapter.TrapViewHolder> {
    private List<Trap> traps = new ArrayList<>();
    TrapViewModel trapViewModel;

    public List<Trap> getTraps() {
        return traps;
    }

    public void setTraps(List<Trap> traps) {
        this.traps = traps;
    }

    public TrapAdapter(TrapViewModel trapViewModel) {
        this.trapViewModel = trapViewModel;
    }

    @NonNull
    @Override
    public TrapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_trap_job_card,parent,false);
        final TrapAdapter.TrapViewHolder viewHolder = new TrapAdapter.TrapViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Trap p = (Trap) view.getTag();

                Log.e(TAG, "onClick: " + p.toString() );
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrapViewHolder holder, int position) {
        final Trap p = traps.get(position);
        holder.trap_card_deviceId.setText(p.getDeviceId());
        holder.trap_card_count.setText(String.valueOf(p.getScount()));
        holder.trap_card_town.setText(p.getTown());
        holder.trap_card_village.setText(p.getVillage());
        holder.trap_card_operator.setText(p.getOperator());
        holder.trap_card_db.setText(String.valueOf(p.getDb()));
        holder.trap_card_xb.setText(String.valueOf(p.getXb()));
        holder.trap_card_position_error.setText(String.valueOf(p.getPositionError()));
        holder.trap_card_remark.setText(p.getRemark());
        if (p.getLureReplaced() == 0){
            holder.trap_card_lure_replace.setText("已换芯");
        }else{
            holder.trap_card_lure_replace.setText("没换芯");
        }

        holder.trap_card_stime.setText(p.getStime());
        holder.trap_card_longitude.setText(String.valueOf(p.getLongitude()));
        holder.trap_card_latitude.setText(String.valueOf(p.getLatitude()));
        holder.trap_card_update.setChecked(p.isUpdateServer());
        holder.itemView.setTag(p);
    }

    @Override
    public int getItemCount() {
        return traps.size();
    }

    public class TrapViewHolder extends RecyclerView.ViewHolder {
        private TextView trap_card_deviceId,trap_card_count,trap_card_town,trap_card_village,trap_card_operator,trap_card_db,trap_card_xb,trap_card_position_error,trap_card_remark,trap_card_lure_replace,trap_card_stime,trap_card_longitude,trap_card_latitude;
        private Switch trap_card_update;

        public TrapViewHolder(@NonNull View itemView) {
            super(itemView);
            this.trap_card_update = itemView.findViewById(R.id.trap_card_update);
            this.trap_card_deviceId = itemView.findViewById(R.id.trap_card_deviceId);
            this.trap_card_count = itemView.findViewById(R.id.trap_card_count);
            this.trap_card_town = itemView.findViewById(R.id.trap_card_town);
            this.trap_card_village = itemView.findViewById(R.id.trap_card_village);
            this.trap_card_operator = itemView.findViewById(R.id.trap_card_operator);
            this.trap_card_db = itemView.findViewById(R.id.trap_card_db);
            this.trap_card_xb = itemView.findViewById(R.id.trap_card_xb);
            this.trap_card_position_error = itemView.findViewById(R.id.trap_card_position_error);
            this.trap_card_remark = itemView.findViewById(R.id.trap_card_remark);
            this.trap_card_lure_replace = itemView.findViewById(R.id.trap_card_lure_replace);
            this.trap_card_stime = itemView.findViewById(R.id.trap_card_stime);
            this.trap_card_longitude = itemView.findViewById(R.id.trap_card_longitude);
            this.trap_card_latitude = itemView.findViewById(R.id.trap_card_latitude);
        }
    }

}
