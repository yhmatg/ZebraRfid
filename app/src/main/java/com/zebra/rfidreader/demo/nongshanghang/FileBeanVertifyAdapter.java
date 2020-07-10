package com.zebra.rfidreader.demo.nongshanghang;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zebra.rfidreader.demo.R;

import java.util.List;

public class FileBeanVertifyAdapter extends RecyclerView.Adapter<FileBeanVertifyAdapter.ViewHolder>{
    private List<FileBean> mAssetsInfos;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public FileBeanVertifyAdapter(List<FileBean> mAssetsInfos, Context mContext) {
        this.mAssetsInfos = mAssetsInfos;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.file_adapter_item, viewGroup, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final FileBean fileBean = mAssetsInfos.get(i);
        viewHolder.bagCode.setText(fileBean.getBagCode());
        viewHolder.fileNum.setText(fileBean.getFileNumber());
        viewHolder.boxCode.setText(fileBean.getBoxCode());
        int invStatus = 1;
        for (EpcBean epc : fileBean.getEpcs()) {
            if(!epc.isInved()){
                invStatus = 0;
                break;
            }
        }
        if(invStatus == 0){
           viewHolder.status.setText("缺失");
            viewHolder.status.setTextColor(mContext.getResources().getColor(R.color.red));
        }else{
            viewHolder.status.setText("正常");
            viewHolder.status.setTextColor(mContext.getResources().getColor(R.color.blue));
        }
        viewHolder.fileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(fileBean);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mAssetsInfos == null ? 0: mAssetsInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView bagCode;
        public TextView fileNum;
        public TextView boxCode;
        public TextView status;
        public LinearLayout fileLayout;
        public ViewHolder(@NonNull View view) {
            super(view);
            bagCode = (TextView)view.findViewById(R.id.bag_code);
            fileNum = (TextView)view.findViewById(R.id.file_num);
            boxCode = (TextView)view.findViewById(R.id.box_coe);
            status = (TextView)view.findViewById(R.id.inv_status);
            fileLayout = (LinearLayout)view.findViewById(R.id.file_layout);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(FileBean fileBean);

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
