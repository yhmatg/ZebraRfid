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

public class EpcBeanAdapter extends RecyclerView.Adapter<EpcBeanAdapter.ViewHolder>{
    private List<EpcBean> mEpcBeans;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public EpcBeanAdapter(List<EpcBean> epcBeans, Context mContext) {
        this.mEpcBeans = epcBeans;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.epc_adapter_item, viewGroup, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final EpcBean epcBean = mEpcBeans.get(i);
        viewHolder.epcData.setText(epcBean.getEpc());
        if(epcBean.isInved()){
            viewHolder.status.setText("正常");
            viewHolder.status.setTextColor(mContext.getResources().getColor(R.color.blue));
        }else{
            viewHolder.status.setText("未盘");
            viewHolder.status.setTextColor(mContext.getResources().getColor(R.color.red));
        }
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
        public TextView status;
        public LinearLayout fileLayout;
        public ViewHolder(@NonNull View view) {
            super(view);
            epcData = (TextView)view.findViewById(R.id.epc_data);
            status = (TextView)view.findViewById(R.id.inv_status);
            fileLayout = (LinearLayout)view.findViewById(R.id.file_layout);
        }
    }

    public interface OnItemClickListener {
        void onEpcItemClick(EpcBean fileBean);

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
