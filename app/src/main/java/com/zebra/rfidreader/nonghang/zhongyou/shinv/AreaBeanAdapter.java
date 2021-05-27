package com.zebra.rfidreader.nonghang.zhongyou.shinv;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zebra.rfidreader.nonghang.R;
import com.zebra.rfidreader.nonghang.zhongyou.model.InvBean;

import java.util.List;

public class AreaBeanAdapter extends RecyclerView.Adapter<AreaBeanAdapter.ViewHolder>{
    private List<InvBean> mAssetsInfos;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public AreaBeanAdapter(List<InvBean> mAssetsInfos, Context mContext) {
        this.mAssetsInfos = mAssetsInfos;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.area_adapter_item, viewGroup, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final InvBean invBean = mAssetsInfos.get(i);
        viewHolder.epcCode.setText(invBean.getEpcCode());
        viewHolder.areaName.setText(invBean.getAreaName());
    }

    @Override
    public int getItemCount() {
        return mAssetsInfos == null ? 0: mAssetsInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView epcCode;
        public TextView areaName;
       
        public ViewHolder(@NonNull View view) {
            super(view);
            epcCode = (TextView)view.findViewById(R.id.epc_code);
            areaName = (TextView)view.findViewById(R.id.area_name);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(InvBean invBean);

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
