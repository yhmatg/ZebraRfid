package com.zebra.rfidreader.nonghang.nongshanghang;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.zebra.rfidreader.nonghang.R;

import java.util.List;

public class SingleSelectAdapter extends RecyclerView.Adapter<SingleSelectAdapter.MyHoder> {
    private List<BoxBean> boxBeans;
    private  Context mContext;
    private BoxBean selectedBoxBean;

    public SingleSelectAdapter(List<BoxBean> boxBeans, Context mContext) {
        this.boxBeans = boxBeans;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyHoder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.tree_adapter_item, viewGroup, false);
        return new MyHoder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHoder myHoder, int i) {
        final BoxBean boxBean = boxBeans.get(i);
        myHoder.label.setText(boxBean.getBoxName());
        myHoder.cb.setChecked(boxBean.isChecked());
        myHoder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedBoxBean != null){
                    selectedBoxBean.setChecked(false);
                }
                if(myHoder.cb.isChecked()){
                    boxBean.setChecked(true);
                    selectedBoxBean = boxBean;
                }else {
                    boxBean.setChecked(false);
                }
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return boxBeans == null ? 0 : boxBeans.size();
    }

    class MyHoder extends RecyclerView.ViewHolder{

        public CheckBox cb;

        public TextView label;

        public ImageView icon;
        public MyHoder(View itemView) {
            super(itemView);

            cb = (CheckBox) itemView
                    .findViewById(R.id.cb_select_tree);
            label = (TextView) itemView
                    .findViewById(R.id.id_treenode_label);
            icon = (ImageView) itemView.findViewById(R.id.icon);

        }
    }

    public BoxBean getSelectedBoxBean() {
        return selectedBoxBean;
    }
}
