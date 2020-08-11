package com.zebra.rfidreader.nonghang.nongshanghang;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zebra.rfidreader.nonghang.R;

import java.util.List;

public class EpcPointBeanAdapter extends RecyclerView.Adapter<EpcPointBeanAdapter.ViewHolder>{
    private List<String> mEpcBeans;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public EpcPointBeanAdapter(List<String> epcBeans, Context mContext) {
        this.mEpcBeans = epcBeans;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.epc_point_item, viewGroup, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final String epcBean = mEpcBeans.get(i);
        viewHolder.epcData.setText(epcBean);
        viewHolder.fileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onEpcItemClick(epcBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEpcBeans == null ? 0: mEpcBeans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView epcData;
        public LinearLayout fileLayout;
        public ViewHolder(@NonNull View view) {
            super(view);
            epcData = (TextView)view.findViewById(R.id.epc_data);
            fileLayout = (LinearLayout)view.findViewById(R.id.file_layout);
        }
    }

    public interface OnItemClickListener {
        void onEpcItemClick(String fileBean);

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
