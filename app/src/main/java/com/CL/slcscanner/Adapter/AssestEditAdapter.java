package com.CL.slcscanner.Adapter;

import android.content.Context;
import android.content.SharedPreferences;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.CL.slcscanner.Pojo.ClientAssets.Datum;
import com.CL.slcscanner.R;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.Util;
import com.google.android.gms.common.api.Api;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vrajesh on 3/3/2018.
 */

public class AssestEditAdapter extends RecyclerView.Adapter<AssestEditAdapter.MyViewHolder> {

    public interface MyCallbackForControl {
        void onClickForControl(int position, com.CL.slcscanner.Pojo.ClientAssets.Datum objSlcsBean);
    }

    public interface MyCallbackPoleOption {
        void onClickForControl(int position, boolean from, String notes);
    }

    List<Datum> mList;
    private MyCallbackForControl objMyCallbackForControl;
    private MyCallbackPoleOption objMyCallbackPoleOption;
    Context mContext;
    SharedPreferences spf;
    boolean localUpdate = false;

    public AssestEditAdapter(Context mContext, List<Datum> mList, MyCallbackForControl objMyCallbackForControl, SharedPreferences spf, MyCallbackPoleOption objMyCallbackPoleOption, boolean localUpdate) {
        this.mList = mList;
        this.objMyCallbackForControl = objMyCallbackForControl;
        this.mContext = mContext;
        this.spf = spf;
        this.objMyCallbackPoleOption = objMyCallbackPoleOption;
        this.localUpdate = localUpdate;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_asset, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Datum objBean = mList.get(holder.getAdapterPosition());
        boolean isSelctionNull = false;

        holder.llClickableRaw.setVisibility(View.VISIBLE);

        holder.tvAssetValueEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objMyCallbackForControl.onClickForControl(holder.getAdapterPosition(), mList.get(holder.getAdapterPosition()));
            }
        });

        holder.tvAssetKey.setText(objBean.getAttributeName());
        holder.tvAssetValueDisplay.setVisibility(View.GONE);

        if (objBean.getSelectected() == null)
            isSelctionNull = true;
        else if (objBean.getSelectected().toString().equalsIgnoreCase(""))
            isSelctionNull = true;

        if (mList.get(holder.getAdapterPosition()).getAttributeName().
                equalsIgnoreCase(mContext.getResources().getString(R.string.attribute_pole_id))) {

            holder.tvAssetValueDisplay.setVisibility(View.GONE);
            holder.tvAssetValueEdit.setVisibility(View.GONE);
            holder.edtAssetValueEditText.setVisibility(View.VISIBLE);


            if (isSelctionNull)
                holder.edtAssetValueEditText.setHint(objBean.getBtnText());
            else if (objBean.getSelectected().toString().equalsIgnoreCase("None") || objBean.getSelectected().toString().equalsIgnoreCase(mContext.getString(R.string.none)))
                holder.edtAssetValueEditText.setHint(objBean.getSelectected());
            else
                holder.edtAssetValueEditText.setText(objBean.getSelectected());

            holder.tvAssetKey.setTextSize(16);

            if (mList.get(holder.getAdapterPosition()).getAttributeName().equalsIgnoreCase(mContext.getResources().getString(R.string.attribute_pole_id))) {
                holder.edtAssetValueEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            }

            /*if (mList.get(holder.getAdapterPosition()).getAttributeName().equalsIgnoreCase(AppConstants.attribSLCID)) {
                holder.edtAssetValueEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
                else
                    holder.edtAssetValueEditText.setInputType(InputType.TYPE_CLASS_TEXT);*/


        } else if (mList.get(holder.getAdapterPosition()).getAttributeName()
                .equalsIgnoreCase(mContext.getResources().getString(R.string.attribute_address))) {
            holder.tvAssetValueDisplay.setVisibility(View.VISIBLE);
            holder.tvAssetValueEdit.setVisibility(View.GONE);
            holder.edtAssetValueEditText.setVisibility(View.GONE);

            holder.tvAssetValueDisplay.setTextColor(mContext.getResources().getColor(R.color.colorAccent));

            if (isSelctionNull)
                holder.tvAssetValueDisplay.setText(objBean.getBtnText());
            else
                holder.tvAssetValueDisplay.setText(objBean.getSelectected());

        } else {
            if (objBean.getAttrKey().equals("notes")) {
                holder.tvAssetValueEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        objMyCallbackPoleOption.onClickForControl(position, true, objBean.getNote());
                    }
                });
            }

            holder.tvAssetValueDisplay.setVisibility(View.GONE);
            holder.tvAssetValueEdit.setVisibility(View.VISIBLE);
            holder.edtAssetValueEditText.setVisibility(View.GONE);

            if (isSelctionNull)
                holder.tvAssetValueEdit.setText(objBean.getBtnText());
            else
                holder.tvAssetValueEdit.setText(objBean.getSelectected());

            if (mList.get(holder.getAdapterPosition()).getAttributeName().equalsIgnoreCase(spf.getString(AppConstants.MACID_LABLE, ""))
                    ||
                    mList.get(holder.getAdapterPosition()).getAttributeName().equalsIgnoreCase(mContext.getResources().getString(R.string.attribute_slc_id))) {

                if (!mList.get(position).isClickable()) {
                    //holder.tvAssetValueEdit.setEnabled(false);
                    //Toast.makeText(mContext,"false",Toast.LENGTH_SHORT).show();
                } else {
                    //holder.tvAssetValueEdit.setEnabled(true);
                    //Toast.makeText(mContext,"true",Toast.LENGTH_SHORT).show();
                }

                holder.tvAssetKey.setTextSize(16);
            }



          /*  if (mList.get(holder.getAdapterPosition()).getAttributeName().equalsIgnoreCase(mContext.getResources().getString(R.string.attribute_slc_id))) {
                if (!mList.get(position).isClickable()) {
                    holder.tvAssetValueEdit.setEnabled(false);
                    //Toast.makeText(mContext,"false",Toast.LENGTH_SHORT).show();
                }else {
                    holder.tvAssetValueEdit.setEnabled(true);
                    //Toast.makeText(mContext,"true",Toast.LENGTH_SHORT).show();
                }
            }

            if (mList.get(holder.getAdapterPosition()).getAttrKey().toString().equalsIgnoreCase("mac_address")) {

                if (!mList.get(position).isClickable()) {
                    holder.tvAssetValueEdit.setEnabled(false);
                    Toast.makeText(mContext,"false",Toast.LENGTH_SHORT).show();
                }
                else {
                    holder.tvAssetValueEdit.setEnabled(true);
                    Toast.makeText(mContext,"true",Toast.LENGTH_SHORT).show();
                }
            }
*/
        }

      /*if(localUpdate)
            holder.tvAssetValueEdit.setText(objBean.getSelectected());
        else
            holder.tvAssetValueEdit.setText(objBean.getBtnText());
      */

        holder.edtAssetValueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("---", "before" + s + " " + s.length());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("---", "onTextChanged" + s + " " + s.length() + " count" + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("---", "afterTextChanged" + s + " " + s.length());
                objBean.setBtnText(s.toString());
                objBean.setSelectected(s.toString());

                SharedPreferences.Editor editor = spf.edit();
                editor.putString("BtnText" + position, s.toString());
                editor.putString("Selectected" + position, s.toString());
                editor.apply();

            }
        });


    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgvScan)
        ImageView imgvScan;

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