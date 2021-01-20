package com.CL.slcscanner.Adapter;

import android.content.Context;
import android.content.SharedPreferences;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.CL.slcscanner.Pojo.PoleMaster.Datum;
import com.CL.slcscanner.R;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.MyCallbackForControl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vrajesh on 3/3/2018.
 */

public class PoleAdapter extends RecyclerView.Adapter<PoleAdapter.MyViewHolder> {

    List<com.CL.slcscanner.Pojo.ListResponse.List> mList;
    List<com.CL.slcscanner.Pojo.ListResponse.List> mFilterList;
    private MyCallbackForControl objMyCallbackForControl;
    Context mContext;

    String isPoleDetailDisplay;
    SharedPreferences spf;

    /*  public PoleAdapter(Context mContext, List<Datum> mList, MyCallbackForControl objMyCallbackForControl,String isPoleDetailDisplay) {
          this.mList = mList;
          this.objMyCallbackForControl = objMyCallbackForControl;
          this.mContext = mContext;
          mFilterList = mList;
          this.isPoleDetailDisplay=isPoleDetailDisplay;
          spf=mContext.getSharedPreferences(AppConstants.SPF,Context.MODE_PRIVATE);
      }
  */
    public PoleAdapter(Context mContext, List<com.CL.slcscanner.Pojo.ListResponse.List> mList, MyCallbackForControl objMyCallbackForControl, String isPoleDetailDisplay) {
        this.mList = mList;
        this.objMyCallbackForControl = objMyCallbackForControl;
        this.mContext = mContext;
        mFilterList = mList;
        this.isPoleDetailDisplay = isPoleDetailDisplay;
        spf = mContext.getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_with_alignment, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final com.CL.slcscanner.Pojo.ListResponse.List objBean = mFilterList.get(position);

        isPoleDetailDisplay = spf.getString(AppConstants.SPF_CLIENT_SLC_LIST_VIEW, "Yes");
        holder.tvTitle1.setText(objBean.getSlcId());

        holder.tvTitle3.setText(objBean.getCreated());
        holder.tvTitle4.setText(objBean.getAddress());

        String isPoleCompulsary = spf.getString(AppConstants.SPF_CLIENT_SLC_POLE_ID, "No");
        if(isPoleCompulsary.toString().equalsIgnoreCase("yes")) {
            holder.llPoleId.setVisibility(View.VISIBLE);
            holder.tvTitle2.setText(objBean.getPoleId());
        }else{
            holder.llPoleId.setVisibility(View.GONE);
        }

        if (isPoleDetailDisplay.equalsIgnoreCase("Yes")) {
            holder.ivGatwaySatus.setVisibility(View.VISIBLE);
        } else {
            holder.ivGatwaySatus.setVisibility(View.GONE);
        }

        holder.llTitles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objMyCallbackForControl.onClickForControl(holder.getAdapterPosition(), mFilterList.get(holder.getAdapterPosition()), true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle1)
        TextView tvTitle1;

        @BindView(R.id.tvTitle2)
        TextView tvTitle2;

        @BindView(R.id.tvTitle3)
        TextView tvTitle3;

        @BindView(R.id.tvTitle4)
        TextView tvTitle4;

        @BindView(R.id.ivGatwaySatus)
        ImageView ivGatwaySatus;

        @BindView(R.id.llTitles)
        LinearLayout llTitles;

        @BindView(R.id.llClickableRaw)
        LinearLayout llClickableRaw;

        @BindView(R.id.llPoleId)
        LinearLayout llPoleId;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

   /* public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilterList = mList;
                } else {
                    List<Datum> filteredList = new ArrayList<>();
                    for ( Datum row : mList) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match

                        if (row.getSlcId().toLowerCase().contains(charSequence)
                                || row.getPoleId().contains(charSequence)
                                || row.getMacAddress().contains(charSequence)
                                ) {
                            filteredList.add(row);
                        }
                    }
                    mFilterList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilterList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilterList = (ArrayList<Datum>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }*/
