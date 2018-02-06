package com.martin.ads.ticktock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.martin.ads.ticktock.R;
import com.martin.ads.ticktock.model.TimerDatabaseHelper;
import com.martin.ads.ticktock.model.TimerModel;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.NormalTextViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private ArrayList<TimerModel> timerModels = null;
    private TimerStateListener timerStateListener;

    public ListAdapter(Context context,ArrayList<TimerModel> timerModels) {
        this.timerModels=timerModels;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.timer_item, parent, false));
    }

    @Override
    public void onBindViewHolder(NormalTextViewHolder holder, final int position) {
        holder.timerDurationText.setText(timerModels.get(position).getTimerTimeData().getSimpleTimeStr());
        holder.timerOnBtn.setChecked(timerModels.get(position).isOn());
        holder.timerOnBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(timerStateListener!=null)
                    timerStateListener.onTimerStateChanged(timerModels.get(position),isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timerModels == null ? 0 : timerModels.size();
    }

    class NormalTextViewHolder extends RecyclerView.ViewHolder {
        TextView timerDurationText;
        SwitchButton timerOnBtn;
        NormalTextViewHolder(View view) {
            super(view);
            timerDurationText = view.findViewById(R.id.time_duration);
            timerOnBtn=view.findViewById(R.id.timer_on);
        }
    }

    public interface TimerStateListener{
        void onTimerStateChanged(TimerModel timerModel,boolean isChecked);
    }

    public void setTimerStateListener(TimerStateListener timerStateListener) {
        this.timerStateListener = timerStateListener;
    }
}
