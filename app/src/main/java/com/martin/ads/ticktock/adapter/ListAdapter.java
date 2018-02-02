package com.martin.ads.ticktock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.martin.ads.ticktock.R;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.NormalTextViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private String[] mTitles = null;

    public ListAdapter(Context context) {
        mTitles = new String[]{"2123243","fdsgdfghgfh"
                ,"2123243","fdsgdfghgfh"
                ,"2123243","fdsgdfghgfh"
                ,"2123243","fdsgdfghgfh"
                ,"2123243","fdsgdfghgfh"
                ,"2123243","fdsgdfghgfh"
                ,"2123243","fdsgdfghgfh"
                ,"2123243","fdsgdfghgfh"
                , "2123243","fdsgdfghg32fh111"};
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.timer_item, parent, false));
    }

    @Override
    public void onBindViewHolder(NormalTextViewHolder holder, int position) {
        holder.mTextView.setText(mTitles[position]);
    }

    @Override
    public int getItemCount() {
        return mTitles == null ? 0 : mTitles.length;
    }

    class NormalTextViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        NormalTextViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.tv_text);
        }
    }

}
