package com.CL.slcscanner.Adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.CL.slcscanner.Pojo.PoleDisplayData.Asset;
import com.CL.slcscanner.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vrajesh on 3/3/2018.
 */

public class AssestDisplayAdapter extends RecyclerView.Adapter<AssestDisplayAdapter.MyViewHolder> {
    List<Asset> mList;
    Context mContext;
    MyCallbackForControl objMyCallbackForControl;

    public interface MyCallbackForControl {
        void onClickStatusUI(int position,String json);
    }


    public AssestDisplayAdapter(Context mContext, ArrayList<Asset> mList,MyCallbackForControl objMyCallbackForControl) {
        this.objMyCallbackForControl=objMyCallbackForControl;
        this.mList = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_asset, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Asset objBean = mList.get(position);


            holder.llClickableRaw.setVisibility(View.VISIBLE);

            holder.tvAssetKey.setText(objBean.getAttributeName());
            holder.tvAssetValueDisplay.setText(objBean.getSelected());

            holder.tvAssetValueDisplay.setVisibility(View.VISIBLE);
            holder.edtAssetValueEditText.setVisibility(View.GONE);
            holder.tvAssetValueEdit.setVisibility(View.GONE);

            if (objBean.getAttrKey().equals("notes")) {
                holder.tvAssetValueDisplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        objMyCallbackForControl.onClickStatusUI(position, objBean.getNote());
                    }
                });
            } else {
                holder.tvAssetValueDisplay.setOnClickListener(null);
            }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.llTitles)
        LinearLayout llTitles;

        @BindView(R.id.edtAssetValueEditText)
        EditText edtAssetValueEditText;

        @BindView(R.id.tvAssetValueDisplay)
        TextView tvAssetValueDisplay;

        @BindView(R.id.tvAssetValueEdit)
        TextView tvAssetValueEdit;

        @BindView(R.id.tvAssetKey)
        TextView tvAssetKey;

        @BindView(R.id.llClickableRaw)
        LinearLayout llClickableRaw;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}