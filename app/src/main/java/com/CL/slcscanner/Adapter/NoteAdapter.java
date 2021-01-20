package com.CL.slcscanner.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.CL.slcscanner.Pojo.Note.Datum;
import com.CL.slcscanner.R;
import com.CL.slcscanner.Utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vrajesh on 3/3/2018.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {
    List<Datum> mList;
    Context mContext;
    Boolean isViewOnly;

    public NoteAdapter(Context mContext, ArrayList<Datum> mList,boolean isViewOnly) {
        this.mList = mList;
        this.mContext = mContext;
        this.isViewOnly=isViewOnly;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_note, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Datum objBean = mList.get(position);

        holder.tvNoteLable.setText(objBean.getValue());
        holder.edtNote.setText(objBean.getNotesEach());

        holder.cbNote.setOnCheckedChangeListener(null);
        //holder.edtNote.addTextChangedListener(null);
        holder.cbNote.setChecked(mList.get(position).isChecked());
        //holder.edtNote.setText(mList.get(position).getNotesEach());
        //mList.get(position).setNotesEach("");

        if(mList.get(position).isChecked())
            holder.edtNote.setVisibility(View.VISIBLE);
        else
            holder.edtNote.setVisibility(View.GONE);

        if(isViewOnly){
            holder.cbNote.setOnCheckedChangeListener(null);
            holder.edtNote.setFocusable(false);
            holder.cbNote.setEnabled(false);
        }else{
            holder.edtNote.setEnabled(true);
            holder.cbNote.setEnabled(true);
        }

       /* holder.edtNote.setFocusable(false);*/
        holder.cbNote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Integer pos =position;//= (Integer) holder.cbNote.getTag();
                Log.d(AppConstants.TAG,mList.get(pos).getKey() + " clicked!");
                if (mList.get(pos).isChecked()) {
                    mList.get(pos).setChecked(false);
                    holder.cbNote.setChecked(false);
                    holder.edtNote.setVisibility(View.GONE);
                } else {
                    mList.get(pos).setChecked(true);
                    holder.cbNote.setChecked(true);
                    holder.edtNote.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.edtNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Integer pos =position;
                    if(mList.get(pos).isChecked()) {
                        mList.get(pos).setNotesEach(editable.toString());
                    }else{
                        mList.get(pos).setNotesEach("");
                    }
                    Log.d(AppConstants.TAG,mList.get(pos).getKey()+" : Text: "+editable.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.edtNote)
        EditText edtNote;

        @BindView(R.id.tvNoteLable)
        TextView tvNoteLable;

        @BindView(R.id.cbNote)
        CheckBox cbNote;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}