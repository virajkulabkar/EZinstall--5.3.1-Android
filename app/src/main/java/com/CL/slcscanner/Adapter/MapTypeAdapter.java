package com.CL.slcscanner.Adapter;

import android.content.Context;
import android.content.SharedPreferences;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.CL.slcscanner.Pojo.MapTypePojo;
import com.CL.slcscanner.R;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.MyCallbackForMapType;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vrajesh on 3/3/2018.
 */

public class MapTypeAdapter extends RecyclerView.Adapter<MapTypeAdapter.MyViewHolder> {

    List<MapTypePojo> mList;
    private MyCallbackForMapType objMyCallbackForMapType;
    Context mContext;

    SharedPreferences spf;

    public MapTypeAdapter(Context mContext, List<MapTypePojo> mList, MyCallbackForMapType objMyCallbackForMapType) {
        this.mList = mList;
        this.objMyCallbackForMapType = objMyCallbackForMapType;
        this.mContext = mContext;
        spf = mContext.getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_map_type, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if (mList.get(position).isChecked())
            holder.ivTickMark.setVisibility(View.VISIBLE);
        else
            holder.ivTickMark.setVisibility(View.GONE);

        holder.tvTitle2.setText(mList.get(position).getValue().toString());

        holder.llTitles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapTypePojo objMapTypePojo = new MapTypePojo();
                objMapTypePojo.setValue(holder.tvTitle2.getText().toString());
                objMapTypePojo.setPosition(position);
                objMapTypePojo.setChecked(true);
                objMyCallbackForMapType.onClickForControl(position, objMapTypePojo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle2)
        TextView tvTitle2;

        @BindView(R.id.ivTickMark)
        ImageView ivTickMark;

        @BindView(R.id.llTitles)
        LinearLayout llTitles;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}