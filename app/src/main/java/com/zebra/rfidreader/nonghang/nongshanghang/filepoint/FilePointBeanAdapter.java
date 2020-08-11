package com.zebra.rfidreader.nonghang.nongshanghang.filepoint;

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

public class FilePointBeanAdapter extends RecyclerView.Adapter<FilePointBeanAdapter.ViewHolder>{
    private List<FilePointBean> mAssetsInfos;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public FilePointBeanAdapter(List<FilePointBean> mAssetsInfos, Context mContext) {
        this.mAssetsInfos = mAssetsInfos;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.file_point_item, viewGroup, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final FilePointBean fileBean = mAssetsInfos.get(i);
        viewHolder.bagCode.setText(fileBean.getBagCode());
        viewHolder.fileNum.setText(String.valueOf(fileBean.getEpcs().size()));
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
        public LinearLayout fileLayout;
        public ViewHolder(@NonNull View view) {
            super(view);
            bagCode = (TextView)view.findViewById(R.id.bag_code);
            fileNum = (TextView)view.findViewById(R.id.file_num);
            fileLayout = (LinearLayout)view.findViewById(R.id.file_layout);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(FilePointBean fileBean);

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
